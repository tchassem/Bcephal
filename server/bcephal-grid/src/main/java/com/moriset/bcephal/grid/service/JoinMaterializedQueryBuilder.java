/**
 * 
 */
package com.moriset.bcephal.grid.service;

import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.service.filters.ISpotService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class JoinMaterializedQueryBuilder extends JoinQueryBuilder {
		
	public JoinMaterializedQueryBuilder(JoinFilter filter, JoinColumnRepository joinColumnRepository, ISpotService spotService) {
		super(filter, joinColumnRepository, spotService);
	}
	
	@Override
	protected String buildFromPart() {
		String fromPart = " FROM " + filter.getJoin().getMaterializationTableName();		
		return fromPart;
	}
		
}
