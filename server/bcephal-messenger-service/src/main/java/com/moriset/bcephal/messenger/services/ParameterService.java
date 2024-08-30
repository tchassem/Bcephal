package com.moriset.bcephal.messenger.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.messenger.model.Parameter;
import com.moriset.bcephal.messenger.repository.ParameterRepository;


@Service
public class ParameterService {

	private static final String BYPASS_WHITE_LIST = "bcephal.bypass.white.list";
	
	@Autowired
	ParameterRepository parameterRepository;
	
	public boolean isBypassWhiteList() {
		Parameter parameter = getParameter(BYPASS_WHITE_LIST);		
		if(parameter == null) {
			return setBypassWhiteList(true, Locale.ENGLISH);
		}
		return parameter.isBooleanValue();
	}

	public boolean setBypassWhiteList(boolean allowed, Locale locale) {
		Parameter parameter = getParameter(BYPASS_WHITE_LIST);
		if(parameter == null) {
			parameter = new Parameter();
			parameter.setCode(BYPASS_WHITE_LIST);
		}
		parameter.setBooleanValue(allowed);
		parameterRepository.save(parameter);
		return allowed;
	}
	
	public Parameter getParameter(String code) {
		Parameter parameter = parameterRepository.findByCode(code);
		return parameter;
	}
	
}
