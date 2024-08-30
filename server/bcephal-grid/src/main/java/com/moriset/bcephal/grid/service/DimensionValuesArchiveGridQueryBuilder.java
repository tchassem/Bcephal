/**
 * 
 */
package com.moriset.bcephal.grid.service;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.repository.ParameterRepository;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DimensionValuesArchiveGridQueryBuilder extends ArchiveGridQueryBuilder {

	/**
	 * 
	 */
	private DimensionDataFilter filter;
	
	protected ParameterRepository parameterRepository;
	
	public DimensionValuesArchiveGridQueryBuilder(DimensionDataFilter filter) {
		super(filter);
		this.filter = filter;
	}
	
	Attribute getAttribute() {
		Attribute attrib = new Attribute(filter.getDimensionId());
		attrib.setDataSourceId(filter.getDataSourceId());
		attrib.setDataSourceType(filter.getDataSourceType());
		return attrib;
	}
	
	Measure getMeasure() {
		Measure attrib = new Measure(filter.getDimensionId());
		attrib.setDataSourceId(filter.getDataSourceId());
		attrib.setDataSourceType(filter.getDataSourceType());
		return attrib;
	}
	
	Period getPeriod() {
		Period attrib = new Period(filter.getDimensionId());
		attrib.setDataSourceId(filter.getDataSourceId());
		attrib.setDataSourceType(filter.getDataSourceType());
		return attrib;
	}
	
	Dimension getDimension() {
		if(DimensionType.MEASURE.equals(filter.getDimensionType())) {
			return getMeasure();
		}
		if(DimensionType.PERIOD.equals(filter.getDimensionType())) {
			return getPeriod();
		}
		return getAttribute();
	}

	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT DISTINCT " + getDimension().getUniverseTableColumnName() + " ";
		return selectPart;
	}	
	
	@Override
	protected String buildOrderPart() {
		return " ORDER BY " + getDimension().getUniverseTableColumnName() + " ASC ";
	}
}
