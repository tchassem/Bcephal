/**
 * 
 */
package com.moriset.bcephal.manager.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.manager.model.FileManagerBrowserDataPage;
import com.moriset.bcephal.manager.model.enumeration.InvoiceType;
import com.moriset.bcephal.manager.service.BaseService;
import com.moriset.bcephal.manager.service.FileManagerService;
import com.moriset.bcephal.manager.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


/**
 * @author MORISET-004
 *
 */
@Slf4j
@RestController
@RequestMapping("/file-manager")
public class FileManagerController extends BaseController {

	@Autowired
	BaseService baseService;

	@Autowired
	FileManagerService fileManagerService;

	@Autowired
	protected MessageSource messageSource;
	
	@PostMapping("/document-by-id/{oid}")
	public ResponseEntity<String> getDocumentById(@PathVariable("oid") String documentId) throws IOException {
		return baseService.getById(documentId);
	}
	
	@PostMapping("/save-update/{category}")
	public ResponseEntity<String> saveOrUpdate(@RequestBody String data,@PathVariable("category") String nodeId) throws IOException {
		return baseService.saveOrUpdate(data,nodeId);
	}
	
	@DeleteMapping("/delete/{oid}")
	public ResponseEntity<String> delete(@PathVariable("oid") String documentId) throws IOException {
		return baseService.delete(documentId);
	}
	
	@PostMapping(path = "/upload/{category}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> handleFileUpload(@RequestPart("filedata") MultipartFile file, @RequestPart("body") String body, @PathVariable("category") String parentNodeId) {
		return baseService.handleFileUpload(file, body, parentNodeId);
	}
	
	@PostMapping("/download/{oid}")
	public ResponseEntity<Resource> handleFileDownload(@PathVariable("oid") String documentId) throws IOException {
		return baseService.handleFileDownload(documentId);
	}

	@PostMapping("/browser-view")
	public ResponseEntity<?> viewDocument(@RequestBody BrowserDataFilter viewDocument) throws IOException {
		return baseService.viewDocument(viewDocument);
	}
	
	@PostMapping("/browser-items")
	public ResponseEntity<?> GetBrowserItems(@RequestBody BrowserDataFilter filter) throws IOException {
		return baseService.GetBrowserItems(filter);
	}
	
	@PostMapping("/categories")
	public ResponseEntity<?> GetCategories(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) throws IOException {
		
		List<String> categories = new ArrayList<String>();
		for (InvoiceType invoiceType : InvoiceType.values()) {
			categories.add(invoiceType.getValue());
        }
		
		return ResponseEntity.status(HttpStatus.OK).body(categories);
	}
	
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {

		log.debug("Call of search");
		try {
			FileManagerBrowserDataPage<?> page = fileManagerService.search(filter, locale);
			log.debug("Found : {}", page.page);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (Exception e) {
			log.error("Unexpected error while search files managing by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.scheduler.by.filter", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@RequestBody BrowserDataFilter filter, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, HttpServletRequest request) {
        log.debug("Controller try to get download file.");

        Resource resource = null;
        String contentType = null;
        String fileName = "";
        
        try {
            resource = fileManagerService.downloadFileAsResource(filter, locale);
        } catch (Exception e) {
            log.error("Unable to get resource file: {}", e.getMessage());
            throw new BcephalException("Resource not found: {}", e);
        }

        try {
            log.debug("Controller try to get the contentType.");
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            fileName = resource.getFile().getName();
            log.trace("Path: {}", resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.debug("Couldn't determine contentType of Resource file.");
            throw new BcephalException("Unknow type file: {}", ex);
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        log.debug("Successful building resource file operation.");
        return ResponseEntity.ok()
        		.header("fileName", fileName)
        		.header("Content-Disposition", "attachment; filename=\"" + fileName +"\"")
        		.contentType(MediaType.parseMediaType(contentType)).body(resource);
    }
	
	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<String> codes, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of delete : {}", codes);
		try {
			fileManagerService.delete(codes, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleteing : {}", codes, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { codes }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
