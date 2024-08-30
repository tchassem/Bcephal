/**
 * 3 juin 2024 - LoaderTemplateController.java
 *
 */
package com.moriset.bcephal.sourcing.grid.controller;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.loader.domain.LoaderTemplate;
import com.moriset.bcephal.loader.service.LoaderTemplateService;
import com.moriset.bcephal.project.archive.ProjectFileUtil;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;
import com.moriset.bcephal.utils.ZipFileUnZipUtil;

import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Emmanuel Emmeni
 *
 */
@Slf4j
@RestController
@RequestMapping("/sourcing/loader-template")
public class LoaderTemplateController extends BaseController<LoaderTemplate, BrowserData> {

	@Autowired
	LoaderTemplateService loaderTemplateService;

	@Autowired
	ProjectFileUtil projectFileUtil;

	@Override
	protected LoaderTemplateService getService() {
		return loaderTemplateService;
	}

	@PostMapping("/save-template")
	public ResponseEntity<?> save(@RequestBody LoaderTemplate entity, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, @RequestHeader HttpHeaders headers) {
		log.debug("Call of save entity : {}", entity);
		try {
			String projectName = headers.getFirst(RequestParams.BC_PROJECT_NAME);
			if(!StringUtils.hasText(projectName)) {
				throw new BcephalException("Undefined project name!");
			}
			getService().save(entity, locale, projectName);
			entity = getService().getById((Long) entity.getId());
			log.debug("entity : {}", entity);
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

	@Override
	public ResponseEntity<String> uploadData(MultipartFile file, String code) throws Exception {

		log.debug("Call uploadData");
		if (file == null) {
			throw new RuntimeException("You must select a file for uploading");
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

		byte[] buffer = new byte[inputStream.available()];
		inputStream.read(buffer);

		File file_ = Paths.get(FilenameUtils.separatorsToSystem(projectFileUtil.getDataDir()), "loader-templates", code, code.concat(".zip")).toFile();
		if (file_ != null && file_.getParentFile() != null && !file_.getParentFile().exists()) {
			file_.getParentFile().mkdirs();
		} else {
			try {
				FileUtils.deleteDirectory(file_.getParentFile());
				file_.getParentFile().mkdirs();
			} catch (Exception e) {
				log.error("Fail to delete last dir old dir  {}", e);
			}
		}
		try {
			Files.write(file_.toPath(), buffer);
		} catch (Exception e) {
			log.error("upload data to BillTemplate failed {}", e);
			throw new WebApplicationException(511);
		}
		try {
			java.nio.file.Path source = file_.toPath();
			java.nio.file.Path target = file_.getParentFile().toPath();
			ZipFileUnZipUtil.unzipFile(source, target);
			log.debug("Upload data sucessfully");
			return new ResponseEntity<String>("true", HttpStatus.OK);
		} catch (Exception e) {
			log.error("Fail to UnzipFile BillTemplate {}", e);

		} finally {
			try {
				Files.deleteIfExists(file_.toPath());
			} catch (Exception e) {
				log.error("Fail to delete file zip {}", e);
			}
		}
		throw new WebApplicationException(511);
	}

}
