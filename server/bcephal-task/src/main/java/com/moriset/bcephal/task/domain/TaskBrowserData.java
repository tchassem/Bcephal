package com.moriset.bcephal.task.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TaskBrowserData extends BrowserData {

	private String code;	
	
	private TaskCategory category;
	
	private TaskStatus status;
	
	private TaskNature nature;
	
	private int serieNbr;
	
	private String username;	
			
	private String linkedFunctionality;
	
	private Long linkedObjectId;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp deadline;
	
	public TaskBrowserData(Task task){
		super(task);
		this.username = task.getUsername();
		this.code = task.getCode();
		this.linkedFunctionality = task.getLinkedFunctionality();
		this.linkedObjectId = task.getLinkedObjectId();
		this.deadline = task.getDeadline();
		this.serieNbr = task.getSerieNbr();
		this.status = task.getStatus();
		this.category = task.getCategory();
		this.nature = task.getNature();
	}
	
}
