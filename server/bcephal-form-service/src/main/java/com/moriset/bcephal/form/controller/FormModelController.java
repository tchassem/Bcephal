/**
 * 
 */
package com.moriset.bcephal.form.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelMenu;
import com.moriset.bcephal.grid.service.form.FormModelService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/form/model")
@Slf4j
public class FormModelController extends BaseController<FormModel, BrowserData> {

	@Autowired
	FormModelService formModelService;

	@Override
	protected FormModelService getService() {
		return formModelService;
	}

	@GetMapping("/menus")
	public ResponseEntity<?> getMenus(@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get menus");
		try {
			List<FormModelMenu> menus = getService().getActiveMenus();
			log.debug("Found : {}", menus.size());
			return ResponseEntity.status(HttpStatus.OK).body(menus);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving active menus", e);
			String message = messageSource.getMessage("unable.to.retieve.active.form.menus", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/import-all-labels")
	public ResponseEntity<?> importAllLabels(@RequestBody FormModel model, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of import all labels : {}", model.getName());
		try {
			int count = getService().importAllLabels(model);
			log.debug("{} labels imported", count);
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while importing labels : {}", model.getName(), e);
			String message = messageSource.getMessage("unable.to.import.labels", new Object[] { model }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
