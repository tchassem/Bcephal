/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;

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

/**
 * @author Joseph Wambo
 *
 */
@SuppressWarnings("serial")
@jakarta.persistence.Entity(name = "UniverseFilter")
@Table(name = "BCP_UNIVERSE_FILTER")
@Data
@EqualsAndHashCode(callSuper = false)
public class UniverseFilter implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "universe_filter_seq")
	@SequenceGenerator(name = "universe_filter_seq", sequenceName = "universe_filter_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "attributeFilter")
	private AttributeFilter attributeFilter;

	@ManyToOne
	@JoinColumn(name = "measureFilter")
	private MeasureFilter measureFilter;

	@ManyToOne
	@JoinColumn(name = "periodFilter")
	private PeriodFilter periodFilter;

	@ManyToOne
	@JoinColumn(name = "spotFilter")
	private SpotFilter spotFilter;
	
	@Transient
	private List<VariableValue> variableValues;
	
	public UniverseFilter() {
		this.attributeFilter = new AttributeFilter();
		this.measureFilter = new MeasureFilter();
		this.periodFilter = new PeriodFilter();
		this.spotFilter = new SpotFilter();
		this.variableValues =  new ArrayList<VariableValue>(0); 
	}
	
	@JsonIgnore
	public String toSql() {
		String sql = "";
		String and = "";
		if(attributeFilter != null) {
			String filterSql = attributeFilter.toSql(variableValues);
			if (StringUtils.hasText(filterSql)) {
				sql = sql.concat(and).concat("(").concat(filterSql).concat(")");
				and = " AND ";
			}
		}
		if(measureFilter != null) {
			String filterSql = measureFilter.toSql(variableValues);
			if (StringUtils.hasText(filterSql)) {
				sql = sql.concat(and).concat("(").concat(filterSql).concat(")");
				and = " AND ";
			}
		}
		if(periodFilter != null) {
			String filterSql = periodFilter.toSql(variableValues);
			if (StringUtils.hasText(filterSql)) {
				sql = sql.concat(and).concat("(").concat(filterSql).concat(")");
				and = " AND ";
			}
		}
		return sql;
	}
	
	public void add(UniverseFilter filter) {
		if(filter != null) {
			getAttributeFilter().add(filter.getAttributeFilter());
			getMeasureFilter().add(filter.getMeasureFilter());
			getPeriodFilter().add(filter.getPeriodFilter());
		}
	}


	public boolean containsExcludedDimensions(List<CalculatedMeasureExcludeFilter> excludeFilters) {
		if(attributeFilter != null) {
			boolean response = attributeFilter.containsExcludedDimensions(excludeFilters, getVariableValues());
			if(response) {
				return true;
			}
		}
		if(measureFilter != null) {
			boolean response = measureFilter.containsExcludedDimensions(excludeFilters, getVariableValues());
			if(response) {
				return true;
			}
		}
		if(periodFilter != null) {
			boolean response = periodFilter.containsExcludedDimensions(excludeFilters, getVariableValues());
			if(response) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public UniverseFilter copy() {
		UniverseFilter copy = new UniverseFilter();
		copy.setAttributeFilter(attributeFilter != null ? attributeFilter.copy() : null);
		copy.setMeasureFilter(measureFilter != null ? measureFilter.copy() : null);
		copy.setPeriodFilter(periodFilter != null ? periodFilter.copy() : null);
		copy.setSpotFilter(spotFilter != null ? spotFilter.copy() : null);
		copy.setVariableValues(new ArrayList<>(variableValues));
		return copy;
	}


}
