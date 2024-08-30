package com.moriset.bcephal.grid.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.PeriodGrouping;
import com.moriset.bcephal.service.filters.ISpotService;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity(name = "JoinColumn")
@Table(name = "BCP_JOIN_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinColumn extends Persistent {

	private static final long serialVersionUID = -1094810888417832866L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_seq")
	@SequenceGenerator(name = "join_column_seq", sequenceName = "join_column_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "joinId")
	private Join joinId;

	private String name;
	
	@Enumerated(EnumType.STRING) 
	private JoinColumnCategory category;
	
	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	@Enumerated(EnumType.STRING)
	private HorizontalAlignment alignment;
	
	private Long dimensionId;
	
	private String dimensionName;
	
	private String dimensionFunction;
	
	private String dimensionFormat;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType;
	
	private Long gridId;
	
	private Long columnId;
	
	private String columnName;
	
	private boolean show;	
	
	private Integer backgroundColor;

	private Integer foregroundColor;
	
	private Integer width;
	
	private String fixedType;

	private int position;
	
	@Embedded	
	private DimensionFormat format;
	
	@Enumerated(EnumType.STRING) 
	private PeriodGrouping groupBy;
	
	private boolean usedForPublication;
	
	private Long publicationDimensionId;
	
	private String publicationDimensionName;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "properties")
	private JoinColumnProperties properties;
	
	@Transient
	private Boolean orderAsc;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Transient
	private DataSourceType dataSourceType;
	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Transient
	private JoinColumn originalColumn;
	
	public JoinColumn() {
		this.show = true;	
		this.format = new DimensionFormat();
	}
	
	public JoinColumn(Long id) {
		this();
		setId(id);
	}
	
	public JoinColumn(JoinGridType gridType, Long gridId, Long columnId, Long dimensionId, String dimensionName, DimensionType type, DataSourceType dataSourceType) {
		this();
		this.gridType = gridType;
		this.gridId = gridId;
		this.columnId = columnId;
		this.dimensionId = dimensionId;
		this.dimensionName = dimensionName;
		this.type = type;
		this.category = JoinColumnCategory.STANDARD;
		this.dataSourceType = dataSourceType;
	}
	
	
	public JoinColumnCategory getCategory() {
		if(category == null) {
			this.category = JoinColumnCategory.STANDARD;
		}
		return category;
	}
	
	@JsonIgnore
	public boolean canGroupBy() {
		return (isStandard() && !isMeasure())
				|| (isCustom() 
						&& !getProperties().getField().isConcatenate() 
						&& !getProperties().getField().isCalculate() 
						&& !getProperties().getField().isCopy()
						&& !getProperties().getField().isSequence());			
	}
	
	@JsonIgnore
	public boolean isCustom() {
		return this.category == JoinColumnCategory.CUSTOM;
	}
	
	@JsonIgnore
	public boolean isStandard() {
		return this.category == JoinColumnCategory.STANDARD;
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
	public boolean isPeriod() {
		return type == DimensionType.PERIOD;
	}
	
	
	@JsonIgnore
	public String getDbGridVarName() {
		String name = isMaterializedGrid() ? "mat_" : isJoin() ? "join_" : "grid_";	
		return name + getGridId();
	}
	
	@JsonIgnore
	public String getDbColAliasName() {
		return "column" + getId();
//		if (isMeasure() && dimensionId != null) {
//			return new Measure(dimensionId, dimensionName).getUniverseTableColumnName() + "_" + getGridId();
//		}
//		if (isAttribute() && dimensionId != null) {
//			return new Attribute(dimensionId, dimensionName).getUniverseTableColumnName() + "_" + getGridId();
//		}
//		if (isPeriod() && dimensionId != null) {			
//			return new Period(dimensionId, dimensionName).getUniverseTableColumnName() + "_" + getGridId();
//		}	
//		if(isJoin()) {
//			return "column" + getColumnId();
//		}
//		if(isMaterializedGrid()) {
//			return "column" + getColumnId();
//		}
//		return null;
	}
	
	@JsonIgnore
	public String getDbColName(boolean withGridVarName, boolean withalias) {
		return getDbColName(withGridVarName, withalias, true);
	}
	
	@JsonIgnore
	public String getDbColName(boolean withGridVarName, boolean withalias, boolean withMeasureFunction) {
		String varCol = withGridVarName ? getDbGridVarName() + "." : "";
		String alias = getDbColAliasName();
		String aliasCol = withalias ? " AS " + alias : "";
			
		if(isMaterializedGrid()) {		
			alias = new MaterializedGridColumn(columnId).getDbColumnName();
			//aliasCol = withalias ? " AS " + alias : "";
			if (isMeasure()) {				
				if(withMeasureFunction) {
					String func = StringUtils.hasText(this.dimensionFunction) ? this.dimensionFunction : "SUM";
					String col = varCol + alias;
					col = "CASE WHEN " + col + " IS NULL THEN 0 ELSE " + col + " END";
					return func + "(" + col + ")" + aliasCol;
				}
				else {
					String col = varCol + alias;
					col = "CASE WHEN " + col + " IS NULL THEN 0 ELSE " + col + " END";
					return col + aliasCol;
				}
			}
			String col = alias;		
			return varCol + col + aliasCol;
		}
		
		if(isJoin()) {
			if (isMeasure()) {
				String colName = columnId != null ? new Measure(columnId, dimensionName, DataSourceType.JOIN, gridId).getUniverseTableColumnName() : alias;
				if(withMeasureFunction) {
					String func = StringUtils.hasText(this.dimensionFunction) ? this.dimensionFunction : "SUM";
					String col = varCol + colName;
					col = "CASE WHEN " + col + " IS NULL THEN 0 ELSE " + col + " END";
					return func + "(" + col + ")" + aliasCol;
				}
				else {
					String col = varCol + colName;
					col = "CASE WHEN " + col + " IS NULL THEN 0 ELSE " + col + " END";
					return col + aliasCol;
				}
			}
			if (isAttribute()) {
				String colName = columnId != null ? new Attribute(columnId, dimensionName, DataSourceType.JOIN, gridId).getUniverseTableColumnName() : alias;
				return varCol + colName + aliasCol;
			}
			if (isPeriod()) {	
				String colName = columnId != null ? new Period(columnId, dimensionName, DataSourceType.JOIN, gridId).getUniverseTableColumnName() : alias;
				return varCol + colName + aliasCol;
			}		
		}
				
		if (isMeasure() && dimensionId != null) {
			if(withMeasureFunction) {
				String func = StringUtils.hasText(this.dimensionFunction) ? this.dimensionFunction : "SUM";
				return func + "(" + varCol + new Measure(dimensionId, dimensionName, dataSourceType, null).getUniverseTableColumnName() + ")" + aliasCol;
			}
			else {
				return varCol + new Measure(dimensionId, dimensionName, dataSourceType, null).getUniverseTableColumnName() + "" + aliasCol;
			}
		}
		if (isAttribute() && dimensionId != null) {
			return varCol + new Attribute(dimensionId, dimensionName, dataSourceType, null).getUniverseTableColumnName() + aliasCol;
		}
		if (isPeriod() && dimensionId != null) {			
			return varCol + new Period(dimensionId, dimensionName, dataSourceType, null).getUniverseTableColumnName() + aliasCol;
		}		
		return null;
	}
	
	@JsonIgnore
	public String getCustomCol(boolean withalias, Map<String, Object> parameters, Join join, ISpotService spotService, Map<String, Object> variableValues) {
		String alias = getDbColAliasName();
		if(isCustom() && properties != null) {
			return properties.getCustomCol(withalias, alias, parameters, join, dimensionFunction, spotService,variableValues);
		}
		return null;
	}
	
	@JsonIgnore
	public String getGroupByCols(Join grid) {
		if(!isMeasure()) {
			if(isStandard()) {
				return getDbColName(true, false);
			}
			else if(properties != null) {
				return properties.getGroupByCols(grid);
			}
		}
		return null;
	}
	
	
	
	@JsonIgnore
	public boolean isMaterializedGrid() {
		return gridType == JoinGridType.MATERIALIZED_GRID;
	}
	
	@JsonIgnore
	public boolean isJoin() {
		return gridType == JoinGridType.JOIN;
	}
	
	@JsonIgnore
	public boolean isGrid() {
		return gridType == JoinGridType.GRID || gridType == JoinGridType.REPORT_GRID;
	}
	
	
	@Override
	public JoinColumn copy() {
		JoinColumn copy = new JoinColumn();
		copy.setBackgroundColor(getBackgroundColor());
		copy.setCategory(getCategory());
		copy.setColumnId(getColumnId());
		copy.setColumnName(getColumnName());
		copy.setDimensionFormat(getDimensionFormat());
		copy.setDimensionFunction(getDimensionFunction());
		copy.setDimensionId(getDimensionId());
		copy.setDimensionName(getPublicationDimensionName());
		copy.setFixedType(getFixedType());
		copy.setForegroundColor(getForegroundColor());
		copy.setFormat(getFormat());
		copy.setGridId(getGridId());
		copy.setGridType(getGridType());
		copy.setGroupBy(getGroupBy());
		copy.setJoinId(getJoinId());
		copy.setName(getName());
		copy.setOrderAsc(getOrderAsc());
		copy.setPosition(getPosition());
		copy.setPublicationDimensionId(getPublicationDimensionId());
		copy.setPublicationDimensionName(getPublicationDimensionName());
		copy.setShow(isShow());
		copy.setType(getType());
		copy.setUsedForPublication(isUsedForPublication());
		copy.setWidth(getWidth());
		
		if(getProperties() != null) {
			JoinColumnProperties copyField = getProperties().copy();
			copy.setProperties(copyField);
		}
		
		return copy;
	}
	
	public void updateCopy(Join copy, HashMap<Long, JoinColumn> columns) {
		if(isCustom() && getProperties() != null && getProperties().getField() != null) {
			boolean updated = getProperties().updateCopy(copy, columns);
			if(updated) {
				copy.getColumnListChangeHandler().addUpdated(this);
			}
		}
	}
	
}
