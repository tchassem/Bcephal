/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.PeriodGrouping;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author B-Cephal Team
 *
 *         29 mars 2021
 */
@Inheritance(strategy = InheritanceType.JOINED)
@Entity(name = "GrilleColumn")
@Table(name = "BCP_GRID_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class GrilleColumn extends Persistent {

	private static final long serialVersionUID = -3541296295134746100L;

	public static final String DEFAULT_SQL_COL_VAR_NAME = "item";
		
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grid_column_seq")
	@SequenceGenerator(name = "grid_column_seq", sequenceName = "grid_column_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	

	private String name;
	
	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	@Enumerated(EnumType.STRING)
	private HorizontalAlignment alignment;
	
	private Long dimensionId;
	
	private String dimensionName;
	
	private String dimensionFunction;
	
	private String dimensionFormat;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grid")
	private Grille grid;
	
	@Enumerated(EnumType.STRING) 
	private GrilleColumnCategory category;
	
	private boolean mandatory;
	
	private boolean editable;	
	
	private boolean showValuesInDropList;
	
	private int position;

	private boolean show;	
	
	private Integer backgroundColor;

	private Integer foregroundColor;
	
	private Integer width;
	
	private String fixedType;
		
	private String defaultStringValue;
	
	private BigDecimal defaultDecimalValue;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date defaultDateValue;
	
	private boolean applyDefaultValueIfCellEmpty;
	
	private boolean applyDefaultValueToFutureLine;
	
	
	@Embedded	
	private DimensionFormat format;
	
	@Enumerated(EnumType.STRING) 
	private PeriodGrouping groupBy;
	
	@Transient
	private Boolean orderAsc;
	
	@Transient @JsonIgnore
	private DataSourceType dataSourceType;
	
	@Transient @JsonIgnore
	private Long dataSourceId;
	
	@Transient @JsonIgnore
	private String alias;
	
	@Transient @JsonIgnore
	private CalculatedMeasure calculatedMeasure;
	

	public GrilleColumn() {
		this.show = true;	
		this.category = GrilleColumnCategory.USER;
		this.mandatory = false;
		this.format = new DimensionFormat();
		this.editable = true;
		this.dataSourceType = DataSourceType.UNIVERSE;
	}

	public GrilleColumn(Long id, String name, DimensionType type, Long dimensionId, String dimensionName) {
		this();
		this.id = id;
		this.name = name;
		this.type = type;
		this.dimensionId = dimensionId;
		this.dimensionName = dimensionName;
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
	
	@JsonIgnore
	public Object getDefaultValue() {
		if(isAttribute()) {
			return getDefaultStringValue();
		}
		else if(isMeasure()) {
			return getDefaultDecimalValue();
		}
		else if(isPeriod()) {
			return getDefaultDateValue();
		}
		return null;
	}

	@JsonIgnore
	public String getUniverseTableColumnName() {
		if(isAttribute()) {
			return new Attribute(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		}
		else if(isMeasure()) {
			return new Measure(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		}
		else if(isPeriod()) {
			return new Period(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		}
		else if(isCalculatedMeasure() && calculatedMeasure != null) {
			return calculatedMeasure.toSql();
		}
		return null;
	}
	
	@JsonIgnore
	public String getUniverseTableColumnType() {
		if(isAttribute()) {
			return new Attribute().getUniverseTableColumnType();
		}
		else if(isMeasure()) {
			return new Measure().getUniverseTableColumnType();
		}
		else if(isPeriod()) {
			return new Period().getUniverseTableColumnType();
		}
		else if(isCalculatedMeasure()) {
			return new Measure().getUniverseTableColumnType();
		}
		return null;
	}
	
	@JsonIgnore
	public Dimension getDimension() {
		if(isAttribute()) {
			return new Attribute(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId());
		}
		else if(isMeasure()) {
			return new Measure(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId());
		}
		else if(isPeriod()) {
			return new Period(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId());
		}
		return null;
	}
	
	@JsonIgnore
	public String buildDbColumnAlias() {
		return "column_" + getType() + "_" + getDimensionId();
	}
	
	
	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}

	@JsonIgnore
	@Override
	public GrilleColumn copy() {
		GrilleColumn copy = new GrilleColumn();
		copy.setName(this.getName());
		copy.setType(type);
		copy.setDimensionId(dimensionId);
		copy.setDimensionName(dimensionName);
		copy.setDimensionFunction(dimensionFunction);
		copy.setDimensionFormat(dimensionFormat);
		copy.setCategory(category);
		copy.setMandatory(mandatory);
		copy.setEditable(editable);	
		copy.setShowValuesInDropList(showValuesInDropList);
		copy.setPosition(position);
		copy.setShow(show);	
		copy.setBackgroundColor(backgroundColor);
		copy.setForegroundColor(foregroundColor);
		copy.setWidth(width);
		copy.setFixedType(fixedType);
		copy.setDefaultStringValue(defaultStringValue);
		copy.setDefaultDecimalValue(defaultDecimalValue);
		copy.setDefaultDateValue(defaultDateValue);
		copy.setApplyDefaultValueIfCellEmpty(applyDefaultValueIfCellEmpty);
		copy.setApplyDefaultValueToFutureLine(applyDefaultValueToFutureLine);
		copy.setFormat(format != null ? format.copy() : null);
		copy.setGroupBy(getGroupBy());
		return copy;
	}



}
