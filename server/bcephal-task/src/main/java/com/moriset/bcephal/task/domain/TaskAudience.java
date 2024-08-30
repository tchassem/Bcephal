package com.moriset.bcephal.task.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "TaskAudience")
@Table(name = "BCP_TASK_AUDIENCE")
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskAudience extends Persistent {

	private static final long serialVersionUID = 7329302417350133073L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_audience_seq")
	@SequenceGenerator(name = "task_audience_seq", sequenceName = "task_audience_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "taskId")
	private Task taskId;
	
	@Enumerated(EnumType.STRING) 
	private TaskAudienceType type;
	
	private Long objectId;
	
	private int position;
	
	public TaskAudience() {
		this.type = TaskAudienceType.ALL;
	}

	@Override
	public TaskAudience copy() {
		TaskAudience copy = new TaskAudience();
		copy.setType(type);
		copy.setObjectId(objectId);
		copy.setPosition(position);
		return copy;
	}
	
}
