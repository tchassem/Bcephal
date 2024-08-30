package com.moriset.bcephal.sourcing.grid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.Variable;
import com.moriset.bcephal.domain.VariableBrowserData;
import com.moriset.bcephal.grid.service.VariableService;

@RestController
@RequestMapping("/sourcing/variable")
public class VariableController extends BaseController<Variable, VariableBrowserData> {

	@Autowired
	VariableService variableService;

	@Override
	protected VariableService getService() {
		return variableService;
	}

}
