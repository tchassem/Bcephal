/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class InitiationParameterBuilder extends ParameterBuilder {

	protected String MODEL_NAME = "Default Model";
	protected String ENTITY_NAME = "Loader";
	protected Model model;

	@Transactional
	public void buildParameters(HttpSession session, Locale locale) {
		HashMap<Parameter, IPersistent> parameters = new HashMap<Parameter, IPersistent>(0);
		try {
			buildInitiationParameters(parameters, session, locale);
			saveParameters(parameters, locale);
		} catch (Exception ex) {
			log.error("Unable to save billing role parameters ", ex);
		}
	}

	private void buildInitiationParameters(HashMap<Parameter, IPersistent> parametersMap, HttpSession session, Locale locale) throws Exception {
		this.model = (Model) buildParameterIfNotExist(MODEL_NAME, InitiationParameterCodes.INITIATION_DEFAULT_MODEL,
				ParameterType.MODEL, parametersMap, null, null, null, session, locale);
		buildParameterIfNotExist("Default Entity", InitiationParameterCodes.INITIATION_DEFAULT_ENTITY,
				ParameterType.ENTITY, parametersMap, null, null, this.model, session, locale);
		buildParameterIfNotExist("Load File", InitiationParameterCodes.INITIATION_FILE_LOADER_FILE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("Load Nbr", InitiationParameterCodes.INITIATION_FILE_LOADER_LOAD_NBR_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, ENTITY_NAME, this.model, session, locale);
		buildIncrementalNumber("Load Nbr Generator", InitiationParameterCodes.INITIATION_FILE_LOADER_LOAD_NBR_SEQUENCE,
				ParameterType.INCREMENTAL_NUMBER);
		
		buildParameterIfNotExist("Operation Code", InitiationParameterCodes.OPERATION_CODE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, ENTITY_NAME, this.model, session, locale);

	}

}
