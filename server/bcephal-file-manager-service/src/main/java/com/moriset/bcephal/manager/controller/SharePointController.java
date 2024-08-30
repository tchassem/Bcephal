/**
 * 
 */
package com.moriset.bcephal.manager.controller;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author MORISET-004
 *
 */
@RestController
public class SharePointController extends BaseController{

//	@Autowired
//	BaseService baseService;
//	@Autowired
//	ResourceLoader resourceLoader;
	
//	@PostMapping("/pentaho")
//	public ResponseEntity<String> penthato() throws IOException {
//		String response = "{\"name\":\"bcephalPentaho\"}";
//		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
//	}
//	
//	@PostMapping("/pentaho/download")
//	public ResponseEntity<Resource> penthatoDownloadFile() throws IOException {
//		Path path = Files.createTempFile("bcephal", "-");
//		FileUtils.writeStringToFile(path.toFile(), "1;bcephalPentaho", Charset.forName("UTF-8"));
//		if(path != null && path.toFile().exists()) {
//			Resource resource = resourceLoader.getResource("file:" + path.toFile().getCanonicalPath());
//			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
//		}else {
//			return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).build();
//		}
//	}
}
