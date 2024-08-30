package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodGrouping;

import jakarta.persistence.Column;
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

@Entity(name = "UnionGridColumn")
@Table(name = "BCP_UNION_GRID_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class UnionGridColumn extends Persistent {

	private static final long serialVersionUID = 8032684868789165082L;
	
	private static final String separator = ";";
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "union_grid_column_seq")
	@SequenceGenerator(name = "union_grid_column_seq", sequenceName = "union_grid_column_seq", initialValue = 1, allocationSize = 1)
	private Long id;
		
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unionGrid")
	private UnionGrid unionGrid;
	
	@Column(name = "dimensionType")
	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	@Enumerated(EnumType.STRING)
	private HorizontalAlignment alignment;
	
	private String name;
	
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
	
	@JsonIgnore
	private String columnIds;
	
	@Transient
	private List<SmartMaterializedGridColumn> columns;
	
	@Transient @JsonIgnore
	private Map<Long, SmartMaterializedGridColumn> columnsMap;
	
	@Transient
	private Boolean orderAsc;
	
	
	public UnionGridColumn() {
		groupBy = PeriodGrouping.DAY_OF_MONTH;
		show = true;
		columns = new ArrayList<>();
	}
	
	public UnionGridColumn(Long id) {
		this();
		setId(id);
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
	
	public List<Long> buildColumnIds() {
		List<Long> ids = new ArrayList<>();
		if(StringUtils.hasText(getColumnIds())) {
			for(String s : getColumnIds().split(UnionGridColumn.separator)) {
				try {
					ids.add(Long.valueOf(s));
				}
				catch (Exception e) {
					
				}
			}
		}
		return ids;
	}
	
	public String buildColumnIdsAsString(){
		String ids = "";
		String coma = "";
		for(SmartMaterializedGridColumn column : getColumns()) {
			ids += coma + column.getId();
			coma = UnionGridColumn.separator;
		}
		return ids;
	}
	
	public List<SmartMaterializedGridColumn> buildColumns() {
		columns = new ArrayList<>();
		if(StringUtils.hasText(getColumnIds())) {
			for(String s : getColumnIds().split(UnionGridColumn.separator)) {
				try {
					columns.add(new SmartMaterializedGridColumn(Long.valueOf(s)));
				}
				catch (Exception e) {
					
				}
			}
		}
		return columns;
	}
	
	@JsonIgnore
	public SmartMaterializedGridColumn getColumnByGridId(Long gridId) {
		return getColumnsMap().get(gridId);
	}
	
	@JsonIgnore
	public SmartMaterializedGridColumn getColumnInListByGridId(Long gridId) {				
		for(SmartMaterializedGridColumn col :  getColumns()) {
			if(col.getGrid() != null && col.getGrid().getId().equals(gridId)) {
				return col;
			}
		}
		return null;
	}
	
	@JsonIgnore
	public String getDbColAliasName() {
		return "column" + getId();
	}

	@Override
	public UnionGridColumn copy() {
		UnionGridColumn copy = new UnionGridColumn();
		copy.setName(this.getName());
		copy.setType(type);
		copy.setPosition(position);
		copy.setShow(show);	
		copy.setBackgroundColor(backgroundColor);
		copy.setForegroundColor(foregroundColor);
		copy.setWidth(width);
		copy.setFixedType(fixedType);
		copy.setFormat(format != null ? format.copy() : null);
		copy.setGroupBy(getGroupBy());
		
		return copy;
	}
	
}
