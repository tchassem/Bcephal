/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterItem;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.filters.VariableIntervalPeriod;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "JoinCondition")
@Table(name = "BCP_JOIN_CONDITION")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinCondition extends Persistent {
	
	private static final long serialVersionUID = 2585605951026028998L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_condition_seq")
	@SequenceGenerator(name = "join_condition_seq", sequenceName = "join_condition_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "joinId")
	private Join joinId;
	
	private int position;
	
	private String verb;
    private String openingBracket;
    private String closingBracket;
    private String comparator;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "item1")
	private JoinConditionItem item1;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "item2")
	private JoinConditionItem item2;
	
	
	public JoinCondition(){
		
	}
	
    public JoinCondition(FilterItem item, Join join){
		setVerb(item.getFilterVerb().name());
		setOpeningBracket(item.getOpenBrackets());
		setClosingBracket(item.getCloseBrackets());
		
		JoinColumn column = join.getColumnByOid(item.getDimensionId());
		
		setItem1(new JoinConditionItem());		
		getItem1().setType(JoinConditionItemType.COLUMN);
		getItem1().setDimensionType(item.getDimensionType());
		if(column != null) {
			getItem1().setGridType(column.getGridType());
			getItem1().setGridId(column.getGridId());
			getItem1().setColumnId(column.getColumnId());
			getItem1().setDimensionId(column.getDimensionId());
			getItem1().setDimensionName(column.getDimensionName());
		}
		setItem2(new JoinConditionItem());
		getItem2().setType(JoinConditionItemType.PARAMETER);
		getItem2().setDimensionType(item.getDimensionType());
		if(item instanceof AttributeFilterItem) {
			getItem2().setStringValue(((AttributeFilterItem)item).getValue());
			if(((AttributeFilterItem)item).getOperator() != null) {
				setComparator(((AttributeFilterItem)item).getOperator().name());
			}
			
		}
		else if(item instanceof PeriodFilterItem) {
			PeriodFilterItem periodFilterItem = (PeriodFilterItem)item;
			PeriodValue value = new PeriodValue();
			value.setDateGranularity(periodFilterItem.getGranularity());
			value.setDateNumber(periodFilterItem.getNumber());
			value.setDateOperator(periodFilterItem.getOperator());
			value.setDateSign(periodFilterItem.getSign());
			value.setDateValue(periodFilterItem.getValue());
			getItem2().setPeriodValue(value);	
			if(((PeriodFilterItem)item).getOperator() != null) {
				setComparator(((PeriodFilterItem)item).getOperator().name());
			}			
		}
		else if(item instanceof MeasureFilterItem) {
			getItem2().setDecimalValue(((MeasureFilterItem)item).getValue());			
			setComparator(((MeasureFilterItem)item).getOperator());
		}
	}
	
	
	@JsonIgnore
	public String asSql(Map<String, Object> parameters, Join join, Map<String, Object> variableValues) {
		String sql = "";
		String data = null;
		if(getItem1() != null) {
			String col = getItem1().getDbColName(true, false);
			if(StringUtils.hasText(col)) {
				Object value = null;
				String col2 = null;
				if(getItem2() != null) {
					if(getItem2().getType() == JoinConditionItemType.PARAMETER) {						
						if(getItem2().isPeriod() && getItem2().getPeriodValue() != null && getItem2().getPeriodValue().isVariable()) {
							if(!StringUtils.hasText(getItem2().getPeriodValue().getVariableName())) {
								getItem2().getPeriodValue().setVariableName(getItem2().getStringValue());
							}
							value = getItem2().getPeriodValue().buildDynamicDate(variableValues);
						}
						else {
							value = getItem2().getDbParameterValue(variableValues);
						}
					}
					if(getItem2().getType() == JoinConditionItemType.COLUMN) {
						col2 = getItem2().getDbColName(true, false);
					}
					if(getItem2().getType() == JoinConditionItemType.VARIABLE) {
						if(variableValues.containsKey(getItem2().getStringValue())) {
							value = variableValues.get(getItem2().getStringValue());
						}
					}
				}
				
				if((getItem2().getType() == JoinConditionItemType.VARIABLE
						 || (getItem2().getType() == JoinConditionItemType.PARAMETER && getItem2().isPeriod() && getItem2().getPeriodValue() != null && getItem2().getPeriodValue().isVariable()))
						 && value == null) {
					data = "true";
				}				
				else if(getItem1().isAttribute()) {
					data = getSqlForAttribute(col, col2, value, parameters, join, variableValues);						
				}
				else if(getItem1().isPeriod()) {
					data = getSqlForPeriod(col, col2, value, parameters, join, variableValues);			
				}
				else if(getItem1().isMeasure()) {
					data = getSqlForMeasure(col, col2, value, parameters, join, variableValues);			
				}
				if(data != null) {
					sql += this.getOpeningBracket() != null ? this.getOpeningBracket() : "";
					sql += data;
					sql += this.getClosingBracket() != null ? this.getClosingBracket() : "";
				}
			}
		}
		
		return sql;
	}

	
	@JsonIgnore
	protected String getSqlForAttribute(String col, String col2, Object val, Map<String, Object> parameters, Join grid, Map<String, Object> variableValues){
		String sql = null;
		String operator = this.comparator;
		String key = "param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();
		String value = val != null ? val.toString() : "";
		
		if(AttributeOperator.NULL.name().equalsIgnoreCase(operator)) {
			//sql = col + " is null";
			sql = "(" + col + " is null OR " + col + " = '')";
		}
		else if(AttributeOperator.NOT_NULL.name().equalsIgnoreCase(operator)) {
			//sql = col + " is not null";
			sql = "(" + col + " is not null AND " + col + " != '')";
		}
//		else if(AttributeOperator.NULL_OR_EMPTY.name().equalsIgnoreCase(operator)) {
//			sql = "(" + col + " is not null OR " + col + " = '')";
//		}
		else if(AttributeOperator.EQUALS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {				
				sql = col + " = " + col2;
			}
			else {
				sql = col + " = :" + key;
				parameters.put(key, value);
			}
		}
		else if(AttributeOperator.NOT_EQUALS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {				
				sql = col + " != " + col2;
			}
			else {
				sql = col + " != :" + key;
				parameters.put(key, value);
			}
		}
		else if(AttributeOperator.STARTS_WITH.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {	
				sql = "UPPER(" + col + ") LIKE " + col2.toUpperCase() + " || '%'";
			}
			else {
				sql = "UPPER(" + col + ") LIKE :" + key;
				parameters.put(key, value.toUpperCase() + "%");
			}
		}
		else if(AttributeOperator.ENDS_WITH.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {	
				sql = "UPPER(" + col + ") LIKE '%' || " + col2.toUpperCase() + "";
			}
			else {
				sql = "UPPER(" + col + ") LIKE :" + key;
				parameters.put(key, "%" + value.toUpperCase());
			}
		}
		else if(AttributeOperator.CONTAINS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {	
				sql = "UPPER(" + col + ") LIKE '%' || " + col2.toUpperCase() + " || '%'";
			}
			else {
				sql = "UPPER(" + col + ") LIKE :" + key;
				parameters.put(key, "%" + value.toUpperCase() + "%");
			}
		}
		else if(AttributeOperator.NOT_CONTAINS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {	
				sql = "UPPER(" + col + ") NOT LIKE '%' || " + col2.toUpperCase() + "|| '%'";
			}
			else {
				sql = "UPPER(" + col + ") NOT LIKE :" + key;
				parameters.put(key, "%" + value.toUpperCase() + "%");
			}
		}		
		return sql;
	}
	
	@JsonIgnore
	protected String getSqlForPeriod(String col, String col2, Object value, Map<String, Object> parameters, Join join, Map<String, Object> variableValues){
		String sql = null;
		String operator = this.comparator;	
		
		
		if(AttributeOperator.NULL.name().equalsIgnoreCase(operator)) {
			sql = col + " is null";
		}
		else if(AttributeOperator.NOT_NULL.name().equalsIgnoreCase(operator)) {
			sql = col + " is not null";
		}
		else if(operator != null) {			
			if(col2 != null) {				
				sql = col + " " + operator + " " + col2;
			}
			else if(value != null) {
				if(value instanceof VariableIntervalPeriod) {
					VariableIntervalPeriod period = (VariableIntervalPeriod)value;
					String key1 = "param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();
					String key2 = "param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis() + 2;
					if(operator.trim().equals("=")) {
						sql = "(" + col + " >= :" + key1 + " AND " + col + " <= :" + key2 + ")";
						parameters.put(key1, period.getStart());
						parameters.put(key2, period.getEnd());
					}
					else if(operator.trim().equals("<") || operator.trim().equals("<=")) {
						sql = col + " " + operator + " :" + key1;
						parameters.put(key1, period.getStart());
					}
					else if(operator.trim().equals(">") || operator.trim().equals(">=")) {
						sql = col + " " + operator + " :" + key2;
						parameters.put(key2, period.getEnd());
					}
					else if(operator.trim().equals("<>") || operator.trim().equals("!=")) {
						sql = "(" + col + " < :" + key1 + " AND " + col + " > :" + key2 + ")";
						parameters.put(key1, period.getStart());
						parameters.put(key2, period.getEnd());
					}
				}
				else {
					String key = "param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();				
					sql = col + " " + operator + " :" + key;
					parameters.put(key, value);
				}
			}
			else {
				sql = col + " is null";
			}
		}					
		return sql;
	}
	
	@JsonIgnore
	protected String getSqlForMeasure(String col, String col2, Object value, Map<String, Object> parameters, Join join, Map<String, Object> variableValues){
		String sql = null;
		String operator = this.comparator;
		String key = "param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();
		
		if(AttributeOperator.NULL.name().equalsIgnoreCase(operator)) {
			sql = col + " is null";
		}
		else if(AttributeOperator.NOT_NULL.name().equalsIgnoreCase(operator)) {
			sql = col + " is not null";
		}
		else if(operator != null) {
			if(col2 != null) {				
				sql = col + " " + operator + " " + col2;
			}
			else {
				sql = col + " " + operator + " :" + key;
				parameters.put(key, value);
			};
		}		
		return sql;
	}
		
	
	
	@Override
	public JoinCondition copy() {
		JoinCondition copy = new JoinCondition();
		copy.setClosingBracket(getClosingBracket());
		copy.setComparator(getComparator());
		copy.setPosition(getPosition());
		copy.setJoinId(getJoinId());
		copy.setVerb(getVerb());
		copy.setOpeningBracket(getOpeningBracket());
		copy.setItem1(getItem1() != null ? getItem1().copy() : null);
		copy.setItem2(getItem2() != null ? getItem2().copy() : null);		
		return copy;
	}
	
}
