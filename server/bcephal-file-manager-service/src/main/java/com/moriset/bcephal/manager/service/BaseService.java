/**
 * 
 */
package com.moriset.bcephal.manager.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.manager.controller.BrowserDataFilter;

/**
 * @author MORISET-004
 *
 */
public interface BaseService {
	ResponseEntity<String> getById(String documentId);
	ResponseEntity<String> saveOrUpdate(String data, String nodeId);
	ResponseEntity<String> delete(String documentId);
	ResponseEntity<String> handleFileUpload(MultipartFile file, String body, String parentNodeId);
	ResponseEntity<Resource> handleFileDownload(String documentId) throws IOException;
	ResponseEntity<?> viewDocument(BrowserDataFilter viewDocument)throws IOException;
	ResponseEntity<?> GetBrowserItems(BrowserDataFilter filter)throws IOException;
}
