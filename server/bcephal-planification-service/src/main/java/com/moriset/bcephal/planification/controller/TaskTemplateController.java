package com.moriset.bcephal.planification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskBrowserData;
import com.moriset.bcephal.task.service.TaskTemplateService;

@RestController
@RequestMapping("/planification/task-template")
public class TaskTemplateController extends BaseController<Task, TaskBrowserData>{

	@Autowired
	TaskTemplateService taskTemplateService;
	
	@Override
	protected TaskTemplateService getService() {
		return taskTemplateService;
	}
	
}
