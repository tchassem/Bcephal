package com.moriset.bcephal.planification.domain.routine;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.MeasureOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "TransformationRoutineSpotCondition")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_SPOT_CONDITION")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineSpotCondition extends Persistent {
	
	private static final long serialVersionUID = -1958445184979523018L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_spot_cond_seq")
	@SequenceGenerator(name = "transformation_routine_spot_cond_seq", sequenceName = "transformation_routine_spot_cond_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "spotId")
	private TransformationRoutineSpot spotId;
			
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	@Enumerated(EnumType.STRING)
	private MappingConditionSide side1;
	
	@Enumerated(EnumType.STRING)
	private MappingConditionSide side2;
	
	private Long columnId1;
		
	private Long columnId2;
	
	private String verb;
	
	private String openingBracket;
	
	private String closingBracket;
	
	private String operator;
	
	private int position;
	
	
	private String stringValue;
	
	private BigDecimal decimalValue;
	
	@Embedded
	private PeriodValue periodValue;
	
	private boolean ecludeNullAndEmptyValue;
	
	private boolean ignoreCase;
	
	
	public TransformationRoutineSpotCondition() {
		ignoreCase = true;
		periodValue = new PeriodValue();
	}
	
	
	public String getSql(String col1, String col2) {
		String sql = "";
		String data = null;
		if(dimensionType.isAttribute()) {
			data = getSqlForAttribute(col1, col2);						
		}
		else if(dimensionType.isPeriod()) {
			data = getSqlForPeriod(col1, col2);			
		}
		else if(dimensionType.isMeasure()) {
			data = getSqlForMeasure(col1, col2);			
		}
		if(data != null) {				
			sql += this.getOpeningBracket() != null ? this.getOpeningBracket() : "";
			sql += data;
			sql += this.getClosingBracket() != null ? this.getClosingBracket() : "";
		}
		return sql;
	}
	
	private String getSqlForAttribute(String col1, String col2){
		String sql = null;		
		if(isEcludeNullAndEmptyValue() && col2 == null) {			
			if(!StringUtils.hasText(this.getStringValue())) {
				return "false";
			}
		}
		
		String part = null;
		AttributeOperator operation = AttributeOperator.valueOf(this.operator);	
		if (col2 == null || StringUtils.hasText(this.getStringValue())) {
			if (operation != null) {			
				String s = operation.buidSql(this.getStringValue());
				part = col1  + " " + s;				
				if(operation.isNull()) {
					part = "(".concat(col1).concat(" ").concat(s).concat(" OR ").concat(col1).concat(" = '')");
				}
				else if(operation.isNotNull()) {
					part = "(".concat(col1).concat(" ").concat(s).concat(" AND ").concat(col1).concat(" != '')");
				}
			}
		}	
		else {
			if (operation != null) {	
				String colValue = col1;
				String paramValue = col2;
				if(getSide1().isReferenceGrid()) {	
					colValue = isIgnoreCase() ? "UPPER(" + col1 + ")" : col1;
				}
				if(getSide2().isReferenceGrid()) {	
					paramValue = isIgnoreCase() ? "UPPER(" + col1 + ")" : col1;
				}
				
				if(getSide1().isTargetGrid()) {	
					colValue = isIgnoreCase() ? "UPPER(" + col2 + ")" : col2;
				}
				if(getSide2().isTargetGrid()) {	
					paramValue = isIgnoreCase() ? "UPPER(" + col2 + ")" : col2;
				}
				
				part = paramValue.concat(" ").concat(operation.buidColSql(colValue));
				
				if(getSide2().isFree()) {	
					paramValue = isIgnoreCase() ? getStringValue().toUpperCase() : getStringValue().toString();
					part = "'" + paramValue + "'".concat(" ").concat(operation.buidColSql(colValue));
				}
				
			}	
		}
		if (StringUtils.hasText(part)) {
			if (!StringUtils.hasText(sql)) {
				sql = part;
			}
			else {
				sql = "(" + sql + " AND " + part + ")";
			}
		}
		return sql;
		
	}
	
	private String getSqlForPeriod(String col1, String col2){
		String sql = null;
		boolean isNull = AttributeOperator.NULL.name().equalsIgnoreCase(this.operator);	
		boolean isNotNull = AttributeOperator.NOT_NULL.name().equalsIgnoreCase(this.operator);
		if(isNull || isNotNull) {
			sql = col1 + (isNull ? " IS NULL " : " IS NOT NULL ");
		}
		else {
			if(col2 != null) {
				sql = col1 + " " + this.operator + " " + col2;
			}
			else {
				String value = buildPeriodValue(getPeriodValue().buildDynamicDate());
				sql = col1 + " " + this.operator + " '" + value + "'";
			}
		}
		return sql;
	}
	
	private String getSqlForMeasure(String col1, String col2){
		String sql = null;
		boolean isNull = AttributeOperator.NULL.name().equalsIgnoreCase(this.operator);	
		boolean isNotNull = AttributeOperator.NOT_NULL.name().equalsIgnoreCase(this.operator);
		if(isNull || isNotNull) {
			sql = col1 + (isNull ? " IS NULL " : " IS NOT NULL ");
		}
		else {
			if(col2 != null) {
				sql = col1 + " " + this.operator + " " + col2;
			}
			else {
				sql = col1 + " " + this.operator + " " + getDecimalValue();
			}
		}
		return sql;		
	}
	
	protected String applyMeasureOperator(String col, BigDecimal value){
		String sql = null;
		String operator = this.operator;		
		if(MeasureOperator.EQUALS.equalsIgnoreCase(operator)){
			BigDecimal measure = buildDecimalValue(value);
			if(measure != null) sql = col + " = " + measure;
		}
		else if(MeasureOperator.NOT_EQUALS.equalsIgnoreCase(operator)){
			BigDecimal measure = buildDecimalValue(value);
			if(measure != null) sql = col + " <> " + measure;		
		}
		else if(MeasureOperator.GRETTER_OR_EQUALS.equalsIgnoreCase(operator)){
			BigDecimal measure = buildDecimalValue(value);
			if(measure != null) sql = col + " >= " + measure;
		}
		else if(MeasureOperator.LESS_OR_EQUALS.equalsIgnoreCase(operator)){
			BigDecimal measure = buildDecimalValue(value);
			if(measure != null) sql = col + " <= " + measure;
		}
		else if(MeasureOperator.LESS_THAN.equalsIgnoreCase(operator)){
			BigDecimal measure = buildDecimalValue(value);
			if(measure != null) sql = col + " < " + measure;
		}
		else if(MeasureOperator.GRETTER_THAN.equalsIgnoreCase(operator)){
			BigDecimal measure = buildDecimalValue(value);
			if(measure != null) sql = col + " > " + measure;
		}				
		return sql;
	}
	
	protected String applyMeasureOperatorForNullValue(String col){
		if(MeasureOperator.EQUALS.equals(this.operator)) {
			return "(" + col + " IS NULL OR " + col + " = 0)";
		}
		else if(MeasureOperator.NOT_EQUALS.equals(this.operator)) {
			return "(" + col + " IS NOT NULL AND " + col + " != 0)";
		}
		else if(MeasureOperator.GRETTER_THAN.equals(this.operator)) {
			return col + " > 0";
		}
		else if(MeasureOperator.GRETTER_OR_EQUALS.equals(this.operator)) {
			return "(" + col + " IS NULL OR " + col + " >= 0)";
		}
		else if(MeasureOperator.LESS_THAN.equals(this.operator)) {
			return col + " < 0";
		}
		else if(MeasureOperator.LESS_OR_EQUALS.equals(this.operator)) {
			return "(" + col + " IS NULL OR " + col + " <= 0)";
		}			
		String part1 = side1.isReferenceGrid() ? "NULL" : col;
		String part2 = side1.isReferenceGrid() ? col : "NULL";
		return part1.concat(" ").concat(this.operator).concat(" ").concat(part2);
	}
	
	private String buildPeriodValue(Object value) {
		String periodValue = null;
		if(value != null) {		
			Date date = null;
			SimpleDateFormat[] Formats = new SimpleDateFormat[]{				 
					new SimpleDateFormat("yyyy/MM/dd"), new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"),
					new SimpleDateFormat("yyyy-MM-dd"), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"),				
					new SimpleDateFormat("dd-MM-yyyy"), new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"),
					new SimpleDateFormat("dd/MM/yyyy"), new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")};
			int i = Formats.length;
			while (date == null && i > 0) {
				SimpleDateFormat f = Formats[i-1];
				try{				
					date = value instanceof Date ? (Date)value : f.parse(value.toString());
					if(date != null) {						
						periodValue = new SimpleDateFormat("yyyy-MM-dd").format(date);
					}
				}catch(Exception e){}
				i--;
			}			
		}
		return periodValue;
	}
	
	
	
	
	private BigDecimal buildDecimalValue(Object value){
		BigDecimal decimalValue = null;
		if(value != null) {
			try {
				decimalValue = value instanceof Number ? new BigDecimal(((Number)value).doubleValue()): new BigDecimal(value.toString().trim().replaceAll(",", "."));
			} catch(Exception e){}
		}
		return decimalValue;
	}
	
	
	@JsonIgnore
	@Override
	public TransformationRoutineSpotCondition copy() {
		TransformationRoutineSpotCondition copy = new TransformationRoutineSpotCondition();
		copy.setDimensionType(dimensionType);
		copy.setSide1(side1);
		copy.setSide2(side2);
		copy.setColumnId1(columnId1);
		copy.setColumnId2(columnId2);
		copy.setVerb(verb);
		copy.setOpeningBracket(openingBracket);
		copy.setClosingBracket(closingBracket);
		copy.setOperator(operator);
		copy.setPosition(position);
		copy.setStringValue(stringValue);
		copy.setDecimalValue(decimalValue);
		copy.setPeriodValue(periodValue != null ? periodValue.copy() : new PeriodValue());
		copy.setIgnoreCase(ignoreCase);
		copy.setEcludeNullAndEmptyValue(ecludeNullAndEmptyValue);
		return copy;
	}
	
}
