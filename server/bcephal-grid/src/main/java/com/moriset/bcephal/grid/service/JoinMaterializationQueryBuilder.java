/**
 * 
 */
package com.moriset.bcephal.grid.service;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.service.filters.ISpotService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class JoinMaterializationQueryBuilder extends JoinQueryBuilder {
			
	
	public JoinMaterializationQueryBuilder(JoinFilter filter, JoinColumnRepository joinColumnRepository, ISpotService spotService) {
		super(filter, joinColumnRepository,spotService);
	}
		
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";
		for (JoinColumn column : filter.getJoin().getColumns()) {
			String col = column.isCustom() ? column.getCustomCol(true, parameters, filter.getJoin(), spotService, filter.getVariableValues())
					: column.getDbColName(true, true);
			if (!StringUtils.hasText(col)) {
				continue;
			}
			selectPart += coma + col;
			coma = ", ";
		}
		if (addMainGridOid) {
			String col = getMainGridOidCol();
			if (col != null) {
				selectPart += coma + col;
				coma = ", ";
			}
		}
		else {
			selectPart += coma + "row_number() over () as id";
		}
		return selectPart;
	}
	
	
}
