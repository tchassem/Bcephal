/**
 * 
 */
package com.moriset.bcephal.task.service;

import java.util.Locale;

import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskCategory;
import com.moriset.bcephal.task.domain.TaskEditorData;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;

/**
 * @author MORISET-004
 *
 */
@Service
public class TaskTemplateService extends TaskService {
		
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.TASK_TEMPLATE;
	}

	
	@Override
	public TaskEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		TaskEditorData data = super.getEditorData(filter, session, locale);
		data.setSequences(incrementalNumberRepository.getAllIncrementalNumbers());
		return data;
	}

	@Override
	protected Task getNewItem() {
		Task task = new Task();
		task.setCategory(TaskCategory.SCHEDULED);
		String baseName = "Task template ";
		int i = 1;
		task.setName(baseName + i);
		while(getByName(task.getName()) != null) {
			i++;
			task.setName(baseName + i);
		}
		return task;
	}
	

}
