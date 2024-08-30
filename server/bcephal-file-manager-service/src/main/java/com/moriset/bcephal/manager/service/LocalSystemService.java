/**
 * 
 */
package com.moriset.bcephal.manager.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.manager.controller.BrowserData;
import com.moriset.bcephal.manager.controller.BrowserDataFilter;
import com.moriset.bcephal.manager.controller.BrowserDataPage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */

@Component
@Slf4j
public class LocalSystemService implements BaseService {

	@Autowired
	public LocalSystemProperties manager;
	
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	
	
	/* (non-Javadoc)
	 * @see com.moriset.misp.document.service.BaseService#getById(java.lang.String)
	 */
	@Override
	public ResponseEntity<String> getById(String documentId) {
		Path path;
		try {
			path = getLocationFile(documentId);
		
		if(path != null && path.toFile().exists()) {
			Resource resource = resourceLoader.getResource("file:" + path.toFile().getCanonicalPath());
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(formatId(resource.getFile().toPath())));
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).build();
	}
	
	/**
	 * 
	 */

	@Override
	public ResponseEntity<String> saveOrUpdate(String data, String nodeId) {
		JSONObject jData = new JSONObject(data);
		try {
			String name = jData.getString("name");
			Path path = getDirLocation(nodeId).resolve(name);
			String nodeType = jData.getString("nodeType");
			boolean isFolder = nodeType != null && nodeType.toUpperCase().contains("folder".toUpperCase());
			if(path != null && !path.toFile().exists() && isFolder) {
				path.toFile().mkdirs();
			}
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(formatId(path)));
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String path(String nodeId) throws UnsupportedEncodingException {
		byte[] decodeBase64PathName = Base64.getDecoder().decode(nodeId.getBytes("UTF-8"));
		String value = new String(decodeBase64PathName);
		log.trace(value);
		return value;
	}
	
	public String formatId(Path path) throws UnsupportedEncodingException {
		String fullPathname = path.toAbsolutePath().toString();
		byte[] bytesPathName = fullPathname.getBytes("UTF-8");
		String base64PathName = Base64.getEncoder().encodeToString(bytesPathName);
		log.trace(new String(base64PathName));
		return base64PathName;
	}
	
	@Override
	public ResponseEntity<String> delete(String documentId) {
		Path path;
			try {
				try {
					path(documentId);
				}catch (Exception e) {
					return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("true");
				}				
				path = getLocationFile(documentId);			
			if(path != null && path.toFile().exists()) {
				FileUtils.forceDelete(path.toFile());
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("true");
			}
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
			return ResponseEntity.status(509).contentType(MediaType.APPLICATION_JSON).build();
	}	
	
	private Path getDirLocation(String documentId){
		Path path = null;
		try {
		  path = getLocationFile(documentId);
		}catch (Exception e) {
			path = Paths.get(manager.getLocationPath(),documentId).toAbsolutePath().normalize();
		}
		if(!path.toFile().exists()) {
			path = Paths.get(manager.getLocationPath(),documentId).toAbsolutePath().normalize();
		}
		if(path != null && !path.toFile().exists()) {
			path.toFile().mkdirs();
		}
		return path;
    }
	
	private Path getLocationFile(String documentId) throws IOException {
        return Paths.get(path(documentId)).toAbsolutePath().normalize();
    }
	
	private String getJsonEntry(String id){
		return id;
	}
	
	@Override
	public ResponseEntity<String> handleFileUpload(MultipartFile file, String body, String documentId) {
		//Normalize the path by suppressing sequences like "path/.." and inner simple dots.
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // we can add additional file validation to discard invalid files
            Path uploadDir = getDirLocation(documentId).resolve(fileName);
            //copy the file to the upload directory,it will replace any file with same name.
            Files.copy(file.getInputStream(), uploadDir, StandardCopyOption.REPLACE_EXISTING);
        	return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(formatId(uploadDir)));
        } catch (IOException e) {
        	log.error("unable to cpy file to the target location {}", e);
        }
        return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public ResponseEntity<Resource> handleFileDownload(String documentId) throws IOException {
		Path path = getLocationFile(documentId);
		if(path != null && path.toFile().exists()) {
			Resource resource = resourceLoader.getResource("file:" + path.toFile().getCanonicalPath());
			return ResponseEntity.ok().header("fileName", path.toFile().getName()).contentType(MediaType.APPLICATION_JSON).body(resource);
		}else {
			return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).build();
		}
	}
	
	@Override
	public ResponseEntity<?> viewDocument(BrowserDataFilter viewDocument) throws IOException {
		File path = Paths.get(viewDocument.getCriteria()).toFile();
		if(path != null && path.exists()) {
			Resource resource = resourceLoader.getResource("file:" + path.getCanonicalPath());
			return ResponseEntity.ok().header("fileName", path.getName()).contentType(MediaType.APPLICATION_JSON).body(resource);
		}else {
			return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).build();
		}
	}

	@Override
	public ResponseEntity<?> GetBrowserItems(BrowserDataFilter filter) {
		List<BrowserData> items = new ArrayList<BrowserData>();
		if(filter.getPageSize() == 0) {
			filter.setPageSize(25);
		}		
		if(filter.getPage() <= 0) {
			filter.setPage(1);
		}		
		File path = Paths.get(manager.getLocationPath()).toFile();
		List<File> filles =  getFiles(path);
		long start = (filter.getPage() - 1 ) * filter.getPageSize();		
		long MaxItem = start + filter.getPageSize();
		long Total = filles.size();
		
		if(MaxItem > Total) {
			MaxItem = Total;
		}	
		
		for(Long index=start; index< MaxItem; index++) {	
			File file = filles.get(index.intValue());
			try {
			items.add(new BrowserData(index, file.getName(), file.getCanonicalPath()));
			}catch (Exception e) {}
		}
		BrowserDataPage<BrowserData> Page = new BrowserDataPage<BrowserData>(items);
		Page.pageSize = filter.getPageSize();
		Page.page = filter.getPageSize();
		Page.totalItems = Total;		
		return ResponseEntity.ok().body(Page);
	}
	
	
	List<File> getFiles(File path){
		List<File>  files = new ArrayList<>();
		if(path != null && path.exists()) {
			if(path.isFile()) {
				files.add(path);
			}else {
				for(File file : path.listFiles()) {
					if(file.isFile()) {
						files.add(file);
					}else {
						files.addAll(getFiles(file));
					}
				}	
			}
		}
		return files;
	}
}
