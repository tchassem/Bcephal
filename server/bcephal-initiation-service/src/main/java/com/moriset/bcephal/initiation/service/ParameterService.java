/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.initiation.repository.ParameterRepository;
import com.moriset.bcephal.service.PersistentService;

/**
 * @author Joseph Wambo
 *
 */
@Service
public class ParameterService extends PersistentService<Parameter, BrowserData> {

	@Autowired
	ParameterRepository parameterRepository;

	@Override
	public ParameterRepository getRepository() {
		return parameterRepository;
	}

	public EditorData<Parameter> getEditorData(EditorDataFilter filter, Locale locale) {
		EditorData<Parameter> data = new EditorData<Parameter>();
		data.setItem(new Parameter());
		List<Parameter> parameters = parameterRepository.findAll();
		data.getItem().setParameters(new ListChangeHandler<>(parameters));

//		data.setModels(initiationService.getModels());
//		data.setPeriods(initiationService.getPeriods());
//		data.setMeasures(initiationService.getMeasures());
		return data;
	}

}
