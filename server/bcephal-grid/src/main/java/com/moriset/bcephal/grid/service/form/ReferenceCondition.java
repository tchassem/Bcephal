package com.moriset.bcephal.grid.service.form;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.AbstractSmartGridColumn;
import com.moriset.bcephal.grid.domain.VariableReferenceCondition;
import com.moriset.bcephal.grid.domain.form.ReferenceConditionItemType;
import com.moriset.bcephal.service.condition.VariableReferenceConditionItemType;

import lombok.Data;

@Data
public class ReferenceCondition {
	
	private DimensionType parameterType;	
	private int position;			
	private String verb;
    private String openingBracket;
    private String closingBracket;
    private String comparator;
    private ReferenceConditionItemType conditionItemType;	
    private String stringValue;
    private BigDecimal decimalValue;
    private PeriodValue periodValue;
    private Long fieldId;
    private Long keyId;
    
    @JsonIgnore
	private AbstractSmartGrid<?> grid;
    
    @JsonIgnore
	private AbstractSmartGridColumn column;
    
    
    public ReferenceCondition() {
		
	}
	

	public ReferenceCondition(VariableReferenceCondition cond) {
		this.parameterType = cond.getParameterType();
		this.position = cond.getPosition();
		this.verb = cond.getVerb();
		this.openingBracket = cond.getOpeningBracket();
		this.closingBracket = cond.getClosingBracket();
		this.comparator = cond.getComparator();
		this.keyId = cond.getKeyId();
		this.conditionItemType = cond.getConditionItemType() == VariableReferenceConditionItemType.FREE ? ReferenceConditionItemType.FREE : ReferenceConditionItemType.FIELD;
		this.stringValue = cond.getStringValue();
		this.decimalValue = cond.getDecimalValue();
		this.periodValue = cond.getPeriodValue();
	}
    
    public String buildSql(){
		String col = column.getDbColumnName();
		String sql = null;
		if(column.isAttribute()) {
			AttributeOperator operation = AttributeOperator.valueOf(comparator);
			if (operation == null) {
				operation = !StringUtils.hasText(stringValue) ? AttributeOperator.NOT_NULL : AttributeOperator.EQUALS;
			}
			String s = operation.buidSql(stringValue);
			sql = col.concat(" ").concat(s);
			if(operation.isNull()) {
				sql = "(".concat(col).concat(" ").concat(s).concat(" OR ").concat(col).concat(" = '')");
			}
			else if(operation.isNotNull()) {
				sql = "(".concat(col).concat(" ").concat(s).concat(" AND ").concat(col).concat(" != '')");
			}
		}
		else if(column.isMeasure()) {			
			if(decimalValue == null) {
				sql = "(" + col + " IS NULL OR " + col + " = 0)";
			}
			else {
				sql = col + " " + comparator + " " + decimalValue;
			}
		}
		else if(column.isPeriod()) {
			if(periodValue == null) {
				sql = col + " IS NULL";
			}
			else {				
				Date date = periodValue.buildDynamicDate();
				sql = col + " " + comparator + " '" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "'";
			}
		}
		if(StringUtils.hasText(openingBracket)) {
			sql = openingBracket + sql;
		}
		if(StringUtils.hasText(closingBracket)) {
			sql += closingBracket;
		}
		return sql;
	}

}
