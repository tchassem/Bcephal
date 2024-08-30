package com.moriset.bcephal.task.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TaskLogItemBrowserData extends BrowserData {

	private TaskLogItemType type;
	
	private String fileName;
	
	private String oldValue;
	
	private String newValue;
	
	private String username;
	
	
	public TaskLogItemBrowserData(TaskLogItem log) {
		super(log);
		setType(log.getType());
		setUsername(log.getUsername());
		setFileName(log.getFileName());
		setOldValue(log.getOldValue());
		setNewValue(log.getNewValue());
	}
	
}
