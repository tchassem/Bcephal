package com.moriset.bcephal.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RightEditorData<P> extends EditorData<P>{

	private List<Nameable> items;
	
	private List<String> lowRightLevels;
}
