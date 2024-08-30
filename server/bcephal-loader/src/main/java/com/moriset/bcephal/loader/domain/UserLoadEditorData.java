package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.domain.EditorData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserLoadEditorData extends EditorData<UserLoad> {

	private UserLoader userLoader;

	public UserLoadEditorData() {
		super();
	}
	
	public UserLoadEditorData(EditorData<UserLoad> data) {
		super(data);
	}
	
}
