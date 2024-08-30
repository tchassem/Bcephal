package com.moriset.bcephal.planification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.task.domain.TaskLogItem;
import com.moriset.bcephal.task.domain.TaskLogItemBrowserData;
import com.moriset.bcephal.task.service.TaskLogItemService;

@RestController
@RequestMapping("/planification/task-log-item")
public class TaskLogItemController extends BaseController<TaskLogItem, TaskLogItemBrowserData>{

	@Autowired
	TaskLogItemService taskService;
	
	@Override
	protected TaskLogItemService getService() {
		return taskService;
	}
	
}
