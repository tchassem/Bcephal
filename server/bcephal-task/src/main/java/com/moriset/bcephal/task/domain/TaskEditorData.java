package com.moriset.bcephal.task.domain;

import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TaskEditorData extends EditorData<Task> {

	public List<Nameable> sequences;
	
	public List<Nameable> users;
	
	public List<Nameable> profils;
	
}
