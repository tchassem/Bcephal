/**
 * 1 avr. 2024 - UnionGridPublishedQueryBuilder.java
 *
 */
package com.moriset.bcephal.grid.service;

import com.moriset.bcephal.grid.repository.UnionGridColumnRepository;
import com.moriset.bcephal.service.filters.ISpotService;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Emmanuel Emmeni
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UnionGridPublishedQueryBuilder extends UnionGridQueryBuilder {

	public UnionGridPublishedQueryBuilder(UnionGridFilter filter, UnionGridColumnRepository unionGridColumnRepository, ISpotService spotService) {
		super(filter, unionGridColumnRepository, spotService);
		addMainGridOid = true;
	}

	/*protected String buildMeasureFilterWherePart(MeasureFilter measureFilter, List<VariableValue> variableValues) {
		log.trace("Try to build MeasureFilter sql : {}", measureFilter);
		String sql = "";
		List<MeasureFilterItem> items = measureFilter.getSortedItems();		
		for(MeasureFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, new ArrayList<>());
			if (StringUtils.hasText(itemSql)) {
				boolean isFirst = item.getPosition() == 0;
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
//				this.measures.put(item.getDimensionId(), new Measure(item.getDimensionId()));
			}
		};
		log.trace("MeasureFilter sql builded : {}", sql);
		return sql;
	}

	protected String buildScopeFilterWherePart(AttributeFilter attributeFilter, List<VariableValue> variableValues) {
		log.trace("Try to build AttributeFilter sql : {}", attributeFilter);
		String sql = "";
		List<AttributeFilterItem> items = attributeFilter.getSortedItems();		
		for(AttributeFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, new ArrayList<>());
			if (StringUtils.hasText(itemSql)) {
				
				boolean isFirst = item.getPosition() == 0;
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
//				this.attributes.put(item.getDimensionId(), new Attribute(item.getDimensionId()));
			}
		};
		log.trace("AttributeFilter sql builded : {}", sql);
		return sql;
	}
	
	
	protected String buildPeriodFilterWherePart(PeriodFilter periodFilter, List<VariableValue> variableValues) {
		log.trace("Try to build PeriodFilter sql : {}", periodFilter);
		String sql = "";
		List<PeriodFilterItem> items = periodFilter.getSortedItems();		
		for(PeriodFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, new ArrayList<>());
			if (StringUtils.hasText(itemSql)) {
				
				boolean isFirst = item.getPosition() == 0;
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
//				this.periods.put(item.getDimensionId(), new Period(item.getDimensionId()));
			}
		};
		log.trace("PeriodFilter sql builded : {}", sql);
		return sql;
	}*/

}
