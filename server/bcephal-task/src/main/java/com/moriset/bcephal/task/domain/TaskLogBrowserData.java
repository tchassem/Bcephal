package com.moriset.bcephal.task.domain;

import com.moriset.bcephal.domain.BrowserData;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TaskLogBrowserData extends BrowserData {

	@Enumerated(EnumType.STRING) 
	private TaskLogType type;
	
	private String username;
	
	public TaskLogBrowserData(TaskLog log) {
		super(log);
		setType(log.getType());
		setUsername(log.getUsername());
	}
	
}
