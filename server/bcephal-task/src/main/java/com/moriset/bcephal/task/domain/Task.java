package com.moriset.bcephal.task.domain;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "Task")
@Table(name = "BCP_TASK")
@Data
@EqualsAndHashCode(callSuper = false)
public class Task extends MainObject {
	
	private static final long serialVersionUID = 7266527265033543492L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
	@SequenceGenerator(name = "task_seq", sequenceName = "task_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long sequenceId;
	
	private String code;
	
	private Long templateId;
	
	private String description;
	
	@Enumerated(EnumType.STRING) 
	private TaskCategory category;
	
	@Enumerated(EnumType.STRING) 
	private TaskStatus status;
	
	@Enumerated(EnumType.STRING) 
	private TaskNature nature;
	
	private int serieNbr;
	
	private Long userId;
	
	private String username;
	
	@Embedded		
	private PeriodValue byWhen;
	
	private boolean sendNotice;
		
	private String linkedFunctionality;
	
	private Long linkedObjectId;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column
	private Timestamp deadline;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "taskId")
	private List<TaskAudience> audiences;
	@Transient 
	private ListChangeHandler<TaskAudience> audienceListChangeHandler;
	
	
	public Task() {
		this.audienceListChangeHandler = new ListChangeHandler<>();
		this.category = TaskCategory.STANDARD;
		this.status = TaskStatus.DRAFT;
		this.nature = TaskNature.LAST;
		this.byWhen = new PeriodValue();
	}
	
	
	public void setAudiences(List<TaskAudience> audiences) {
		this.audiences = audiences;
		audienceListChangeHandler.setOriginalList(audiences);
	}
	
	public String getCode(){
		if(code == null && getId() != null) {
			code = "" + getId();
		}
		return code;
	}
	
	@PostLoad
	public void initListChangeHandler() {
		audiences.size();
		audienceListChangeHandler.setOriginalList(audiences);
	}
	
	
	
	@Override
	public Task copy() {
		Task copy = new Task();
		copy.setName("Copy " + getName());
		copy.setGroup(getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setStatus(status);
		copy.setCategory(category);
		copy.setNature(nature);
		copy.setByWhen(byWhen.copy());
		copy.setUserId(userId);
		copy.setUsername(username);
		copy.setLinkedFunctionality(linkedFunctionality);
		copy.setLinkedObjectId(linkedObjectId);
		copy.setDeadline(deadline);
		copy.setDescription(description);
		for(TaskAudience audience : audienceListChangeHandler.getItems()) {
			copy.getAudienceListChangeHandler().addNew(audience.copy());
		}
		return copy;
	}
	
}
