package com.moriset.bcephal.loader.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.grid.repository.GrilleColumnRepository;
import com.moriset.bcephal.loader.domain.FileLoaderColumn;
import com.moriset.bcephal.loader.repository.FileLoaderColumnRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileLoaderColumnService extends PersistentService<FileLoaderColumn, BrowserData> {

	@Autowired
	FileLoaderColumnRepository columnRepository;

	@Autowired
	GrilleColumnRepository grilleColumnRepository;

	@Override
	public FileLoaderColumnRepository getRepository() {
		return columnRepository;
	}

	@Transactional
	public FileLoaderColumn save(FileLoaderColumn fileLoaderColumn, Locale locale) {
		log.debug("Try to save FileLoaderColumn : {}", fileLoaderColumn);
		try {
			if (fileLoaderColumn == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.file.loader.column",
						new Object[] { fileLoaderColumn }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(fileLoaderColumn.getFileColumn())) {
				String message = getMessageSource().getMessage("unable.to.save.file.loader.column.with.empty.name",
						new String[] { fileLoaderColumn.getFileColumn() }, locale);
				throw new BcephalException(message);
			}
			if (fileLoaderColumn.getGrilleColumn() != null && fileLoaderColumn.getGrilleColumn().getId() == null) {
				fileLoaderColumn.setGrilleColumn(grilleColumnRepository.save(fileLoaderColumn.getGrilleColumn()));
			}
			fileLoaderColumn = columnRepository.save(fileLoaderColumn);
			return fileLoaderColumn;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save FileLoader : {}", fileLoaderColumn, e);
			String message = getMessageSource().getMessage("unable.to.save.file.loader",
					new Object[] { fileLoaderColumn }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public void delete(FileLoaderColumn fileLoaderColumn) {
		log.debug("Try to delete FileLoaderColumn : {}", fileLoaderColumn);
		if (fileLoaderColumn == null || fileLoaderColumn.getId() == null) {
			return;
		}
		columnRepository.deleteById(fileLoaderColumn.getId());
		log.debug("FileLoaderColumn successfully to delete : {} ", fileLoaderColumn);
		return;
	}
}
