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
public class GitService implements BaseService {

	/* (non-Javadoc)
	 * @see com.moriset.misp.document.service.BaseService#getById(java.lang.String)
	 */
	@Override
	public ResponseEntity<String> getById(String documentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> saveOrUpdate(String data, String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> delete(String documentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> handleFileUpload(MultipartFile file, String body, String documentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Resource> handleFileDownload(String documentId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public ResponseEntity<?> viewDocument(BrowserDataFilter viewDocument) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ResponseEntity<?> GetBrowserItems(BrowserDataFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
