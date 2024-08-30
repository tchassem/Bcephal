package com.moriset.bcephal.sourcing.grid.controller.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleExportData;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.service.ExportGrilleRunner;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderSource;
import com.moriset.bcephal.loader.domain.api.FileLoaderLogResponse;
import com.moriset.bcephal.loader.repository.api.FileLoaderLogResponseRepository;
import com.moriset.bcephal.loader.service.FileLoaderApiService;
import com.moriset.bcephal.loader.service.FileLoaderProperties;
import com.moriset.bcephal.loader.service.FileLoaderRunData;
import com.moriset.bcephal.loader.service.FileLoaderService;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.security.domain.UserData;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.utils.ApiCodeGenerator;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping( value = "/sourcing-api")
@Slf4j
public class ApiSourcingController {

	@Autowired
	GrilleService grilleService;
	
	@Autowired
	InitiationService initiationService;

	@Autowired
	FileLoaderService loaderService;
	
	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;

	@Autowired
	UserDataRepository userDataRepository;
	
	@Autowired
	ExportGrilleRunner exportGrilleRunner;
	
	@Autowired
	FileLoaderApiService fileLoaderApiService;
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;
	
	@Autowired
	FileLoaderLogResponseRepository logRepository;
	
	@Autowired
	FileLoaderProperties properties;

	@Value("${bcephal.file.loader.in-dir}")
	protected String loaderDir;
	

	@PostMapping(value = "/run-file-loader", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> loadData(
    		HttpSession session,
    		JwtAuthenticationToken principal,
    		@RequestHeader(RequestParams.AUTHORIZATION) String auth,
    		@RequestHeader(RequestParams.LANGUAGE) String locale,
    		@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
    		@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
    		@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
    		@RequestHeader(RequestParams.OPERATION_NAME) String operationName,
    		@RequestParam(name = "file", required = false) MultipartFile file
		) {		
		log.debug("Api sourcing data loading ....");
		
		try {
			List<FileLoader> loaders = loaderService.getAllByName(operationName);
			if(loaders.size() == 0) {
				throw new BcephalException(String.format("File loader not found : %s", operationName));
			}
			if(loaders.size() > 1) {
				throw new BcephalException(String.format("There are more than one file loader named : %s", operationName));
			}
			FileLoader loader = loaders.get(0);
			log.debug("File loader found : {}", loader.getName());
			
					
			log.trace("API code generation...");
			String code = ApiCodeGenerator.generate("API_" + ApiCodeGenerator.FILE_LOADER_PREFIX);
			log.debug("API code generated : {}", code);
			
			String username = principal.getName();
			
			log.trace("Build FileLoaderRunData...");
			FileLoaderRunData data = new FileLoaderRunData();
			data.setMode(RunModes.M);
			data.setOperationCode(code);
			data.setLoader(loader);
			data.getLoader().getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(data.getLoader().getId(), FileLoader.class.getName()));
			data.getLoader().sortRoutines();
			
			data.setRepositories(new ArrayList<>());
			for(com.moriset.bcephal.loader.domain.FileLoaderRepository repo : data.getLoader().getRepositories()) {
				if (StringUtils.hasText(repo.getRepositoryOnServer())) {
					Path path = Paths.get(properties.getInDir(), repo.getRepositoryOnServer());
					data.getRepositories().add(path.toString());
				}
			}
			if(data.getRepositories().isEmpty()) {
				if (StringUtils.hasText(properties.getInDir())) {
					Path path = Paths.get(properties.getInDir());
					data.getRepositories().add(path.toString());
				}
			}
			
			if(file != null) {
				String folder = data.getRepositories().get(0);
				Path path = Paths.get(folder, file.getOriginalFilename());
				file.transferTo(path);
				
				if(data.getLoader().getSource() == FileLoaderSource.LOCAL) {
					data.setFiles(new ArrayList<>());
					data.getFiles().add(file.getOriginalFilename());
				}	
			}
			
			fileLoaderApiService.load(data, username, session, TenantContext.getCurrentTenant(), new TaskProgressListener() {				
				@Override
				public void SendInfo() { }
			});
			
			return ResponseEntity.ok(code);
		}
		catch (HttpClientErrorException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (BcephalException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@PostMapping(value = "/run-loader", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> loadData(
    		HttpSession session,
    		JwtAuthenticationToken principal,
    		@RequestHeader(RequestParams.AUTHORIZATION) String auth,
    		@RequestHeader(RequestParams.LANGUAGE) String locale,
    		@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
    		@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
    		@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
    		@RequestHeader(RequestParams.OPERATION_NAME) String operationName) {		
		log.debug("Api sourcing data loading ....");
		
		try {
			List<FileLoader> loaders = loaderService.getAllByName(operationName);
			if(loaders.size() == 0) {
				throw new BcephalException(String.format("File loader not found : %s", operationName));
			}
			if(loaders.size() > 1) {
				throw new BcephalException(String.format("There are more than one file loader named : %s", operationName));
			}
			FileLoader loader = loaders.get(0);
			log.debug("File loader found : {}", loader.getName());
			
					
			log.trace("API code generation...");
			String code = ApiCodeGenerator.generate("API_" + ApiCodeGenerator.FILE_LOADER_PREFIX);
			log.debug("API code generated : {}", code);
			
			String username = principal.getName();
			
			
			log.trace("Build FileLoaderRunData...");
			FileLoaderRunData data = new FileLoaderRunData();
			data.setMode(RunModes.M);
			data.setOperationCode(code);
			data.setLoader(loader);
			data.getLoader().getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(data.getLoader().getId(), FileLoader.class.getName()));
			data.getLoader().sortRoutines();
			
			data.setRepositories(new ArrayList<>());
			for(com.moriset.bcephal.loader.domain.FileLoaderRepository repo : data.getLoader().getRepositories()) {
				if (StringUtils.hasText(repo.getRepositoryOnServer())) {
					Path path = Paths.get(properties.getInDir(), repo.getRepositoryOnServer());
					data.getRepositories().add(path.toString());
				}
			}
			if(data.getRepositories().isEmpty()) {
				if (StringUtils.hasText(properties.getInDir())) {
					Path path = Paths.get(properties.getInDir());
					data.getRepositories().add(path.toString());
				}
			}
			
			fileLoaderApiService.load(data, username, session, TenantContext.getCurrentTenant(), new TaskProgressListener() {				
				@Override
				public void SendInfo() { }
			});
			
			return ResponseEntity.ok(code);
		}
		catch (HttpClientErrorException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (BcephalException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
	}

    @PostMapping(value = "/transfer-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> transferFiles(
    		HttpSession session,
    		JwtAuthenticationToken principal,
    		@RequestHeader(RequestParams.AUTHORIZATION) String auth,
    		@RequestHeader(RequestParams.LANGUAGE) String locale,
    		@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
    		@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
    		@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
    		@RequestHeader(RequestParams.OPERATION_NAME) String operationName,
    		@RequestParam("file") MultipartFile file,
    		@RequestParam("foldername") String foldername
	) {

    	log.debug("Api sourcing files transfering ....");
		if (file == null || !StringUtils.hasText(file.getOriginalFilename()) || foldername == null || !StringUtils.hasText(foldername)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must both select a file and enter a folder name for uploading");
		}
    	
		String path = null;
		try {
        	String filename = file.getOriginalFilename();
        	path = FilenameUtils.concat(loaderDir, foldername);
    		if (!new File(path).exists()) {
    			new File(path).mkdirs();
    		}
    		
    		log.trace("Original file name: {}", filename);
    		path = FilenameUtils.concat(path, filename);
    		File destination = new File(path);
    		log.trace("Destination directory name: {}", destination.toString());

            OutputStream stream = new FileOutputStream(destination);
            stream.write(file.getBytes());
            stream.close();

        } catch (Exception e) {
            log.debug(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
		
    	log.debug("Api sourcing files upload complete.");
        return ResponseEntity.status(HttpStatus.OK).body(path);
    }

    @PostMapping(value = "/export-grid")
    public ResponseEntity<?> exportGrid(
    		JwtAuthenticationToken principal,
    		@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
    		@RequestHeader(RequestParams.LANGUAGE) String locale,
    		@RequestHeader("grid-name") String gridName,
    		@RequestHeader("file-type") String fileType,
    		@RequestHeader(name="operation-code", required=false) String code,
    		@RequestBody(required = false) List<ApiFilter> filters) {

    	log.debug("Api sourcing file export ....");
    	try {
			if (!StringUtils.hasText(gridName) || !StringUtils.hasText(fileType)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must both enter a grid name and type file to export.");
			}
			
			List<Grille> grilles = grilleService.getAllByName(gridName);
			if(grilles.size() == 0) {
				throw new BcephalException(String.format("Grid not found : %s", gridName));
			}
			if(grilles.size() > 1) {
				throw new BcephalException(String.format("There are more than one grid named : %s", gridName));
			}
			Grille grid = grilles.get(0);
			log.debug("Grid found : {}", grid.getName());
	
			GrilleExportData data = new GrilleExportData();
			data.setType(GrilleExportDataType.valueOf(fileType));
			GrilleDataFilter filter = new GrilleDataFilter();
			filter.setClientId(clientId);
			filter.setGrid(grid);
			filter.setDataSourceId(grid.getDataSourceId());
			filter.setDataSourceType(grid.getDataSourceType());
			Optional<UserData> user = userDataRepository.findByUsername(principal.getName());
			filter.setUserId(!user.isEmpty() ? user.get().getId() : null);
			filter.setUsername(principal.getName());
			
			if(filters != null && !filters.isEmpty()) {
				UniverseFilter universeFilter = buildFilter(filters, grid);
				if(universeFilter != null) {
					filter.setFilter(universeFilter);
				}
			}
			
			data.setFilter(filter);
			List<String> paths = exportGrilleRunner.export(data);
			
//			if(paths.size() > 0) {
//				Path path = Paths.get(paths.get(0));
//				org.springframework.core.io.UrlResource resource = new org.springframework.core.io.UrlResource(path.toUri());
//				return ResponseEntity.ok()
//						.contentType(MediaType.MULTIPART_FORM_DATA)//MediaType.parseMediaType(MediaType.MULTIPART_FORM_DATA))
//						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//						.body(resource);
//			}
						
	    	log.debug("Api sourcing file export complete.");
	        return ResponseEntity.ok(paths);
    	}catch (HttpClientErrorException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (BcephalException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
    }
    
    private UniverseFilter buildFilter(List<ApiFilter> filters, Grille grid) {
    	int position = 1;
    	for(ApiFilter f : filters) {
    		validate(f, position++, grid);
    	}
    	
    	UniverseFilter filter = new UniverseFilter();
    	filter.setAttributeFilter(buildAttributeFilter(filters, grid.getDataSourceType(), grid.getDataSourceId()));
    	filter.setMeasureFilter(buildMeasureFilter(filters, grid.getDataSourceType(), grid.getDataSourceId()));
    	filter.setPeriodFilter(buildPeriodFilter(filters, grid.getDataSourceType(), grid.getDataSourceId()));
		return filter;
	}
    
    private void validate(ApiFilter filter, int position,  Grille grid) {
    	if(!StringUtils.hasText(filter.getDimensionName())) {
    		throw new BcephalException("Wrong filter at position (" + position + ") : Dimension name can't be empty!");
		}
    	if(grid.getDataSourceType() == DataSourceType.MATERIALIZED_GRID) {
    		Optional<SmartMaterializedGridColumn> response = smartMaterializedGridRepository.findColumnByGridAndName(grid.getDataSourceId(), filter.getDimensionName());
    		if(response.isEmpty()) {
    			throw new BcephalException("Wrong filter at position (" + position + ") : Dimension not found '" + filter.getDimensionName() + "'");
    		}
    		Dimension dimension = new Attribute(response.get().getId(), response.get().getName());
    		filter.setDimension(dimension);
    	}
    	else {
	    	Dimension dimension = initiationService.getDimension(filter.getDimensionType(), filter.getDimensionName(), false, null, Locale.ENGLISH);
	    	if(dimension == null) {
	    		throw new BcephalException("Wrong filter at position (" + position + ") : Dimension not found '" + filter.getDimensionName() + "'");
	    	}
	    	filter.setDimension(dimension);
    	}
    	
    	if(!StringUtils.hasText(filter.getOperator())) {
    		filter.setOperator(filter.isAttribute() ? AttributeOperator.EQUALS.name() : "=");
		}
	}

	private AttributeFilter buildAttributeFilter(List<ApiFilter> filters, DataSourceType dataSourceType, Long dataSourceId) {
    	AttributeFilter filter = new AttributeFilter();
    	int position = 0;
    	for(ApiFilter f : filters) {
    		if(f.isAttribute()) {
    			if(StringUtils.hasText(f.getOperator())) {
    				f.setOperator(AttributeOperator.EQUALS.name());
    			}
    			AttributeFilterItem item = new AttributeFilterItem();    			
    			item.setDataSourceId(dataSourceId);
    			item.setDataSourceType(dataSourceType);
    			item.setDimensionType(DimensionType.ATTRIBUTE);
    			item.setFilterVerb(FilterVerb.AND);
    			item.setPosition(position++);
    			item.setDimensionId((Long)f.getDimension().getId());
    			item.setDimensionName(f.getDimension().getName());
    			item.setOperator(AttributeOperator.valueOf(f.getOperator()));
    			item.setValue(f.getStringValue());
    			filter.addItem(item);
    		}
    	}
		return filter;
	}
    
    private MeasureFilter buildMeasureFilter(List<ApiFilter> filters, DataSourceType dataSourceType, Long dataSourceId) {
    	MeasureFilter filter = new MeasureFilter();
    	int position = 0;
    	for(ApiFilter f : filters) {
    		if(f.isMeasure()) {
    			MeasureFilterItem item = new MeasureFilterItem();    			
    			item.setDataSourceId(dataSourceId);
    			item.setDataSourceType(dataSourceType);
    			item.setDimensionType(DimensionType.MEASURE);
    			item.setFilterVerb(FilterVerb.AND);
    			item.setPosition(position++);
    			item.setDimensionId((Long)f.getDimension().getId());
    			item.setDimensionName(f.getDimension().getName());
    			item.setOperator(f.getOperator());
    			item.setValue(f.getDecimalValue());
    			filter.addItem(item);
    		}
    	}
		return filter;
	}
    
    private PeriodFilter buildPeriodFilter(List<ApiFilter> filters, DataSourceType dataSourceType, Long dataSourceId) {
    	PeriodFilter filter = new PeriodFilter();
    	int position = 0;
    	for(ApiFilter f : filters) {
    		if(f.isPeriod()) {
    			PeriodFilterItem item = new PeriodFilterItem();    			
    			item.setDataSourceId(dataSourceId);
    			item.setDataSourceType(dataSourceType);
    			item.setDimensionType(DimensionType.PERIOD);
    			item.setFilterVerb(FilterVerb.AND);
    			item.setPosition(position++);
    			item.setDimensionId((Long)f.getDimension().getId());
    			item.setDimensionName(f.getDimension().getName());
    			item.setComparator(f.getOperator());
    			item.setValue(f.getDateValue());
    			filter.addItem(item);
    		}
    	}
		return filter;
	}

	@GetMapping("/status")
	public ResponseEntity<?> status(
    		@RequestHeader(RequestParams.AUTHORIZATION) String auth,
    		@RequestHeader(RequestParams.LANGUAGE) String locale,
    		@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
    		@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
    		@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
    		@RequestHeader(RequestParams.OPERATION_CODE) String operationCode
    		) {
		
		log.debug("Try to get status for scheduler by operation code : {}", operationCode);
		
		try {
			if(!StringUtils.hasText(operationCode)) {
				throw new BcephalException(String.format("Operation code is not provided!"));
			}
			List<FileLoaderLogResponse> logs = logRepository.findByOperationCode(operationCode);			
			log.debug("Log found : {}", logs.size());						
			return ResponseEntity.ok(logs);

		}catch (HttpClientErrorException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (BcephalException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
	}

}
