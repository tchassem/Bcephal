package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodGrouping;

import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractSmartGridColumn  extends Persistent {
	
	private static final long serialVersionUID = -1994139856885162167L;

	private String name;	
	
	@Enumerated(EnumType.STRING)
	private DimensionType type;	
	
	@Enumerated(EnumType.STRING)
	private HorizontalAlignment alignment;
	
	private Long dimensionId;
					
	private int position;

	private boolean show;	
	
	private Integer backgroundColor;

	private Integer foregroundColor;
	
	private Integer width;
	
	private String fixedType;
	
	@Embedded	
	private DimensionFormat format;
	
	@Enumerated(EnumType.STRING) 
	private PeriodGrouping groupBy;
	
	@Transient
	private JoinGridType gridType;
	
	
	public AbstractSmartGridColumn(){
		this.show = true;
	}
	
	public abstract Long getParentId();
	
	@JsonIgnore
	public abstract String getDbColumnName();
	
	@JsonIgnore
	public String getUniverseColumnName() {
		if(dimensionId != null && type != null) {
			GrilleColumn column = new GrilleColumn();
			column.setDimensionId(dimensionId);
			column.setType(type); 
			return column.getUniverseTableColumnName();
		}
		return null;
	}
	
	@JsonIgnore
	public boolean isAttribute() {
		return getType() != null && getType().isAttribute();
	}
	
	@JsonIgnore
	public boolean isMeasure() {
		return getType() != null && getType().isMeasure();
	}

	@JsonIgnore
	public boolean isCalculatedMeasure() {
		return getType() != null && getType().isCalculatedMeasure();
	}
	
	@JsonIgnore
	public boolean isPeriod() {
		return getType() != null && getType().isPeriod();
	}
	
	public abstract String getGridName();
		
	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}

	@Override
	public Persistent copy() {		
		return null;
	}
	
}
