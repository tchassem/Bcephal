package com.moriset.bcephal.api.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.api.domain.ApiFilter;
import com.moriset.bcephal.api.domain.RequestParams;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "B-Cephal operations", description = "The partner must use these api like entry point to used B-Cephal resources.")
@RestController
@RequestMapping(value = "/api/operations")
@Slf4j
public class ApiController {

	@Autowired
	private RestTemplate loadBalancedRestTemplate;

	@Value("${bcephal.project.temp-dir}")
	protected String tempDir;

	@Operation(operationId = "load-data", summary = "Load data", description = "Load data endpoint", responses = {
			@ApiResponse(description = "The operation code", responseCode = "200"),
			@ApiResponse(description = "Unauthorized", responseCode = "403") })
	@PostMapping(value = "/load-data", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> loadData(
			HttpSession session, 
    		@RequestHeader HttpHeaders httpHeaders,
			@Parameter(name = "operation-name", description = "File loader name", required = true) @RequestParam(name = "operation-name", required = true) String operationName,
			@Parameter(name = "file", description = "File to load", required = false) @RequestParam(name = "file", required = false) MultipartFile file)
			throws Exception {

		log.info("Call loadData...");
		try {
			
			HttpHeaders headers = buildHttpHeaders(session, httpHeaders, "");
			headers.set(RequestParams.OPERATION_NAME, operationName);
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<?> request = new HttpEntity<>(headers);
			File tempFile = null;
			if(file != null) {
				String path = Paths.get(tempDir, file.getOriginalFilename()).toString();
				tempFile = new File(path);
				file.transferTo(tempFile);
				
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
				body.add("file", new FileSystemResource(tempFile));				
				request = new HttpEntity<>(body, headers);
			}

			ResponseEntity<String> result = loadBalancedRestTemplate.exchange("lb://sourcing-grid-service/sourcing-api/run-file-loader", HttpMethod.POST, request, String.class);
			try {
				if(tempFile != null) {
					org.apache.tomcat.util.http.fileupload.FileUtils.forceDelete(tempFile);
				}
			} catch (IOException e) {
				log.debug("Unable to delete temp file: " + tempFile.getName(), e);
			}
			var response = result.getBody();
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			return handleExeption(e);
		}
	}

	@Operation(operationId = "run-file-loader", summary = "Run file loader", description = "Run file loader endpoint", responses = {
			@ApiResponse(description = "The operation code", responseCode = "200"),
			@ApiResponse(description = "Unauthorized", responseCode = "403") })
	@PostMapping(value = "/run-file-loader", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runFileLoader(
			HttpSession session, 
    		@RequestHeader HttpHeaders httpHeaders,
			@Parameter(name = "operation-name", description = "File loader name", required = true) @RequestParam(name = "operation-name", required = true) String operationName)
			throws Exception {

		log.info("Call FileLoaderRunner...");
		try {
			
			HttpHeaders headers = buildHttpHeaders(session, httpHeaders, "");
			headers.set(RequestParams.OPERATION_NAME, operationName);
			HttpEntity<?> request = new HttpEntity<>(headers);
			ResponseEntity<String> result = loadBalancedRestTemplate.exchange("lb://sourcing-grid-service/sourcing-api/run-loader", HttpMethod.POST, request, String.class);
			var response = result.getBody();
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			return handleExeption(e);
		}
	}
	
	@Operation(operationId = "run-job", summary = "Run a job", description = "Run job endpoint", responses = {
			@ApiResponse(description = "The operation code", responseCode = "200"),
			@ApiResponse(description = "Unauthorized", responseCode = "403") })
	@GetMapping(value = "/run-job", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runJob(
			HttpSession session, 
			@RequestHeader HttpHeaders httpHeaders,
			@Parameter(name = "operation-name", description = "Scheduler name") @RequestParam("operation-name") String operationName)
			throws Exception {

		log.info("Call runJob...");
		try {
			HttpHeaders headers = buildHttpHeaders(session, httpHeaders, operationName);
			HttpEntity<?> request = new HttpEntity<>(headers);
			ResponseEntity<String> result = loadBalancedRestTemplate.exchange("lb://scheduler-service/scheduler-api/run", HttpMethod.GET, request, String.class);
			var response = result.getBody();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return handleExeption(e);
		}
	}

	@Operation(operationId = "load-status", summary = "Get load status", description = "Load status endpoint", responses = {
			@ApiResponse(description = "The load status", responseCode = "200"),
			@ApiResponse(description = "Unauthorized", responseCode = "403") })
	@GetMapping(value = "/load-status", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loadStatus(
			HttpSession session, 
			@RequestHeader HttpHeaders httpHeaders,
			@Parameter(name = "operation-code", description = "Operation code") @RequestParam("operation-code") String operationCode)
			throws Exception {

		log.info("Call loadStatus...");
		try {
			HttpHeaders headers = buildHttpHeaders(session, httpHeaders, "");
			headers.set(RequestParams.OPERATION_CODE, operationCode);
			HttpEntity<?> request = new HttpEntity<>(headers);
			ResponseEntity<String> result = loadBalancedRestTemplate.exchange("lb://sourcing-grid-service/sourcing-api/status", HttpMethod.GET, request, String.class);
			var response = result.getBody();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return handleExeption(e);
		}
	}

	@Operation(operationId = "job-status", summary = "Get job status", description = "Job status endpoint", responses = {
			@ApiResponse(description = "The job status", responseCode = "200"),
			@ApiResponse(description = "Unauthorized", responseCode = "403") })
	@GetMapping(value = "/job-status", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> jobStatus(
			HttpSession session, 
			@RequestHeader HttpHeaders httpHeaders,
			@Parameter(name = "operation-code", description = "Operation code") @RequestParam("operation-code") String operationCode)
			throws Exception {

		log.info("Call jobStatus...");
		try {
			HttpHeaders headers = buildHttpHeaders(session, httpHeaders, "");
			headers.set(RequestParams.OPERATION_CODE, operationCode);
			HttpEntity<?> request = new HttpEntity<>(headers);
			ResponseEntity<String> result = loadBalancedRestTemplate.exchange("lb://scheduler-service/scheduler-api/status", HttpMethod.GET, request, String.class);
			var response = result.getBody();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return handleExeption(e);
		}
	}

	@Operation(operationId = "transfer-files", summary = "Transfer files", description = "Transfer files endpoint", responses = {
			@ApiResponse(description = "The operation code", responseCode = "200"),
			@ApiResponse(description = "Unauthorized", responseCode = "403") })
	@PostMapping(value = "/transfer-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> transferFiles(
			HttpSession session, 
			@RequestHeader HttpHeaders httpHeaders,
			@RequestParam("file") MultipartFile file, @RequestParam("foldername") String foldername) throws Exception {

		log.info("Call transfer-files ...");
		try {
			HttpHeaders headers = buildHttpHeaders(session, httpHeaders, "");
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			String path = Paths.get(tempDir, file.getOriginalFilename()).toString();
			File tempFile = new File(path);
			file.transferTo(tempFile);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
			body.add("file", new FileSystemResource(tempFile));
			body.add("foldername", foldername);

			HttpEntity<?> request = new HttpEntity<>(body, headers);
			ResponseEntity<String> result = loadBalancedRestTemplate.exchange("lb://sourcing-grid-service/sourcing-api/transfer-files", HttpMethod.POST, request, String.class);
			try {
				org.apache.tomcat.util.http.fileupload.FileUtils.forceDelete(tempFile);
			} catch (IOException e) {
				log.debug("Unable to delete temp file: " + tempFile.getName(), e);
			}
			return result;
		} 
		catch (Exception e) {
			return handleExeption(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Operation(operationId = "download-data", summary = "Download data", description = "Download data endpoint", responses = {
			@ApiResponse(description = "Data file", responseCode = "200"),
			@ApiResponse(description = "Unauthorized", responseCode = "403") })
	@PostMapping(value = "/download-data")
	@Produces("application/octet-stream")
	public ResponseEntity<?> downloadData(
			HttpSession session, 
			HttpServletResponse response,
			@RequestHeader HttpHeaders httpHeaders,
			@Parameter(name = "report-name", required = true, description = "The name of report to download") 
				@RequestParam(name = "report-name", required = true) String reportName, 
			@Parameter(name = "file-type", required = true, description = "The type of file to download : (JSON, CSV, XSL)") 
				@RequestParam(name = "file-type", required = true, defaultValue = "CSV") String fileType,
			@Parameter(name = "operation-code", required = false, description = "The operation code")
				@RequestParam(name = "operation-code", required = false) String operationCode,				
			@Parameter(name = "filters", required = false, description = "Filters")
				@RequestBody(required = false) List<ApiFilter> filters
			) {
		try {
			HttpHeaders headers = buildHttpHeaders(session, httpHeaders, "");
			HttpEntity<?> request = new HttpEntity<>(filters, headers);
	
			headers.set("grid-name", reportName);
			headers.set("file-type", fileType);
			if(StringUtils.hasText(operationCode)) {
				headers.set("operation-code", operationCode);
			}			
			
			ResponseEntity<List> result = loadBalancedRestTemplate.exchange("lb://sourcing-grid-service/sourcing-api/export-grid", HttpMethod.POST, request, List.class);
			List<String> paths = result.getBody();
			
//			if(paths.size() == 1) {
//				Path path = Paths.get(paths.get(0));
//				org.springframework.core.io.UrlResource resource = new org.springframework.core.io.UrlResource(path.toUri());
//				return ResponseEntity.ok()
//						.contentType(MediaType.MULTIPART_FORM_DATA)
//						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//						.body(resource);
//			}
			if(paths.size() > 0){
				
				if(paths.size() == 1){
					for (String path : paths) {
						File file = new File(path);
						InputStream inputStream = new FileInputStream(file);
						byte[] isr = inputStream.readAllBytes();
						
						inputStream.close();
						
						String fileName = file.getName();
						HttpHeaders respHeaders = new HttpHeaders();
						respHeaders.setContentLength(isr.length);
						
						respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
						respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
						return new ResponseEntity<byte[]>(isr, respHeaders, HttpStatus.OK);						
					}
				}
				else {
				
					ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
					for (String fileName : paths) {
						FileSystemResource resource = new FileSystemResource(fileName);
						ZipEntry zipEntry = new ZipEntry(resource.getFilename());
						zipEntry.setSize(resource.contentLength());
						zipOut.putNextEntry(zipEntry);
						StreamUtils.copy(resource.getInputStream(), zipOut);
						zipOut.closeEntry();
					}
					zipOut.finish();
					zipOut.close();
					
					try {
						for (String path : paths) {
							org.apache.tomcat.util.http.fileupload.FileUtils.forceDelete(new File(path));
						}
					} catch (Exception e) {
						log.debug("Unable to delete temp file", e);
					}
					
					return ResponseEntity.ok()
							//.contentType(MediaType.valueOf("application/zip"))
							.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportName + "\"")
							.header("File-Count", "" + paths.size())
							.body(zipOut);
				}
			}

			return ResponseEntity.ok().body("No data to download!");
		} 
		catch (Exception e) {
			return handleExeption(e);
		}
	}

	private HttpHeaders buildHttpHeaders(HttpSession session, HttpHeaders httpHeaders, String operationName) {
		
		String auth = httpHeaders.getFirst("Authorization");
		if(!StringUtils.hasText(auth)) {
			throw new HttpServerErrorException(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()), "Missing access token");
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		headers.set(RequestParams.LANGUAGE, "en");
		headers.set(RequestParams.AUTHORIZATION, auth);
		if (session.getAttribute(RequestParams.BC_PROJECT) == null) {
			throw new HttpServerErrorException(HttpStatusCode.valueOf(403), "You have to open a project first!");
		}
		headers.set(RequestParams.BC_CLIENT, session.getAttribute(RequestParams.BC_CLIENT).toString());
		headers.set(RequestParams.BC_PROJECT, session.getAttribute(RequestParams.BC_PROJECT).toString());
		headers.set(RequestParams.BC_PROFILE, session.getAttribute(RequestParams.BC_PROFILE).toString());
		headers.set(RequestParams.OPERATION_NAME, operationName);
		return headers;
	}

	private ResponseEntity<?> handleExeption(Exception e) {
		if (e instanceof HttpStatusCodeException) {
			log.error("Error!", e);
			return ResponseEntity.status(((HttpStatusCodeException) e).getStatusCode()).body(e.getMessage());
		} 
		else if (e instanceof ResourceAccessException) {
			log.error("Error!", e);
			return ResponseEntity.internalServerError().body("Service unavailable!");
		}
		else if (e instanceof IllegalStateException) {
			log.error("Error!", e);
			return ResponseEntity.internalServerError().body("Service unavailable : " + e.getMessage());
		}
		else {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
	}

}