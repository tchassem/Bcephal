/**
 * 
 */
package com.moriset.bcephal.grid.service;

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
public class ReconciliationGridQueryBuilder extends ReportGridQueryBuilder {

	
	public ReconciliationGridQueryBuilder(GrilleDataFilter filter) {
		super(filter);
	}
	
	@Override
	protected boolean isReport() {
		return false;
	}
	
	@Override
	protected String getColumnNameForDetails(GrilleColumn column) {
		String col = column.getUniverseTableColumnName();
		return col;
	}

}
