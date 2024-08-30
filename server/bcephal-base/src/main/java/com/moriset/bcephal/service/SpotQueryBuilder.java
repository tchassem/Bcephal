/**
 * 
 */
package com.moriset.bcephal.service;

import java.util.List;

import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SpotQueryBuilder extends QueryBuilder {

	private Spot spot;
	List<UniverseFilter> filters;
	
	public SpotQueryBuilder(Spot spot, List<UniverseFilter> filters) {		
		super();
		this.spot = spot;
		this.filters = filters;
		if(spot.getGridType().isMaterializedGrid()) {
			tableName = "MATERIALIZED_GRID_" + spot.getGridId();
		}
	}	
		
	@Override
	public String buildQuery() {		
		return this.getSpot().buildQuery(tableName, filters);
	}
	
	
}
