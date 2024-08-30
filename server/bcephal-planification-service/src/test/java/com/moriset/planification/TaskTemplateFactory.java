package com.moriset.planification;


import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskCategory;
import com.moriset.bcephal.task.domain.TaskStatus;

public class TaskTemplateFactory {
	
	
	public static Task BuildtaskTemplate () {
		Task taskTemplate = new Task();
		taskTemplate.setName("EOM voorbereiding");
		taskTemplate.setCategory(TaskCategory.SCHEDULED);
		taskTemplate.setStatus(TaskStatus.DRAFT);
		return taskTemplate;
	
	}

}
