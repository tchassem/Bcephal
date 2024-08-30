/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ArchiveGridQueryBuilder extends ReportGridQueryBuilder {

	
	public ArchiveGridQueryBuilder(GrilleDataFilter filter) {
		super(filter);
	}
	
	@Override
	protected boolean isReport() {
		return getFilter().getGrid().isConsolidated();
	}
	
	public String buildDeleteQuery() {	
		String sql = "DELETE ";
		String from = "FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
		String where = buildWherePart();
		sql += from + " " + where;
		return sql;
	}

	public String buildBackupQuery() {
		setAddOidOfEachRow(!getFilter().getGrid().isConsolidated());
		attributes = new HashMap<>();
		measures = new HashMap<>();
		periods = new HashMap<>();
		String table = "A";
		String sql = "SELECT *";				
		String where = buildWherePart(false);
		String whereOperationForFilter = buildWherePartOperationForFilter();
		String from = buildFromPart(table);	
		sql += from + " " + where ;//+ " ORDER BY ID ";
		if(StringUtils.hasText(whereOperationForFilter)) {
			sql = "SELECT * FROM ( "+ sql + " ) as AAA "+ whereOperationForFilter;
		}
		return sql;
	}
	
	
	protected String buildSelectPartForBackup() {
		String selectPart = "SELECT distinct ";
		boolean isReport = isReport();
		if(!isReport) {
			selectPart = "SELECT ";
		}
		
		selectPart += UniverseParameters.USERNAME;		
		String coma = ",";
		selectPart += coma + UniverseParameters.SOURCE_TYPE;
		selectPart += coma + UniverseParameters.SOURCE_ID;
		selectPart += coma + UniverseParameters.SOURCE_NAME;
		selectPart += coma + UniverseParameters.EXTERNAL_SOURCE_TYPE;
		selectPart += coma + UniverseParameters.EXTERNAL_SOURCE_REF;
		selectPart += coma + UniverseParameters.EXTERNAL_SOURCE_ID;
		selectPart += coma + UniverseParameters.ISREADY;
		selectPart += coma + UniverseParameters.INCREMENTAL;
		selectPart += coma + UniverseParameters.FREE_1;
		selectPart += coma + UniverseParameters.FREE_2;
		selectPart += coma + UniverseParameters.FREE_3;
		selectPart += coma + UniverseParameters.FREE_4;
		selectPart += coma + UniverseParameters.FREE_5;
				
		boolean isEmptyGrid = true;
		setMeasureColumnCount(0);
		this.measureColumn = null;
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			String col = column.getUniverseTableColumnName();
			if (col == null) continue;
			
			if (isReport && column.isMeasure()) {				
				String measurePart = buildMeasureColumSelectPart(column, col);
				selectPart += coma + measurePart;
				this.measureColumnCount++;
				this.measureColumn = column;
				Measure persistent = (Measure)column.getDimension();
				this.measures.put(persistent.getId(), persistent);
			}			
			else if (column.isPeriod()) {
				Period persistent = (Period)column.getDimension();
				this.periods.put(persistent.getId(), persistent);
				String periodPart = buildPeriodColumSelectPart(column, col);
				selectPart += coma + periodPart;
				
			}
			else {
				if (column.isAttribute()) {
					Attribute persistent = (Attribute)column.getDimension();
					this.attributes.put(persistent.getId(), persistent);
				}
				else if (column.isMeasure()) {
					Measure persistent = (Measure)column.getDimension();
					this.measures.put(persistent.getId(), persistent);
				}
				
				selectPart += coma + col;
			}
			coma = ", ";
			if(isEmptyGrid) {
				isEmptyGrid = false;
			}
		}
		if(isEmptyGrid) {
			return null;
		}
		
		return selectPart;
	}
	

}
