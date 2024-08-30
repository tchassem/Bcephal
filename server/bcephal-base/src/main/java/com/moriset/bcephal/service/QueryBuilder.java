/**
 * 
 */
package com.moriset.bcephal.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.universe.UniverseParameters;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Data
@Slf4j
public class QueryBuilder {

	protected Map<Long, Attribute> attributes;
	protected Map<Long, Measure> measures;
	protected Map<Long, Period> periods;
	
	protected String tableName;
	
	public QueryBuilder() {		
		attributes = new HashMap<>();
		measures = new HashMap<>();
		periods = new HashMap<>();
		this.tableName = UniverseParameters.UNIVERSE_TABLE_NAME;
	}	
	
	
	
	public String buildCountQuery() {
		String sql = buildSelectCountPart();
		if(StringUtils.hasText(sql)){
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if(StringUtils.hasText(wherePart)){
				sql += wherePart;
			}
		}
		return sql;
	}
	
	public String buildQuery() {
		String sql = buildSelectPart();
		if(StringUtils.hasText(sql)){
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if(StringUtils.hasText(wherePart)){
				sql += wherePart;
			}	
			String orderBy = buildOrderPart();
			
			if(!StringUtils.hasText(orderBy)) {
				orderBy = " ORDER BY id ";
			}
			
			if(StringUtils.hasText(orderBy)){
				sql += orderBy;
			}
			
		}
		return sql;
	}
	
	protected String buildSelectCountPart() {
		String selectPart = "SELECT COUNT(1) ";
		return selectPart;
	}
	
	protected String buildSelectPart() {
		String selectPart = "SELECT * ";
		return selectPart;
	}	
	
	protected String buildFromPart() {
		return " FROM " + this.tableName;
	}
	
	protected String buildWherePart() {
		String sql = "";		
		return sql;
	}
	
	protected String buildOrderPart() {
		String sql = "";
		return sql;
	}
		
	
	public String buildUniverseFilterWherePart(UniverseFilter universeFilter) {
		return buildUniverseFilterWherePart(universeFilter, new ArrayList<>());
	}
	
	public String buildUniverseFilterWherePart(UniverseFilter universeFilter, List<CalculatedMeasureExcludeFilter> excludeFilters) {
		String sql = "";
		String and = "";
		if (universeFilter.getMeasureFilter() != null) {
			String measureSql = buildMeasureFilterWherePart(universeFilter.getMeasureFilter(), universeFilter.getVariableValues(), excludeFilters);
			if (StringUtils.hasText(measureSql)) {
				sql = sql.concat(and).concat("(").concat(measureSql).concat(")");
				and = " AND ";
			}
		}
		if (universeFilter.getAttributeFilter() != null) {
			String scopeSql = buildScopeFilterWherePart(universeFilter.getAttributeFilter(), universeFilter.getVariableValues(), excludeFilters);
			if (StringUtils.hasText(scopeSql)) {
				sql = sql.concat(and).concat("(").concat(scopeSql).concat(")");
				and = " AND ";
			}
		}
		if (universeFilter.getPeriodFilter() != null) {
			String periodSql = buildPeriodFilterWherePart(universeFilter.getPeriodFilter(), universeFilter.getVariableValues(), excludeFilters);
			if (StringUtils.hasText(periodSql)) {
				sql = sql.concat(and).concat("(").concat(periodSql).concat(")");
				and = " AND ";
			}
		}
		return sql;
	}
	
	protected String buildMeasureFilterWherePart(MeasureFilter measureFilter, List<VariableValue> variableValues) {
		return buildMeasureFilterWherePart(measureFilter, variableValues, new ArrayList<>());
	}

	protected String buildMeasureFilterWherePart(MeasureFilter measureFilter, List<VariableValue> variableValues, List<CalculatedMeasureExcludeFilter> excludeFilters) {
		log.trace("Try to build MeasureFilter sql : {}", measureFilter);
		String sql = "";
		List<MeasureFilterItem> items = measureFilter.getSortedItems();		
		for(MeasureFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, excludeFilters);
			if (StringUtils.hasText(itemSql)) {
				boolean isFirst = item.getPosition() == 0 || !org.springframework.util.StringUtils.hasText(sql);
				FilterVerb verb = item.getFilterVerb() != null ? item.getFilterVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);
				this.measures.put(item.getDimensionId(), new Measure(item.getDimensionId()));
			}
		};
		log.trace("MeasureFilter sql builded : {}", sql);
		return sql;
	}

	protected String buildScopeFilterWherePart(AttributeFilter attributeFilter, List<VariableValue> variableValues) {
		return buildScopeFilterWherePart(attributeFilter, variableValues, new ArrayList<>());
	}
	
	protected String buildScopeFilterWherePart(AttributeFilter attributeFilter, List<VariableValue> variableValues, List<CalculatedMeasureExcludeFilter> excludeFilters) {
		log.trace("Try to build AttributeFilter sql : {}", attributeFilter);
		String sql = "";
		List<AttributeFilterItem> items = attributeFilter.getSortedItems();		
		for(AttributeFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, excludeFilters);
			if (StringUtils.hasText(itemSql)) {
				
				boolean isFirst = item.getPosition() == 0 || !org.springframework.util.StringUtils.hasText(sql);
				FilterVerb verb = item.getFilterVerb() != null ? item.getFilterVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);					
				this.attributes.put(item.getDimensionId(), new Attribute(item.getDimensionId()));
			}
		};
		log.trace("AttributeFilter sql builded : {}", sql);
		return sql;
	}
	
	protected String buildPeriodFilterWherePart(PeriodFilter periodFilter, List<VariableValue> variableValues) {
		return buildPeriodFilterWherePart(periodFilter, variableValues, new ArrayList<>());
	}
	
	protected String buildPeriodFilterWherePart(PeriodFilter periodFilter, List<VariableValue> variableValues, List<CalculatedMeasureExcludeFilter> excludeFilters) {
		log.trace("Try to build PeriodFilter sql : {}", periodFilter);
		String sql = "";
		List<PeriodFilterItem> items = periodFilter.getSortedItems();		
		for(PeriodFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, excludeFilters);
			if (StringUtils.hasText(itemSql)) {
				
				boolean isFirst = item.getPosition() == 0 || !org.springframework.util.StringUtils.hasText(sql);
				FilterVerb verb = item.getFilterVerb() != null ? item.getFilterVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);					
				this.periods.put(item.getDimensionId(), new Period(item.getDimensionId()));
			}
		};
		log.trace("PeriodFilter sql builded : {}", sql);
		return sql;
	}

//	protected String buildPeriodFilterWherePart(PeriodFilter periodFilter) {
//		log.trace("Try to build PeriodFilter sql : {}", periodFilter);
//		String sql = "";
//		String and = "";
//		List<PeriodFilterItem> items = periodFilter.getSortedItems();		
//		for(PeriodFilterItem item : items) {			
//			String itemSql = item.toSql();
//			if (!StringUtils.isBlank(itemSql)) {
//				if(item.getPosition() > 0) {
//					and = item.getFilterVerb() != null ? " " + item.getFilterVerb().toString() + " " : " AND ";
//				}
//				sql = sql.concat(and).concat(itemSql);	
//				this.periods.put(item.getDimensionId(), new Period(item.getDimensionId()));
//			}
//		};
//		log.trace("PeriodFilter sql builded : {}", sql);
//		return sql;
//	}
	
}
