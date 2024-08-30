package com.moriset.bcephal.grid.service;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridCopyData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MaterializedGridCopyQueryBuilder extends MaterializedGridQueryBuilder {

	MaterializedGridCopyData data;
	MaterializedGrid targetGrid;
	
	public MaterializedGridCopyQueryBuilder(MaterializedGridCopyData data, MaterializedGrid targetGrid) {
		super(data.getFilter());
		setData(data);
		setConsolidate(false);
		setTargetGrid(targetGrid);
	}
	
	@Override
	public String buildQuery() {
		String sql = buildSelectPart();
		if(StringUtils.hasText(sql)){
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if(StringUtils.hasText(wherePart)){
				sql += wherePart;
			}	
			if(isConsolidate()) {
				String groupBy = buildGroupByPart();			
				if(StringUtils.hasText(groupBy)){
					sql += groupBy;
				}	
			}
		}
		String query = "INSERT INTO " + targetGrid.getMaterializationTableName();		
		String coma = " (";	
		for (MaterializedGridColumn column : targetGrid.getColumns()) {
			query += coma + column.getDbColumnName();
			coma = ", ";
		}
		query += ") " + sql;
		return query;
	}
	
	@Override
	public String buildDeleteQuery() {		
		String sql = "DELETE ";
		if(StringUtils.hasText(sql)){
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if(StringUtils.hasText(wherePart)){
				sql += wherePart;
			}
		}
		return sql;
	}
	
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";	
		int i = 0;
		for (MaterializedGridColumn column : getFilter().getGrid().getColumns()) {
			String col = column.getId() != null && column.isPublished() ? column.getDbColumnName() : "null as nullcol" + i++;
			if (!StringUtils.hasText(col)) {
				continue;
			}
			selectPart += coma + col;
			coma = ", ";
		}
		return selectPart;
	}

}
