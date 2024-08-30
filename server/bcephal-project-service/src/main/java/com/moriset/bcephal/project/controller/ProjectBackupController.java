package com.moriset.bcephal.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.security.domain.SimpleArchive;
import com.moriset.bcephal.security.service.ProjectBackupService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/projects/backups")
@Slf4j
public class ProjectBackupController {

	@Autowired
	ProjectBackupService projectBackupService;

	@Autowired
	MessageSource messageSource;

	/*
	 * @PostMapping("/create") public ResponseEntity<?> create(@RequestBody
	 * SimpleArchive archive, @RequestHeader(RequestParams.BC_CLIENT) Long clientId)
	 * { log.debug("Call of create projectBackup : {}", archive); try {
	 * archive.setClientId(clientId); projectBackupService.create(archive,
	 * archive.getLocale()); log.debug("archive : {}", archive); return
	 * ResponseEntity.status(HttpStatus.OK).body(archive); } catch (BcephalException
	 * e) { log.debug(e.getMessage()); return
	 * ResponseEntity.status(e.getStatusCode()).body(e.getMessage()); } catch
	 * (Exception e) { log.error("Unexpected error while create projectBackup : {}",
	 * archive, e); String message =
	 * messageSource.getMessage("unable.to.create.simple.archive", new Object[] {
	 * archive }, archive.getLocale()); return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message); } }
	 */

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<SimpleArchive> page = projectBackupService.search(filter, locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search entities by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.entity.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * Delete a Project Backup.
	 * 
	 * @param archiveId The id of the SimpleArchive to delete
	 * @param locale      User locale
	 * @return Deletion status
	 */
	@DeleteMapping("/delete/{archiveId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> deleteProjectBackup(@PathVariable("archiveId") Long archiveId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of deleteProjectBackup id : {}", archiveId);
		try {
			boolean deleted = projectBackupService.deleteProjectBackup(archiveId, locale);
			return ResponseEntity.status(HttpStatus.OK).body(deleted);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting the project backup : {}", archiveId, e);
			String message = messageSource.getMessage("unable.to.delete.project", new Object[] { archiveId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}



}
