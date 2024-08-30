package com.moriset.bcephal.grid.service.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.AbstractSmartGridColumn;
import com.moriset.bcephal.grid.domain.form.FormModelSpotItem;

import lombok.Data;

@Data
public class FormModelSpotData {
	private DataSourceType dataSourceType;
	private Long dataSourceId;
	private Long measureId;
	private String formula;
	private List<FormModelSpotItem> items;
	
	@JsonIgnore
	private AbstractSmartGrid<?> grid;
	
	@JsonIgnore
	private AbstractSmartGridColumn column;
	
	public FormModelSpotData() {
		items = new ArrayList<>();
	}
	
	

	public String buildSql(){
		String col = column.getDbColumnName();
		String table = grid.getDbTableName();
		boolean isMeasure = column.isMeasure();
		String sql = "SELECT " + col + " FROM " + table;
		if(isMeasure && StringUtils.hasText(formula)) {
			sql = "SELECT " + formula + "(" + col + ") FROM " + table;
		}		
		String coma = " WHERE ";
		if(dataSourceType == DataSourceType.UNIVERSE) {
			sql += coma + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.INPUT_GRID.name() + "'";
			if(dataSourceId != null) {
				sql += " AND " + UniverseParameters.SOURCE_ID + " = " + dataSourceId;
			}
			coma = " AND ";
		}
		for(FormModelSpotItem condition : items) {
			String itemSql = condition.buildSql();
			if(StringUtils.hasText(itemSql)) {
				
				boolean isFirst = condition.getPosition() == 0;
				FilterVerb verb = condition.getVerb() != null ? FilterVerb.valueOf(condition.getVerb()) : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = coma;
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);	
				
				
			}
		}
		sql += " LIMIT 1";
		return sql;
	}

	
}
