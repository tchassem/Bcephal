/**
 * 
 */
package com.moriset.bcephal.form.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.form.Form;
import com.moriset.bcephal.grid.service.form.FormModelService;
import com.moriset.bcephal.grid.service.form.FormModelSpotData;
import com.moriset.bcephal.grid.service.form.FormService;
import com.moriset.bcephal.grid.service.form.Reference;
import com.moriset.bcephal.grid.service.form.ReferenceValue;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/form/data")
@Slf4j
public class FormController extends BaseController<Form, Object[]> {

	@Autowired
	FormService formService;

	@Autowired
	FormModelService formModelService;
	
	@Override
	protected FormService getService() {
		return formService;
	}

	@Override
	public ResponseEntity<?> save(@RequestBody Form entity, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of save entity : {}", entity);
		try {
			boolean isNew = entity.getId() == null;
			entity = getService().saveForm(entity, client, profileId, projectCode, locale);
			String rightLevel = isNew ? "CREATE" : "EDIT";
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), entity.getId(), getService().getFunctionalityCode(), rightLevel,profileId);
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
	
	@PostMapping("/duplicate-forms/{modelId}")
	public ResponseEntity<?> duplicateForms(@RequestBody List<Long> ids, 
			@PathVariable("modelId") Long modelId,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of duplicate forms");
		try {
			int count = getService().duplicate(ids, modelId, client, profileId, projectCode, locale);
			log.debug("Duplicated : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while duplicating forms : {}", ids, e);
			String message = messageSource.getMessage("unable.to.duplicate.forms", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/duplicate-form/{modelId}")
	public ResponseEntity<?> duplicateForm(@RequestBody Long id, 
			@PathVariable("modelId") Long modelId,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of duplicate form");
		try {
			Form form = getService().duplicate(id, modelId, client, profileId, projectCode, locale);
			return ResponseEntity.status(HttpStatus.OK).body(form);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while duplicating form : {}", id, e);
			String message = messageSource.getMessage("unable.to.duplicate.form", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/delete-items/{modelId}")
	public ResponseEntity<?> delete(@RequestBody List<Long> oids, 
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@PathVariable("modelId") Long modelId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of delete : {}", oids);
		try {
			Map<Long, String> map = new LinkedHashMap<Long, String>();
			if(oids != null) {
				oids.forEach(result ->{
					map.put(result, getFunctionalityCode(result));
				});
			}
			getService().delete(oids, modelId, client, profileId, projectCode, locale);
			String rightLevel = "DELETE";
			if(oids != null) {
				map.forEach((result, functionalityCode) ->{
				getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), result, functionalityCode, rightLevel,profileId);
			});
			}	
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleteing : {}", oids, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/validate/{modelId}/{validated}")
	public ResponseEntity<?> validate(@RequestBody Form data, @PathVariable("modelId") Long modelId,
			@PathVariable("validated") Boolean validated, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of validate entity : {}", data);
		try {
			data = getService().validate(data, validated, client, profileId, projectCode, locale);
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(),modelId,getFunctionalityCode(modelId) , "VALIDATE",profileId);
			log.debug("entity : {}", data);
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while validating entity : {}", data, e);
			String message = messageSource.getMessage("unable.to.validate.entity", new Object[] { data }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/validate-datas/{modelId}/{validated}")
	public ResponseEntity<?> validate(@RequestBody List<Long> ids, @PathVariable("modelId") Long modelId,
			@PathVariable("validated") Boolean validated, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of validate entity : {}", ids);
		try {
			getService().validate(ids, modelId, validated, locale);
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(),modelId,getFunctionalityCode(modelId) , "VALIDATE",profileId);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while validating datas : {}", ids, e);
			String message = messageSource.getMessage("unable.to.validate.datas", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/reference-value")
	public ResponseEntity<?> getReferenceValue(@RequestBody Reference data, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getReferenceValue : {}", data);
		try {
			ReferenceValue value = getService().getReferenceValue(data);
			log.trace("Reference value : {}", value);
			return ResponseEntity.status(HttpStatus.OK).body(value);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while building reference value : {}", data, e);
			String message = messageSource.getMessage("unable.to.get.reference.value", new Object[] { data }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/reference-values")
	public ResponseEntity<?> getReferenceValue(@RequestBody List<Reference> datas, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getReferenceValues : {}", datas.size());
		try {
			List<ReferenceValue> values = getService().getReferenceValues(datas);
			log.trace("Reference values : {}", values.size());
			return ResponseEntity.status(HttpStatus.OK).body(values);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while building reference value : {}", "", e);
			String message = messageSource.getMessage("unable.to.get.reference.values", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/spot-value")
	public ResponseEntity<?> getSpotValue(@RequestBody FormModelSpotData data, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getReferenceValue : {}", data);
		try {
			ReferenceValue value = getService().getSpotValue(data);
			log.trace("Reference value : {}", value);
			return ResponseEntity.status(HttpStatus.OK).body(value);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while building reference value : {}", data, e);
			String message = messageSource.getMessage("unable.to.get.reference.value", new Object[] { data }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/spot-values")
	public ResponseEntity<?> getSpotValue(@RequestBody List<FormModelSpotData> datas, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getReferenceValues : {}", datas.size());
		try {
			List<ReferenceValue> values = getService().getSpotValues(datas);
			log.trace("Reference values : {}", values.size());
			return ResponseEntity.status(HttpStatus.OK).body(values);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while building reference value : {}", "", e);
			String message = messageSource.getMessage("unable.to.get.reference.values", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/rows")
	public ResponseEntity<?> searchRows(@RequestBody GrilleDataFilter filter,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {

		log.debug("Call of searchRows");
		try {
			 BrowserDataPage<GridItem> page = getService().searchRows(filter,client, projectCode,profileId,locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}


	
	@PostMapping("/code-access-rights")
	public ResponseEntity<?> CodeAccessRights(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of Code Access Rights project : {}", projectCode);
		try {
			List<ClientFunctionality> items =  formModelService.getFunctionalityRight(client, projectCode);
			return ResponseEntity.status(HttpStatus.OK).body(items);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while Code Access Rights project : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.get.code.access.datas", new Object[] { projectCode }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
