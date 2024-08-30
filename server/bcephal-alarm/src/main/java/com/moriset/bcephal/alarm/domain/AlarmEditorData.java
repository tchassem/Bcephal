/**
 * 
 */
package com.moriset.bcephal.alarm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

/**
 * @author Joseph Wambo
 *
 */
public class AlarmEditorData extends EditorData<Alarm> {

	public Map<String, List<String>> variables;
	
	public List<Nameable> grids = new ArrayList<>();
	
	public List<Nameable> graphs = new ArrayList<>();
	
	public List<Nameable> spreadsheets = new ArrayList<>();
	
	public AlarmEditorData() {
	}
	
	public AlarmEditorData(EditorData<Alarm> data) {
		super(data);
	}
	
}
