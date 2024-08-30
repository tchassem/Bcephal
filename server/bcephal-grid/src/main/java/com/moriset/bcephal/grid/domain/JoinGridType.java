package com.moriset.bcephal.grid.domain;

import com.moriset.bcephal.domain.DataSourceType;

public enum JoinGridType {
	REPORT_GRID,
	GRID,
	JOIN,
	MATERIALIZED_GRID,
	UNION_GRID;
	
	
	public boolean isInputGrid() {
		return this == GRID;
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
	
	
	public DataSourceType GetDataSource() {
		return this == MATERIALIZED_GRID ? DataSourceType.MATERIALIZED_GRID 
				: this == JOIN ? DataSourceType.JOIN 
						: this == REPORT_GRID ? DataSourceType.REPORT_GRID 
								: this == GRID ? DataSourceType.INPUT_GRID 
										: DataSourceType.UNIVERSE;
	}
	
	public static JoinGridType GetJoinGridType(DataSourceType dataSourceType) {
		return dataSourceType == DataSourceType.MATERIALIZED_GRID ? MATERIALIZED_GRID 
				: dataSourceType == DataSourceType.JOIN ? JOIN 
						: dataSourceType == DataSourceType.REPORT_GRID ? REPORT_GRID 
								: dataSourceType == DataSourceType.INPUT_GRID ? GRID 
										: null;
	}
}
