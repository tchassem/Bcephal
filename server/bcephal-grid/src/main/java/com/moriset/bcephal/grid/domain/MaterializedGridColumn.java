package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodGrouping;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "MaterializedGridColumn")
@Table(name = "BCP_MATERIALIZED_GRID_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class MaterializedGridColumn extends Persistent {
	
	private static final long serialVersionUID = 3202306866415342764L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mat_grid_column_seq")
	@SequenceGenerator(name = "mat_grid_column_seq", sequenceName = "mat_grid_column_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	

	private String name;
	
	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	@Enumerated(EnumType.STRING)
	private HorizontalAlignment alignment;
		
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grid")
	private MaterializedGrid grid;
	
	@Enumerated(EnumType.STRING) 
	private GrilleColumnCategory category;
	
	@Enumerated(EnumType.STRING)
	private GrilleColumnCategory role;
	
	private Long dimensionId;
	
	private boolean mandatory;
	
	private boolean editable;	
	
	private boolean published;
		
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
	private Boolean orderAsc;
	
	@Transient @JsonIgnore
	private String alias;
	
	@Transient @JsonIgnore
	private String dimensionFunction;
	
	@Transient @JsonIgnore
	private CalculatedMeasure calculatedMeasure;

	public MaterializedGridColumn() {
		this.category = GrilleColumnCategory.USER;
		this.role = GrilleColumnCategory.NONE;
		this.groupBy = PeriodGrouping.DAY_OF_MONTH;
		this.format = new DimensionFormat();
		this.show = true;
	}
	
	public MaterializedGridColumn(Long id) {
		this();
		this.setId(id);
	}
	
	public MaterializedGridColumn(GrilleColumn column) {
		this();
		this.setName(column.getName());
		this.setType(column.getType());	
		this.setPosition(column.getPosition());
		this.setDimensionId(column.getDimensionId());
	}
	
	public MaterializedGridColumn(GrilleColumnCategory category, DimensionType type, String name, int position) {
		this();
		this.category = category;
		this.type = type;
		this.name = name;
		this.position = position;
	}
	
	@JsonIgnore
	public String getDbColumnName() {
		if(isCalculatedMeasure()) {
			return calculatedMeasure.toSql();
		}
		return "column" + getId();
	}
	
	@JsonIgnore
	public String buildDbColumnAlias() {
		return "column_" + getId();
	}
	
	@JsonIgnore
	public String getDbColumnType() {
		return isPeriod() ? "DATE" : isMeasure() ? "DECIMAL(31, 14)" : "TEXT";
	}
	
	@JsonIgnore
	public boolean isAttribute() {
		return type == DimensionType.ATTRIBUTE;
	}
	
	@JsonIgnore
	public boolean isMeasure() {
		return type == DimensionType.MEASURE;
	}
	
	@JsonIgnore
	public boolean isCalculatedMeasure() {
		return type == DimensionType.CALCULATED_MEASURE;
	}
	
	@JsonIgnore
	public boolean isPeriod() {
		return type == DimensionType.PERIOD;
	}
		
	@JsonIgnore
	public boolean isLoadNbr() {
		return getCategory() == GrilleColumnCategory.LOAD_NBR;
	}
	
	@JsonIgnore
	public boolean isOperationCode() {
		return getCategory() == GrilleColumnCategory.OPERATION_CODE;
	}
	
	@JsonIgnore
	public boolean isLoadDate() {
		return getCategory() == GrilleColumnCategory.LOAD_DATE;
	}
	
	@JsonIgnore
	public boolean isLoadSourceName() {
		return getCategory() == GrilleColumnCategory.LOAD_SOURCE_NAME;
	}
	
	@JsonIgnore
	public boolean isLoadMode() {
		return getCategory() == GrilleColumnCategory.LOAD_MODE;
	}
	
	@JsonIgnore
	public boolean isLoadUser() {
		return getCategory() == GrilleColumnCategory.LOAD_USER;
	}
		
	
	@Override
	public MaterializedGridColumn copy() {
		MaterializedGridColumn copy = new MaterializedGridColumn();
		copy.setName(this.getName());
		copy.setType(type);
		copy.setCategory(category);
		copy.setMandatory(mandatory);
		copy.setEditable(editable);	
		copy.setPosition(position);
		copy.setShow(show);	
		copy.setPublished(false);
		copy.setBackgroundColor(backgroundColor);
		copy.setForegroundColor(foregroundColor);
		copy.setWidth(width);
		copy.setFixedType(fixedType);
		copy.setFormat(format != null ? format.copy() : null);
		copy.setGroupBy(getGroupBy());
		return copy;
	}
	
	
}
