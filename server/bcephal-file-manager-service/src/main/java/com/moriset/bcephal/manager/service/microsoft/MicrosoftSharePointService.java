/**
 * 
 */
package com.moriset.bcephal.manager.service.microsoft;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.manager.controller.BrowserDataFilter;
import com.moriset.bcephal.manager.service.BaseService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */

@Component
@Slf4j
@Data
public class MicrosoftSharePointService implements BaseService {

	@Autowired
	public MicrosoftSharePointProperties manager;
	
	@Autowired
	public HttpClientForSharePoint httpClientForSharePoint;
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	/* (non-Javadoc)
	 * @see com.moriset.misp.document.service.BaseService#getById(java.lang.String)
	 */
	@Override
	public ResponseEntity<String> getById(String documentId) {
		try {
			byte[] value = httpClientForSharePoint.getFileById(documentId);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new String(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public ResponseEntity<String> saveOrUpdate(String data, String nodeId) {
		try {		
			//httpClientForSharePoint.createdFile(nodeId,);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<String> delete(String documentId) {
		try {
			boolean resp = httpClientForSharePoint.deletefile(documentId);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp + "");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("{}",e);
		}
		return ResponseEntity.status(509).contentType(MediaType.APPLICATION_JSON).build();
	}	
	
	@Override
	public ResponseEntity<String> handleFileUpload(MultipartFile file, String body, String documentId) {
		 try {
            String id = httpClientForSharePoint.createdFile(documentId, file.getBytes() , file.getOriginalFilename());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(id);
        } catch (Exception e) {
        	log.error("unable to cpy file to the target location {}", e);
        }
        return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public ResponseEntity<Resource> handleFileDownload(String documentId) throws IOException {			
			try {
				byte[] bytes = httpClientForSharePoint.getFileById(documentId);
				Resource resource = new ByteArrayResource(bytes);		
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
			} catch (Exception e) {
				log.error("unable to download file to the target location {}", e);
			}
			return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).build();
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
