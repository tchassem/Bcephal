package com.moriset.bcephal.planification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskBrowserData;
import com.moriset.bcephal.task.service.TaskService;

@RestController
@RequestMapping("/planification/task")
public class TaskController extends BaseController<Task, TaskBrowserData>{

	@Autowired
	TaskService taskService;
	
	@Override
	protected TaskService getService() {
		return taskService;
	}
	
}
