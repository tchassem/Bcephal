package com.moriset.bcephal.settings.domain;

import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IncrementalNumberEditorData extends EditorData<IncrementalNumber> {

	private List<String> variables;
	
}
