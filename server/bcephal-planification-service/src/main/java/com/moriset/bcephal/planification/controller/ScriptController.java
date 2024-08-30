package com.moriset.bcephal.planification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.planification.domain.script.Script;
import com.moriset.bcephal.planification.domain.script.ScriptBrowserData;
import com.moriset.bcephal.planification.service.ScriptService;
import com.moriset.bcephal.service.MainObjectService;

@RestController
@RequestMapping("/planification/script")
public class ScriptController extends BaseController<Script, ScriptBrowserData>{

	@Autowired
	ScriptService scriptService;
	
	@Override
	protected MainObjectService<Script, ScriptBrowserData> getService() {
		return scriptService;
	}

}
