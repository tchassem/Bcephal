package com.moriset.bcephal.billing.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.billing.service.BillTemplateService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.project.archive.ProjectFileUtil;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;
import com.moriset.bcephal.utils.ZipFileUnZipUtil;

import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/billing/template")
public class BillTemplateController extends BaseController<BillTemplate, BrowserData> {

	@Autowired
	BillTemplateService billTemplateService;

	@Autowired
	ProjectFileUtil projectFileUtil;

	@Override
	protected BillTemplateService getService() {
		return billTemplateService;
	}
	
	@PostMapping("/create-default-templates")
	public ResponseEntity<?> createDefaultTemplates( @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, @RequestHeader HttpHeaders headers) {
		log.debug("Call of createDefaultTemplates...");
		try {
			String projectName = headers.getFirst(RequestParams.BC_PROJECT_NAME);
			if(!StringUtils.hasText(projectName)) {
				throw new BcephalException("Undefined project name!");
			}
			getService().createDefaultTemplates(projectName);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while creating default templates.", e);
			String message = messageSource.getMessage("unable.to.create.default.template", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save-template")
	public ResponseEntity<?> save(@RequestBody BillTemplate entity, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, @RequestHeader HttpHeaders headers) {
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
	
	
	@PostMapping("/change-logo/{id}")
	public ResponseEntity<?> changeLogo(@PathVariable("id") Long id, @RequestBody String path, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, @RequestHeader HttpHeaders headers) {
		log.debug("Call of Change logo : {}", id);
		try {
			String projectName = headers.getFirst(RequestParams.BC_PROJECT_NAME);
			if(!StringUtils.hasText(projectName)) {
				throw new BcephalException("Undefined project name!");
			}
			boolean ok = getService().changeLogo(id, path, projectName);
			log.debug("Logo changed : {}", ok);
			return ResponseEntity.status(HttpStatus.OK).body(ok);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while changing logo : {}", id, e);
			String message = messageSource.getMessage("unable.to.change.logo", new Object[] { id }, locale);
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

		File file_ = Paths.get(FilenameUtils.separatorsToSystem(projectFileUtil.getDataDir()), "billing-templates", code,
				code.concat(".zip")).toFile();
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
