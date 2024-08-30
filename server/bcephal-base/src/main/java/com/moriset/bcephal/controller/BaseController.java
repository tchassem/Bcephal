package com.moriset.bcephal.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DocumentBrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.RightEditorData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.service.DocumentService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FileUtil;
import com.moriset.bcephal.utils.RequestParams;
import com.moriset.bcephal.utils.socket.WebSocketDataTransfert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseController<P extends MainObject, B> {

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected ObjectMapper mapper;
	
	@Autowired
	protected DocumentService documentService;

	@Value("${bcephal.project.temp-dir}")
	protected String tempDir;

	@GetMapping
	public ResponseEntity<?> hello() {
		return ResponseEntity.ok("B-CEPHAL : Sourcing REST controller.");
	}

	protected abstract MainObjectService<P, B> getService();

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get by ID : {}", id);
		try {
			P item = getService().getById(id);
			log.debug("Found : {}", item);
			return ResponseEntity.status(HttpStatus.OK).body(item);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving entity with id : {}", id, e);
			String message = messageSource.getMessage("unable.to.retieve.entity.by.id", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/by-name/{name}")
	public ResponseEntity<?> getByName(@PathVariable("name") String name,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get by ID : {}", name);
		try {
			P item = getService().getByName(name);
			log.debug("Found : {}", item);
			return ResponseEntity.status(HttpStatus.OK).body(item);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving entity with id : {}", name, e);
			String message = messageSource.getMessage("unable.to.retieve.entity.by.id", new Object[] { name }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	protected String getFunctionalityCode(Long entityId) {
		return getService().getFunctionalityCode();
	}

	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody P entity, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of save entity : {}", entity);
		try {
			boolean isNew = entity.getId() == null;
			getService().save(entity, locale);
			entity = getService().getById((Long) entity.getId());
			String rightLevel = isNew ? "CREATE" : "EDIT";
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), entity.getId(),getFunctionalityCode(entity.getId()) , rightLevel,profileId);
			log.debug("entity : {}", entity != null ? entity.getName() : null);
			return ResponseEntity.status(HttpStatus.OK).body(entity);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", entity, e);
			String message = messageSource.getMessage("unable.to.save.entity", new Object[] { entity }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/copy-item")
	public ResponseEntity<?> copy(@RequestBody Long id,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of copy : {}", id);
		try {
			Long result = getService().copy(id, locale);
			String rightLevel = "CREATE";
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), result, getFunctionalityCode(result), rightLevel, profileId);
			log.debug("New entity id : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while copying entity with id : {}", id, e);
			String message = messageSource.getMessage("unable.to.copy.entity.by.id", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/copy-items")
	public ResponseEntity<?> copy(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale,@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of copy : {}", ids);
		try {
			List<Long> results = getService().copy(ids, locale);
			String rightLevel = "CREATE";
			if(results != null) {
				results.forEach(result ->{
				getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), result, getFunctionalityCode(result), rightLevel, profileId);
				});
			}			
			log.debug("New entities ids : {}", results);
			return ResponseEntity.status(HttpStatus.OK).body(results);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while copying entities : {}", ids, e);
			String message = messageSource.getMessage("unable.to.copy.entities.by.id", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/saveas/{id}")
	public ResponseEntity<?> saveAs(
			@PathVariable("id") long objectId, 
			@RequestBody String newObjectName, 
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of save as: {}", objectId);
		try {
			Long result = getService().copy(objectId, newObjectName, locale);
			log.debug("save as: {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save as : {}", newObjectName, e);
			String message = messageSource.getMessage("unable.to.save.as.by.id", new Object[] { objectId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/can-delete")
	public ResponseEntity<?> canDelete(@RequestBody List<Long> oids, @RequestHeader("Accept-Language") java.util.Locale locale, @RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode, @RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId, JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of delete : {}", oids);
		try {			
			final boolean res[] = new boolean[] {true};
			if(oids != null) {
				oids.forEach(oid ->{
					res[0] = getService().canDelete(oid, locale);
				});
			}		
			return ResponseEntity.status(HttpStatus.OK).body(res[0]);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while checking deletion items : {}", oids, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> oids,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of delete : {}", oids);
		try {			
		     Map<Long, String> map = new LinkedHashMap<Long, String>();
			if(oids != null) {
				oids.forEach(result ->{
					map.put(result, getFunctionalityCode(result));
				});
			}
			getService().delete(oids, locale);
			String rightLevel = "DELETE";
			if(oids != null) {
				map.forEach((result, functionalityCode) ->{
				getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), result, functionalityCode, rightLevel,profileId);
			});
			}			
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting : {}", oids, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
			JwtAuthenticationToken principal, HttpSession session) {

		log.debug("Call of search");
		try {
			filter.setUsername(principal.getName());
			BrowserDataPage<B> page = getService().search(filter, locale, profileId, projectCode);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search entities by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.entity.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,
			@RequestHeader HttpHeaders headers, @RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
			@RequestHeader(name= RequestParams.BC_CLIENT,required = false ) Long clientId) {

		log.debug("Call of get editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			session.setAttribute(RequestParams.BC_PROFILE, profileId);
			session.setAttribute(RequestParams.BC_PROJECT, projectCode);
			session.setAttribute(RequestParams.BC_CLIENT, clientId);
			EditorData<P> data = getService().getEditorData(filter, session, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@PostMapping("/low-right-editor-data")
	public ResponseEntity<?> getLowRightEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,
			@RequestHeader HttpHeaders headers) {

		log.debug("Call of get right low editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			RightEditorData<P> data = getService().getRightLowLevelEditorData(filter, session, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving low right editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.low.right.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/upload/{folder}")
	public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file,
			@PathVariable("folder") String folder) throws Exception {
		log.debug("Call uploadData");
		if (file == null) {
			throw new RuntimeException("You must select the a file for uploading");
		}
		String path = saveFile(file, folder);
		return new ResponseEntity<String>(path, HttpStatus.OK);
	}

	protected String saveFile(MultipartFile file, String folder) throws Exception {
		String path = FilenameUtils.concat(tempDir, folder);
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}

		InputStream inputStream = file.getInputStream();
		String originalName = file.getOriginalFilename();
		String name = file.getName();
		String contentType = file.getContentType();
		long size = file.getSize();

		log.trace("inputStream: " + inputStream);
		log.trace("originalName: " + originalName);
		log.trace("name: " + name);
		log.trace("contentType: " + contentType);
		log.trace("size: " + size);

		//byte[] buffer = new byte[inputStream.available()];
		//inputStream.read(buffer);
		
		path = FilenameUtils.concat(path, originalName);
		File destination = new File(path);
		@SuppressWarnings("resource")
		OutputStream outStream = new FileOutputStream(destination);
		//outStream.write(buffer);
		FileUtil.copy(inputStream, outStream, 256);
		return path;
	}

	@PostMapping("/download")
	public ResponseEntity<?> downloadData(@RequestBody String path, HttpServletRequest request) throws Exception {
		log.debug("Call downloadData");

		File file = new File(path);
		if (!file.exists()) {
			throw new RuntimeException("File not found!");
		}

		@SuppressWarnings("resource")
		InputStream inputStream = new FileInputStream(file);
		byte[] isr = inputStream.readAllBytes();
		String fileName = file.getName();
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentLength(isr.length);
		// respHeaders.setContentType(new MediaType("text", "json"));
		respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		return new ResponseEntity<byte[]>(isr, respHeaders, HttpStatus.OK);

	}

	@PostMapping(path = "/upload-by-data/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadWithResume(@RequestPart("file") MultipartFile file) throws Exception {
		try {
			WebSocketDataTransfert dataTransfert = mapper.readValue(file.getBytes(), WebSocketDataTransfert.class);
			WebSocketDataTransfert reponseData = new WebSocketDataTransfert();
			reponseData.setName(dataTransfert.getName());
			if (dataTransfert.getDecision() != null && dataTransfert.getDecision().isNew()) {
				Path path2 = Paths.get(tempDir, dataTransfert.getFolder(),
						dataTransfert.getName());
				if (!path2.getParent().toFile().exists()) {
					path2.getParent().toFile().mkdirs();	
					try {
						path2.getParent().toFile().deleteOnExit();
						} catch (Exception e) {
						}
				}
				try {
					path2.toFile().deleteOnExit();
					} catch (Exception e) {
					}
				reponseData.setRemotePath(path2.getParent().normalize().toString());				
			}else {
				reponseData.setRemotePath(dataTransfert.getRemotePath());
			}
			if (dataTransfert.getData().length > 0) {
				Path path2 = Paths.get(reponseData.getRemotePath(), dataTransfert.getName());
				log.debug(" Download data from {}", path2.toString());
				FileUtils.writeByteArrayToFile(path2.toFile(), dataTransfert.getData(), true);
			}
			return ResponseEntity.ok(reponseData);
		} catch (IOException e) {
			log.error("io exception", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	@PostMapping("/search-docs")
	public ResponseEntity<?> searchDocs(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
			JwtAuthenticationToken principal, HttpSession session) {

		log.debug("Call of search");
		try {
			filter.setUsername(principal.getName());
			if(!StringUtils.hasText(filter.getSubjectType())) {
				filter.setSubjectType(getService().getFunctionalityCode());
			}
			BrowserDataPage<DocumentBrowserData> page = documentService.search(filter, locale, profileId, projectCode);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search documents by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.docs.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}
