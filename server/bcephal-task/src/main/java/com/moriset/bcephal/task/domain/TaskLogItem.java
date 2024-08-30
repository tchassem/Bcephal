package com.moriset.bcephal.task.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.MainObject;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "TaskLogItem")
@Table(name = "BCP_TASK_LOG_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskLogItem extends MainObject {
	
	private static final long serialVersionUID = -4887093368405961285L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_log_item_seq")
	@SequenceGenerator(name = "task_log_item_seq", sequenceName = "task_log_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long logId;
	
	@JsonIgnore
	private Long taskId;
	
	@Enumerated(EnumType.STRING) 
	private TaskLogItemType type;
		
	private String fileName;
	
	private String oldValue;
	
	private String newValue;
	
	private String username;

	@Override
	public TaskLogItem copy() {
		TaskLogItem copy = new TaskLogItem();
		copy.setName(getName());
		copy.setDescription(getDescription());
		copy.setTaskId(taskId);
		copy.setType(type);
		copy.setFileName(fileName);
		copy.setOldValue(oldValue);
		copy.setNewValue(newValue);
		copy.setCreationDate(getCreationDate());
		copy.setModificationDate(getModificationDate());
		return copy;
	}

}
