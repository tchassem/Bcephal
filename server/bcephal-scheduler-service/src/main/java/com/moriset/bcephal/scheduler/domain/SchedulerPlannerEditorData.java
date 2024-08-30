package com.moriset.bcephal.scheduler.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPlannerEditorData extends EditorData<SchedulerPlanner> {

	private List<Nameable> routines;
	
	private List<Nameable> joins;
	
	private List<Nameable> matGrids;
	
	private List<Nameable> reportGrids;
	
	private List<Nameable> inputGrids;
	
	private List<Nameable> recos;
	
	private List<Nameable> trees;
	
	private List<Nameable> billings;
	
	private List<Nameable> alarms;
	
	private List<Nameable> bookings;
	
	private List<Nameable> fileLoaders;
	
	private List<Nameable> tasks;
	
	private List<Nameable> integrations;
	
	private List<Nameable> presentationTemplates;
	
	public List<String> variables;
	
	private List<AbstractSmartGrid<?>> grids;
	
	public SchedulerPlannerEditorData() {
		super();
		routines = new ArrayList<>(0);
		recos = new ArrayList<>(0);
		joins = new ArrayList<>(0);
		matGrids = new ArrayList<>(0);
		reportGrids = new ArrayList<>(0);
		inputGrids = new ArrayList<>(0);
		trees = new ArrayList<>(0);
		billings = new ArrayList<>(0);
		alarms = new ArrayList<>(0);
		bookings = new ArrayList<>(0);
		fileLoaders = new ArrayList<>(0);
		tasks = new ArrayList<>(0);
		integrations = new ArrayList<>(0);
		presentationTemplates = new ArrayList<>(0);
		grids = new ArrayList<>(0);
	}
	
	public SchedulerPlannerEditorData(EditorData<SchedulerPlanner> data) {
		super(data);
		routines = new ArrayList<>(0);
		recos = new ArrayList<>(0);
		joins = new ArrayList<>(0);
		matGrids = new ArrayList<>(0);
		reportGrids = new ArrayList<>(0);
		inputGrids = new ArrayList<>(0);
		trees = new ArrayList<>(0);
		billings = new ArrayList<>(0);
		alarms = new ArrayList<>(0);
		bookings = new ArrayList<>(0);
		fileLoaders = new ArrayList<>(0);
		tasks = new ArrayList<>(0);
		integrations = new ArrayList<>(0);
		presentationTemplates = new ArrayList<>(0);
		grids = new ArrayList<>(0);
	}
	
}
