/**
 * 
 */
package com.moriset.bcephal.sourcing.grid.controller;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderBrowserData;
import com.moriset.bcephal.loader.domain.FileLoaderColumn;
import com.moriset.bcephal.loader.domain.FileLoaderColumnDataBuilder;
import com.moriset.bcephal.loader.service.FileLoaderService;
import com.moriset.bcephal.loader.service.SheetData;
import com.moriset.bcephal.loader.service.SpreadSheetData;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
@RestController
@RequestMapping("/sourcing/file-loader")
public class FileLoaderController extends BaseController<FileLoader, FileLoaderBrowserData> {

	@Autowired
	FileLoaderService loaderService;

	@Override
	protected FileLoaderService getService() {
		return loaderService;
	}

	@PostMapping(path = "/build-columns", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> buildFileLoaderColumns(@RequestPart("file") MultipartFile file,
			@RequestPart("data") FileLoaderColumnDataBuilder data,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,@RequestHeader HttpHeaders headers) throws Exception {
		log.debug("Call buildFileLoaderColumns");
		if (file == null) {
			throw new RuntimeException("You must select the a file for uploading");
		}
		String path = null;
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);	
			String folder = "FileLoaderTemp" + System.currentTimeMillis();
			log.debug("Try to save file...");
			path = saveFile(file, folder);
			log.debug("File saved: {}", path);
			List<FileLoaderColumn> fileLoaderColumns = ((FileLoaderService) getService()).buildFileLoaderColumns(data,
					path, locale, session);
			log.debug("Found : {}", fileLoaderColumns.size());
			return ResponseEntity.status(HttpStatus.OK).body(fileLoaderColumns);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while building file loader columns", e);
			String message = messageSource.getMessage("unable.to.build.file.loader.columns", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		} finally {
			if (path != null) {
				Path path_ = Paths.get(path);
				if (path_.toFile().exists()) {
					try {
						Files.deleteIfExists(path_);
					} catch (Exception e) {
					}
				}
			}
		}
	}
	
//	@PostMapping(path = "/spreadsheet/info")
//	public ResponseEntity<?> GetSpreadDataInfo(
//			@RequestPart("chunk") MultipartFile chunk,  
//			@RequestPart("fileName_")String fileName,
//	        @RequestPart("folder")String folder,
//			@RequestPart("length") String length)
//	throws Exception {
//	    try {
//	    	ResponseEntity<?> response = buildSpreadSheetInfo(chunk, fileName, length,folder);
//	        return response;
//	    } catch (IOException e) {
//	    	log.error("io exception",e);
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//	    }
//	}
	
	@PostMapping(path = "/spreadsheet/info-by-path")
	public ResponseEntity<?> GetSpreadDataInfoByPath(@RequestHeader("remote-path-__") String pathFileName)
	throws Exception {
	    try {
	    	ResponseEntity<?> response = buildSpreadSheetInfo(pathFileName);
	        return response;
	    } catch (IOException e) {
	    	log.error("io exception",e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	@PostMapping(path = "/build-columns-by-remote-path")
	public ResponseEntity<?> buildFileLoaderColumnsByRemotePath(@RequestHeader("remote-path-__") String path,
			@RequestBody FileLoaderColumnDataBuilder data,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,@RequestHeader HttpHeaders headers) throws Exception {
		log.debug("Call buildFileLoaderColumns");
		if (path == null) {
			throw new RuntimeException("You must select the a file for uploading");
		}		
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);	
			path = FilenameUtils.separatorsToSystem(path);
			log.debug("File saved: {}", path);
			List<FileLoaderColumn> fileLoaderColumns = ((FileLoaderService) getService()).buildFileLoaderColumns(data,
					path, locale, session);
			log.debug("Found : {}", fileLoaderColumns.size());
			return ResponseEntity.status(HttpStatus.OK).body(fileLoaderColumns);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while building file loader columns", e);
			String message = messageSource.getMessage("unable.to.build.file.loader.columns", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		} finally {
			if (path != null) {
				Path path_ = Paths.get(path);
				if (path_.toFile().exists()) {
					try {
					//	Files.deleteIfExists(path_);
						//path_.toFile().deleteOnExit();
					} catch (Exception e) {
					}
				}
			}
		}

	}
	
	
	
//	@PostMapping(path = "/upload/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> uploadWithResume(
//			@RequestPart("chunk") MultipartFile chunk,  
//			@RequestPart("fileName_")String fileName,
//	        @RequestPart("folder")String folder,
//			@RequestPart("length") String length
//	) throws Exception {
//	    try {
//	        return fileResumeUpload(chunk, fileName, length,folder);
//	    } catch (IOException e) {
//	    	log.error("io exception",e);
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//	    }
//	}
//	
//	@Value("${bcephal.project.temp-dir}")
//	protected String tempDir;
	
	
	@SuppressWarnings("finally")
	public ResponseEntity<?> fileResumeUpload(MultipartFile chunk , String fileName,String length,String folder) throws IOException, ParseException {
		
		Path path2 = Paths.get(tempDir, folder , fileName);
		if (!path2.getParent().toFile().exists()) {
			path2.getParent().toFile().mkdirs();
		}		
	    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path2.toFile(), true));
	    boolean uploaded = true;
	    try {
	        out.write(chunk.getBytes());
	    } catch (IOException e) {
	        uploaded = false;
	        log.error("io exception",e);
	    } finally {
	        if (uploaded) {
	            out.close();
	                return ResponseEntity.ok(true);
	        } else {
	            out.close();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	}
	

//	public ResponseEntity<?> buildSpreadSheetInfo(MultipartFile chunk , String fileName,String length,String folder) throws Exception 
//	{
//		String path = null;
//		folder = folder.replaceAll("^\"|\"$", "");
//		try {
//			path = saveFile(chunk, folder);
//			//log.debug("File saved: {}", path);
//		    FileInputStream inputStream = new FileInputStream(path);
//		    XSSFWorkbook xworkbook = new XSSFWorkbook(inputStream);
//	        SpreadSheetData spreadsheetData = new SpreadSheetData();
//	        int nbsheet= xworkbook.getNumberOfSheets();
//	        List<SheetData> sheets = spreadsheetData.getSheetDatas();
//	        for(int i = 0 ; i< nbsheet ; i++) {
//	        	sheets.add(new SheetData(i,xworkbook.getSheetName(i)));
//	        }
//	        spreadsheetData.setRepositoryOnServer(path);
//	        xworkbook.close();
//	        inputStream.close();
//	    	//log.debug("SpreadSheetData  : repositoryOnServer => {}, nbSheet => {}", spreadsheetData.getRepositoryOnServer(), spreadsheetData.getSheetDatas().size());
//			return ResponseEntity.status(HttpStatus.OK).body(spreadsheetData);
//		}catch(BcephalException e) {
//			log.debug(e.getMessage());
//			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
//		}
//		
//	    
//	}
	
	
	public ResponseEntity<?> buildSpreadSheetInfo(String pathName) throws Exception 
	{
		Path path = Paths.get(pathName).normalize();
		try {
			//log.debug("File saved: {}", path);
		    FileInputStream inputStream = new FileInputStream(path.toFile());
		    XSSFWorkbook xworkbook = new XSSFWorkbook(inputStream);
	        SpreadSheetData spreadsheetData = new SpreadSheetData();
	        int nbsheet= xworkbook.getNumberOfSheets();
	        List<SheetData> sheets = spreadsheetData.getSheetDatas();
	        for(int i = 0 ; i< nbsheet ; i++) {
	        	sheets.add(new SheetData(i,xworkbook.getSheetName(i)));
	        }
	        spreadsheetData.setRepositoryOnServer(path.getParent().normalize().toString());
	        xworkbook.close();
	        inputStream.close();
	    	//log.debug("SpreadSheetData  : repositoryOnServer => {}, nbSheet => {}", spreadsheetData.getRepositoryOnServer(), spreadsheetData.getSheetDatas().size());
			return ResponseEntity.status(HttpStatus.OK).body(spreadsheetData);
		}catch(BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		
	    
	}

}
