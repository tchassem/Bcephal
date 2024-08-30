/**
 * 
 */
package com.moriset.bcephal.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class EditorData<P> {

	private P item;

	private List<Model> models;

	private List<Measure> measures;

	private List<Period> periods;
	
	public List<Nameable> calculatedMeasures;

	public List<Nameable> calendarCategories;

	public List<Nameable> spots;
	
	public List<Nameable> templates;

	public EditorData() {
		models = new ArrayList<>(0);
		measures = new ArrayList<>(0);
		calculatedMeasures = new ArrayList<>(0);
		periods = new ArrayList<>(0);
		calendarCategories = new ArrayList<>(0);
		spots = new ArrayList<>(0);
	}
	
	public EditorData(EditorData<P> data) {
		models = data.getModels();
		measures = data.getMeasures();
		periods = data.getPeriods();
		calendarCategories = data.getCalendarCategories();
		spots = data.getSpots();
		item = data.getItem();
	}

}
