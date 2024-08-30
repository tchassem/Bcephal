package com.moriset.bcephal.loader.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserLoaderEditorData extends EditorData<UserLoader> {
	
	public List<Nameable> fileLoaders;
	
	public List<Nameable> schedulers;
	
	public List<String> fileExtensions;

	public UserLoaderEditorData() {
		super();
		schedulers = new ArrayList<>(0);
		fileLoaders = new ArrayList<>(0);
		fileExtensions = List.of("CSV", "TXT", "XLS", "XLSX");
	}
	
	public UserLoaderEditorData(EditorData<UserLoader> data) {
		super(data);
		schedulers = new ArrayList<>(0);
		fileLoaders = new ArrayList<>(0);
	}
	
}
