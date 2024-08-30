/**
 * 
 */
package com.moriset.bcephal.grid.service;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;

/**
 * @author Joseph Wambo
 *
 */
public class ReportGridPublicationQueryBuilder extends ReportGridQueryBuilder {

	public ReportGridPublicationQueryBuilder(GrilleDataFilter filter) {
		super(filter);
	}
	
	@Override
	protected String buildMeasureColumSelectPart(GrilleColumn column, String col) {
		String functions = StringUtils.hasText(column.getDimensionFunction()) ? column.getDimensionFunction() : MeasureFunctions.SUM.code;
		boolean isCount = functions.toUpperCase().equals(MeasureFunctions.COUNT.code);
		String name = col;		
		if(isCount){
			col = !StringUtils.hasText(col) ? "1" : col;
			return functions.concat("(").concat(col).concat(") AS ").concat(name);
		}
		else {
			return functions.concat("(").concat(col).concat(") AS ").concat(name);
		}
	}
	
	@Override
	protected String buildCustomForReport(String sql, String whereOperationForFilter) {
		String selectPart = "SELECT ";
		String coma = "";
		String groupByPart = "";
		String groupBy = "GROUP BY ";
		String table = "AAA";
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			String col = column.getUniverseTableColumnName();
			col = table.concat(".").concat(col);
			if (col == null) continue;
			if (column.isMeasure()) {
				//String functions = StringUtils.hasText(column.getDimensionFunction()) ? column.getDimensionFunction() : MeasureFunctions.SUM.code;
				//col = col.concat("_").concat(functions);
				String name = column.getUniverseTableColumnName();
				String measurePart = "SUM(".concat(col).concat(") AS ").concat(name);
				selectPart += coma + measurePart;
			}
			else {
				selectPart += coma + col;
				groupByPart += groupBy + col;
				groupBy = ", ";
			}
			coma = ", ";
		}
		String order = buildOrderPart(table);
		
		if(addOidOfEachRow) {
			String col = table.concat(".").concat(UniverseParameters.ID);
			selectPart += coma + col;
			groupByPart += groupBy + col;
		}
		else{
			selectPart += coma + "row_number() OVER() as id";
		}
		
		sql = selectPart + " FROM ( "+ sql +" ) as " + table + " " + whereOperationForFilter + " " + groupByPart  + " " + order;
		return sql;
	}

}
