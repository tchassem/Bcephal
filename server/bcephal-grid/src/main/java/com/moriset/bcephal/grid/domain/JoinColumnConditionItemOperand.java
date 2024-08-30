package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "JoinColumnConditionItemOperand")
@Table(name = "BCP_JOIN_COLUMN_CONDITION_ITEM_OPERAND")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinColumnConditionItemOperand extends Persistent {

	private static final long serialVersionUID = 6199289909176320187L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_condition_operand_seq")
	@SequenceGenerator(name = "join_column_condition_operand_seq", sequenceName = "join_column_condition_operand_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "itemId")
	private JoinColumnConditionItem itemId;
	
	private int position;	
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	@Enumerated(EnumType.STRING)
	private FilterVerb verb;
	
	private String comparator;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "field1")
	private JoinColumnField field1;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "field2")
	private JoinColumnField field2;
	
	private String openingBracket;
	
	private String closingBracket;
	
	
	public JoinColumnConditionItemOperand() {
		this.dimensionType = DimensionType.ATTRIBUTE;
		this.verb = FilterVerb.AND;
	}
	
	
	@JsonIgnore
	public String asSql(Join join, JoinColumnConditionItem item, Map<String, Object> parameters, String measureFunction, JoinColumnField field, Map<String, Object> variableValues) {
		String sql = null;		
		if(getField1() != null) {
			String part1 = getCol(getField1(), join, parameters, measureFunction, variableValues);
			if(getField2() != null) {
				getField2().setDimensionType(getField1().getDimensionType());
			}
			String part2 = getCol(getField2(), join, parameters, measureFunction, variableValues);			
				
			if(getField1().isAttribute()) {
				sql = getSqlForAttribute(part1, part2, parameters);						
			}
			else if(getField1().isPeriod()) {
				sql = getSqlForPeriod(part1, part2, parameters);			
			}
			else if(getField1().isMeasure()) {
				sql = getSqlForMeasure(part1, part2, parameters);			
			}
		}			
		return StringUtils.hasText(sql) ? sql : "null";
	}
	
	private String getCol(JoinColumnField field, Join join, Map<String, Object> parameters, String measureFunction, Map<String, Object> variableValues) {
		String col = null;
		if(field != null) {
			if(field.isFree()) {
				Object value = field.getFreeValue();
				if(value == null && field.isMeasure()) {
					value = BigDecimal.ZERO;							
				}
				String key = "Param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();
				parameters.put(key, value);
				col = ":" + key;
			}
			else if(field.isColumn()) {
				col = field.getCustomColumnCol(measureFunction, true);	
				if(col == null && field.isMeasure()) {
					col = "0";
				}
			}
			else if(field.isCopy()) {
				col = field.getCustomCopyCol(join, parameters, null, variableValues);	
				if(col == null && field.isMeasure()) {
					col = "0";
				}
			}
		}	
		return col;
	}
	
	private String getSqlForAttribute(String col, String col2, Map<String, Object> parameters){
		String sql = null;
		String operator = this.comparator;
//		String key = "param_" + RandomUtils.nextInt(0, 100000) + "_" + System.currentTimeMillis();
		
		if(AttributeOperator.NULL.name().equalsIgnoreCase(operator)) {
			sql = "(" + col + " IS NULL OR " +  col + " = '')";
		}
		else if(AttributeOperator.NOT_NULL.name().equalsIgnoreCase(operator)) {
			sql = "(" + col + " IS NOT NULL AND " +  col + " != '')";
		}
		else if(AttributeOperator.EQUALS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {				
				sql = col + " = " + col2;
			}
			else {
//				sql = col + " = :" + key;
//				parameters.put(key, value);
			}
		}
		else if(AttributeOperator.NOT_EQUALS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {				
				sql = col + " != " + col2;
			}
			else {
//				sql = col + " != :" + key;
//				parameters.put(key, value);
			}
		}
		else if(AttributeOperator.STARTS_WITH.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {	
				if(col2.startsWith(":")) {
					String k = col2.replaceFirst(":", "");
					Object param = parameters.get(k);
					if(param != null) {
						param = param.toString().toUpperCase() + "%";
						parameters.put(k, param);
						//col2 = ":" + key;
					}
				}
				sql = "UPPER(" + col + ") LIKE " + col2;
			}
			else {
//				sql = "UPPER(" + col + ") LIKE :" + key;
//				parameters.put(key, value.toUpperCase() + "%");
			}
		}
		else if(AttributeOperator.ENDS_WITH.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {
				if(col2.startsWith(":")) {
					String k = col2.replaceFirst(":", "");
					Object param = parameters.get(k);
					if(param != null) {
						param = "%" + param.toString().toUpperCase();
						parameters.put(k, param);
					}
				}
				sql = "UPPER(" + col + ") LIKE " + col2;
			}
			else {
//				sql = "UPPER(" + col + ") LIKE :" + key;
//				parameters.put(key, "%" + value.toUpperCase());
			}
		}
		else if(AttributeOperator.CONTAINS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {
				if(col2.startsWith(":")) {
					String k = col2.replaceFirst(":", "");
					Object param = parameters.get(k);
					if(param != null) {
						param = "%" + param.toString().toUpperCase() + "%";
						parameters.put(k, param);
					}
				}
				sql = "UPPER(" + col + ") LIKE " + col2;
			}
			else {
//				sql = "UPPER(" + col + ") LIKE :" + key;
//				parameters.put(key, "%" + value.toUpperCase() + "%");
			}
		}
		else if(AttributeOperator.NOT_CONTAINS.name().equalsIgnoreCase(operator)) {
			if(col2 != null) {	
				if(col2.startsWith(":")) {
					String k = col2.replaceFirst(":", "");
					Object param = parameters.get(k);
					if(param != null) {
						param = "%" + param.toString().toUpperCase() + "%";
						parameters.put(k, param);
					}
				}
				sql = "UPPER(" + col + ") NOT LIKE " + col2;
			}
			else {
//				sql = "UPPER(" + col + ") NOT LIKE :" + key;
//				parameters.put(key, "%" + value.toUpperCase() + "%");
			}
		}		
		return sql;
	}

	protected String getSqlForPeriod(String col, String col2, Map<String, Object> parameters){
		String sql = null;
		String operator = this.comparator;	
		if(operator != null) {
			if(col2 != null) {				
				sql = col + " " + operator + " " + col2;
			}
			else {
//				sql = "WHEN " + col + " " + operator + " :" + key + " THEN " + value;
//				parameters.put(key, value);
			};
		}				
		return sql;
	}
	
	@JsonIgnore
	protected String getSqlForMeasure(String col, String col2, Map<String, Object> parameters){
		String sql = null;
		String operator = this.comparator;
//		String key = "param_" + RandomUtils.nextInt(0, 100000) + "_" + System.currentTimeMillis();
		if(operator != null) {
			if(col2 != null) {				
				sql = col + " " + operator + " " + col2;
			}
			else {
//				sql = "WHEN " + col + " " + operator + " :" + key + " THEN " + value;
//				parameters.put(key, value);
			};
		}		
		return sql;
	}
	

	@Override
	public Persistent copy() {
		JoinColumnConditionItemOperand copy = new JoinColumnConditionItemOperand();
		copy.setClosingBracket(getClosingBracket());
		copy.setComparator(getComparator());
		copy.setDimensionType(getDimensionType());
		copy.setItemId(getItemId());
		copy.setOpeningBracket(getOpeningBracket());
		copy.setPosition(getPosition());
		copy.setVerb(getVerb());
		
		if(getField1() != null) {
			JoinColumnField joinColumnField1 =(JoinColumnField) getField1().copy();
			copy.setField1(joinColumnField1);
		}
		if(getField2() != null) {
			JoinColumnField joinColumnField2 =(JoinColumnField) getField2().copy();
			copy.setField2(joinColumnField2);
		}
		return copy;
	}
	
	
	public boolean updateCopy(Join copy, HashMap<Long, com.moriset.bcephal.grid.domain.JoinColumn> columns) {
		boolean updated = false;		
		if(getField1() != null) {
			boolean res = getField1().updateCopy(copy, columns);
			if(res) {
				updated = true;
			}
		}
		if(getField2() != null) {
			boolean res = getField2().updateCopy(copy, columns);
			if(res) {
				updated = true;
			}
		}
		return updated;
	}
	
	
}
