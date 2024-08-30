package com.moriset.bcephal.archive.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ArchiveConfigEditorData extends EditorData<ArchiveConfiguration> {

	private List<Nameable> matGrids;
	
	public ArchiveConfigEditorData() {
		super();
		matGrids = new ArrayList<>(0);
	}
	
	public ArchiveConfigEditorData(EditorData<ArchiveConfiguration> data) {
		super(data);
		matGrids = new ArrayList<>(0);
	}
}
