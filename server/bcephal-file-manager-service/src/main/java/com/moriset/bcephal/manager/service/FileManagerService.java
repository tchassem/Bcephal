package com.moriset.bcephal.manager.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.manager.controller.BrowserDataFilter;
import com.moriset.bcephal.manager.model.FileManagerBrowserData;
import com.moriset.bcephal.manager.model.FileManagerBrowserDataPage;
import com.moriset.bcephal.manager.model.enumeration.InvoiceType;
import com.moriset.bcephal.manager.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileManagerService {
	
	@Value("${document.manager.locationPath}")
	public String baseDir;
	
	@Autowired
	LocalSystemService localSystemService;

	@Autowired
	protected MessageSource messageSource;
	
	public FileManagerBrowserDataPage<?> search(BrowserDataFilter filter, Locale locale) {

		log.debug("Enter inside search method of file manager service");
		
		FileManagerBrowserDataPage<FileManagerBrowserData> page;
		List<FileManagerBrowserData> browserData = new ArrayList<FileManagerBrowserData>();
		
		String repository = Paths.get(baseDir, InvoiceType.forValue(filter.getCategory()).toString().toLowerCase()).toString();
		
		if (!Files.exists(Path.of(repository)) || !Files.isDirectory(Path.of(repository))) {
			return new FileManagerBrowserDataPage<FileManagerBrowserData>(browserData);
		}
		
		List<File> files = (List<File>) FileUtils.listFiles(new File(repository), null, false);
		
		int skipCount = (int) (filter.getPage() * filter.getPageSize());
		List<File> splitFiles = files.stream().skip(skipCount).limit(filter.getPageSize()).collect(Collectors.toList());
		
		if (splitFiles.size() > 0) {			
			browserData = splitFiles.parallelStream().map((Function<? super File, ? extends FileManagerBrowserData>) fil -> {
				
				FileManagerBrowserData item = new FileManagerBrowserData();
				try {
					item.setCode(localSystemService.formatId(Path.of(Paths.get(repository, fil.getName()).toString())));
				} catch (UnsupportedEncodingException e) {
					log.error("Unable to generate file code: {}", e.getMessage());
					throw new BcephalException("Unable to tranfert bill PDF to file manager server.");
				}
				item.setCategory(filter.getCategory());
				item.setModificationDate(new Timestamp(fil.lastModified()));
				item.setName(FilenameUtils.getName(fil.getName()));
				item.setPath(Paths.get(repository, fil.getName()).toString());
				
				return item;
				
			}).collect(Collectors.toList());
		}

		page = new FileManagerBrowserDataPage<FileManagerBrowserData>(browserData);
		page.page = filter.getPage();
		page.pageSize = splitFiles.size();
		page.items = browserData;
		page.totalItems = files.size();
		
		return page;
	}

    public Resource downloadFileAsResource(BrowserDataFilter filter, Locale locale) throws Exception {
    	
    	log.debug("Enter inside download file like resource in file manager service : {}", filter.getCriteria());
		
		Path filePath = null;
		try {
    		filePath = Path.of(localSystemService.path(filter.getCriteria()));
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new BcephalException("File not found: {}", FilenameUtils.getName(filePath.toString()));
            }
        } catch (MalformedURLException ex) {
            throw new BcephalException("Request url malformed: {}", FilenameUtils.getName(filePath.toString()), ex);
        }
    }

    public void delete(List<String> codes, Locale locale) {
		log.debug("Try to delete : {} files", codes.size());
		try {
			if (codes == null || codes.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { codes }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			codes.forEach(code -> {
				Path filePath = null;
				try {
					filePath = Path.of(localSystemService.path(code));
					Files.deleteIfExists(filePath);
				} catch (UnsupportedEncodingException e) {
					log.error("Encoding problem accured: {}", e.getMessage());
					throw new BcephalException("Unable to delete file. {}", e.getMessage());
				} catch (IOException e) {
					log.error("Some error occured: {}", e.getMessage());
					throw new BcephalException("Unable to delete file. {}", e.getMessage());
				}
			});
			log.debug("{} files successfully deleteed ", codes.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete messages : {}", codes.size(), e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { codes.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

}
