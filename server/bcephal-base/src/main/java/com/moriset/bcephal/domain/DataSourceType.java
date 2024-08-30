package com.moriset.bcephal.domain;

public enum DataSourceType {
	
	INPUT_GRID,
	REPORT_GRID,	
	MATERIALIZED_GRID,
	JOIN,	
	UNIVERSE,
	UNION_GRID;
	
	public boolean isInputGrid() {
		return this == INPUT_GRID;
	}
	
	public boolean isReportGrid() {
		return this == REPORT_GRID;
	}
	
	public boolean isMaterializedGrid() {
		return this == MATERIALIZED_GRID;
	}
	
	public boolean isJoin() {
		return this == JOIN;
	}
	
	public boolean isUnionGrid() {
		return this == UNION_GRID;
	}
	
	public boolean isUniverse() {
		return this == UNIVERSE;
	}
	
}
