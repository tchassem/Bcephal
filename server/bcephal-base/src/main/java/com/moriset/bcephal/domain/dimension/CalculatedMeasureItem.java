/**
 * 9 févr. 2024 - CalculatedMeasureItem.java
 *
 */
package com.moriset.bcephal.domain.dimension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.MeasureFunctions;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Emmanuel Emmeni
 *
 */
@Entity(name = "CalculatedMeasureItem")
@Table(name = "BCP_CALCULATED_MEASURE_ITEM")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculatedMeasureItem extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3382088143543011928L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calculated_measure_item_seq")
	@SequenceGenerator(name = "calculated_measure_item_seq", sequenceName = "calculated_measure_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calculatedMeasure")
	private CalculatedMeasure calculatedMeasure;
	
	private Long measureId;
	
    private BigDecimal decimalValue;

	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	private int position;
	
	private boolean active;
	
    private String openingBracket;
    
    private String closingBracket;
    
    private String arithmeticOperator;
    
    private String dimensionFunction;
    
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "item")
	private List<CalculatedMeasureExcludeFilter> excludeFilters;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<CalculatedMeasureExcludeFilter> excludeFiltersListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore @Transient
	String fromPart;
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore @Transient
	String wherePart;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore @Transient
	private String alias;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore @Transient
	private String tableName;
	
	
	public CalculatedMeasureItem() {
		type = DimensionType.MEASURE;
		active = true;
		this.excludeFilters = new ArrayList<>();
		this.excludeFiltersListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setExcludeFilters(List<CalculatedMeasureExcludeFilter> excludeFilters) {
		this.excludeFilters = excludeFilters;
		this.excludeFiltersListChangeHandler.setOriginalList(excludeFilters);
	}
	
	public void sortExcludeFilters() {
		List<CalculatedMeasureExcludeFilter> items = this.excludeFiltersListChangeHandler.getItems();
		Collections.sort(items, new Comparator<CalculatedMeasureExcludeFilter>() {
			@Override
			public int compare(CalculatedMeasureExcludeFilter o1, CalculatedMeasureExcludeFilter o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		setExcludeFilters(items);
	}
	
	@PostLoad
	public void initListChangeHandler() {		
		this.excludeFilters.size();
		this.excludeFiltersListChangeHandler.setOriginalList(excludeFilters);
	}
	
	@JsonIgnore
	public String getUniverseTableColumnName(DataSourceType dataSourceType) {
		return new Measure(measureId, null, dataSourceType, null).getUniverseTableColumnName();
	}
	
	public boolean hasActiveExcludeFilter() {
		for(CalculatedMeasureExcludeFilter item : getExcludeFilters()) {
			if(item.isActive()) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public String buildDbColumnName(DataSourceType dataSourceType) {
		String col = null;
		
		if(this.type == DimensionType.FREE) {
			col = this.decimalValue != null ? "" + this.decimalValue.toPlainString() : "0";
			alias = "CONST" + getId();
		}
		else if (measureId != null) {
			col = getUniverseTableColumnName(dataSourceType);
			alias = col;
			String functions = getDimensionFunction();
			if(!StringUtils.hasText(functions)) {
				functions =  MeasureFunctions.SUM.code;
			}
			if(StringUtils.hasText(functions)) {
				functions = functions.toUpperCase().equals(MeasureFunctions.AVERAGE.name().toUpperCase()) ? MeasureFunctions.AVERAGE.code : functions;
				col = functions.concat("(").concat(col).concat(")");
			}		
		}
		return col + " AS " + alias;
	}
		
	@JsonIgnore
	public String toSql(DataSourceType dataSourceType) {
		String sql = null;
		String open = !StringUtils.hasText(openingBracket) ? "" : openingBracket;
		String close = !StringUtils.hasText(closingBracket) ? "" : closingBracket;
		if(this.type == DimensionType.FREE) {
			sql = this.decimalValue != null ? "" + this.decimalValue.toPlainString() : "0";
			sql = open.concat(sql).concat(close);
		}
		else if (measureId != null) {
			String col = getUniverseTableColumnName(dataSourceType);
			String functions = getDimensionFunction();
			if(StringUtils.hasText(functions)) {
				functions = functions.toUpperCase().equals(MeasureFunctions.AVERAGE.name().toUpperCase()) ? MeasureFunctions.AVERAGE.code : functions;
				col = functions.concat("(").concat(col).concat(")");
			}
			
			if(hasActiveExcludeFilter() && StringUtils.hasText(fromPart)) {
				if(!StringUtils.hasText(functions)) {
					functions =  MeasureFunctions.SUM.code;
					col = functions.concat("(").concat(col).concat(")");
				}
				col = "(SELECT ".concat(col).concat(fromPart).concat(wherePart).concat(")");
			}
			
			sql = col;
			sql = open.concat(sql).concat(close);			
		}
		return sql;
	}
	
	

	@Override
	public CalculatedMeasureItem copy() {
		CalculatedMeasureItem copy = CalculatedMeasureItem.builder() 
				.position(position)
				.active(active)
				.measureId(measureId)
				.decimalValue(decimalValue)
				.type(type)
				.openingBracket(openingBracket)
				.closingBracket(closingBracket)
				.arithmeticOperator(arithmeticOperator)
				.build();
		for(CalculatedMeasureExcludeFilter item : getExcludeFiltersListChangeHandler().getItems()) {
			copy.getExcludeFiltersListChangeHandler().addNew(item.copy());
		}
		return copy;
	}

}
