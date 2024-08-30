/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.MeasureOperator;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;

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

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "ReconciliationCondition")
@Table(name = "BCP_RECONCILIATION_CONDITION")
@Data
@EqualsAndHashCode(callSuper = false)
public class ReconciliationCondition extends Persistent {

	private static final long serialVersionUID = -5928814082607015523L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_condition_seq")
	@SequenceGenerator(name = "reconciliation_condition_seq", sequenceName = "reconciliation_condition_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "autoRecoId")
	private AutoReco autoRecoId;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "recoModelId")
	private ReconciliationModel recoModelId;
		
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	@Enumerated(EnumType.STRING)
	private ReconciliationModelSide side1;
	
	@Enumerated(EnumType.STRING)
	private ReconciliationModelSide side2;
	
	private Long columnId1;
		
	private Long columnId2;
	
	private String verb;
	
	private String openingBracket;
	
	private String closingBracket;
	
	private String operator;
	
	private int position;
			
	@Enumerated(EnumType.STRING) 
	private AutoRecoPeriodCondition periodCondition;
	
	private String dateSign;

	private int dateNumber;

	@Enumerated(EnumType.STRING)
	private PeriodGranularity dateGranularity;
	
	private boolean ecludeNullAndEmptyValue;
	
	private Boolean ignoreCase;
	
	
	@JsonIgnore @Transient
	private boolean secondaryToPrimary;
	
	@JsonIgnore @Transient
	private GrilleColumn column1;
	
	@JsonIgnore @Transient
	private GrilleColumn column2;
	
	@JsonIgnore @Transient
	private UnionGridColumn unionGridColumn1;
	
	@JsonIgnore @Transient
	private UnionGridColumn unionGridColumn2;
	
//	@JsonIgnore @Transient
//	private String dbColumn;
	
	@JsonIgnore @Transient
	private String primaryDbColumn;
	
	@JsonIgnore @Transient
	private String secondaryDbColumn;
	
	@JsonIgnore @Transient
	private Object value;
	
	
	public boolean getIgnoreCase()
	{
		if(ignoreCase == null) {
			ignoreCase = true;
		}
		return ignoreCase;
	}
	
	public String getSql(String col, Object value, DimensionType type) {
		String sql = "";
		String data = null;
		if(type.isAttribute()) {
			data = getSqlForAttribute(col, value);						
		}
		else if(type.isPeriod()) {
			data = getSqlForPeriod(col, value);			
		}
		else if(type.isMeasure()) {
			data = getSqlForMeasure(col, value);			
		}
		if(data != null) {			
//			if(isEcludeNullAndEmptyValue()) {
//				data = "(" + data + " AND " + col + " IS NOT NULL";
//				if(type.isAttribute()) {
//					data += " AND " + col + " != ''";						
//				}
//				data += ")";
//			}
			
			sql += this.getOpeningBracket() != null ? this.getOpeningBracket() : "";
			sql += data;
			sql += this.getClosingBracket() != null ? this.getClosingBracket() : "";
		}
		return sql;
	}
	
	private String getSqlForAttribute(String col, Object val){
		String sql = null;
		
		if(isEcludeNullAndEmptyValue()) {
			if(val == null || !StringUtils.hasText(val.toString())) {
				return "false";
			}
//			sql = col + " IS NOT NULL";
		}
		
		String part = null;
		AttributeOperator operation = AttributeOperator.valueOf(this.operator);	
		if (val == null || !StringUtils.hasText(val.toString())) {
			if (operation != null) {			
				if(getSide1().isLeft()) {				
					part = operation.buidColSqlForNullValue(col);
				}
				else {
					operation.buidSqlForNullValue(col);
				}			
			}
		}	
		else {
			if (operation != null) {	
				String colValue = getIgnoreCase() ? "UPPER(" + col + ")" : col;
				String paramValue = getIgnoreCase() ? val.toString().toUpperCase() : val.toString();
				if(getSide1().isLeft()) {	
					part = "'" + paramValue + "'".concat(" ").concat(operation.buidColSql(colValue));
				}
				else {
					String s = operation.buidSql(paramValue);
					part = colValue  + " " + s;				
					if(operation.isNull()) {
						part = "(".concat(colValue).concat(" ").concat(s).concat(" OR ").concat(colValue).concat(" = '')");
					}
					else if(operation.isNotNull()) {
						part = "(".concat(colValue).concat(" ").concat(s).concat(" AND ").concat(colValue).concat(" != '')");
					}
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
		
		
		
//		String value = val != null ? val.toString() : "";		
//		AttributeOperator operation = AttributeOperator.valueOf(this.operator);		
//		if (operation != null) {			
//			if(getSide1().isLeft()) {				
//				sql = "'" + value + "'".concat(" ").concat(operation.buidColSql(col));
//			}
//			else {
//				sql = col.concat(" ").concat(operation.buidSql(value));
//			}			
//		}		
//		return sql;
	}
	
	private String getSqlForPeriod(String col, Object val){
		String sql = null;
		if(isEcludeNullAndEmptyValue()) {
			if(val == null) {
				return "false";
			}
			sql = col + " IS NOT NULL";
		}
		String value = val != null ? buildPeriodValue(val) : null;
		String part = null;
		if(this.operator == null) {
			this.operator = "=";
		}
		if (StringUtils.hasText(value)) {
			part = applyPeriodGrouping(col, value);			
		}	
		else {
			part = applyPeriodGroupingForNullValue(col);
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
	
	
	protected String applyPeriodGrouping(String col, String value) {		
		String valuePart = "to_date('".concat(value).concat("', 'YYYY-MM-DD')");
		String part1 = null;
		String part2 = null;
		if(getPeriodCondition() == null || getPeriodCondition() == AutoRecoPeriodCondition.SAME_DAY) {
			part1 = side1.isLeft() ? valuePart : col;
			part2 = side1.isLeft() ? col : valuePart;
			part2 = buildDelta(part2);
		}		
		if(getPeriodCondition() == AutoRecoPeriodCondition.SAME_MONTH){	
			part1 = side1.isLeft() ? "date_part('month', to_date('".concat(value).concat("', 'YYYY-MM-DD'))")
					: "date_part('month', ".concat(col).concat(")");
			part2 = side1.isLeft() ? col : valuePart;
			part2 = buildDelta(part2);
			part2 = "date_part('month', " + part2 + ")";
		}
		else if(getPeriodCondition() == AutoRecoPeriodCondition.SAME_WEEK){
			part1 = side1.isLeft() ? "date_part('week', to_date('".concat(value).concat("', 'YYYY-MM-DD'))")
					: "date_part('week', ".concat(col).concat(")");
			part2 = side1.isLeft() ? col : valuePart;
			part2 = buildDelta(part2);
			part2 = "date_part('week', " + part2 + ")";
		}
		else if(getPeriodCondition() == AutoRecoPeriodCondition.SAME_YEAR){
			part1 = side1.isLeft() ? "date_part('year', to_date('".concat(value).concat("', 'YYYY-MM-DD'))")
					: "date_part('year', ".concat(col).concat(")");			
			part2 = side1.isLeft() ? col : valuePart;
			part2 = buildDelta(part2);
			part2 = "date_part('year', " + part2 + ")";	
		}
		if(StringUtils.hasText(part1) && StringUtils.hasText(part2)) {			
			String sql = part1.concat(" ").concat(this.operator).concat(" ").concat(part2);
			if(!isEcludeNullAndEmptyValue()) {
				//sql = "(" + col + " IS NULL OR " + sql + ")";
			}
			return sql;			
		}
		return null;	
	}
	
	
	protected String applyPeriodGroupingForNullValue(String col) {			
		if(MeasureOperator.EQUALS.equals(this.operator)) {
			return col + " IS NULL";
		}
		else if(MeasureOperator.NOT_EQUALS.equals(this.operator)) {
			return col + " IS NOT NULL";
		}
		else if(MeasureOperator.GRETTER_THAN.equals(this.operator)) {
			return col + " IS NOT NULL";
		}
		else if(MeasureOperator.GRETTER_OR_EQUALS.equals(this.operator)) {
			return "true";
		}
		else if(MeasureOperator.LESS_THAN.equals(this.operator)) {
			return "false";
		}
		else if(MeasureOperator.LESS_OR_EQUALS.equals(this.operator)) {
			return col + " IS NULL";
		}			
		String part1 = side1.isLeft() ? "NULL" : col;
		String part2 = side1.isLeft() ? col : "NULL";
		return part1.concat(" ").concat(this.operator).concat(" ").concat(part2);		
	}
	
	private String buildDelta(String column) {
		if(periodCondition != null) {
			if(!StringUtils.hasText(dateSign)) {
				dateSign = "+";
			}
			column = column.concat(" ").concat(dateSign).concat(" INTERVAL '" + dateNumber + " ").concat(dateGranularity.name() + "'");
		}
		return column;
	}
	
	
	
	private String getSqlForMeasure(String col, Object val){
		String sql = null;
		if(isEcludeNullAndEmptyValue()) {
			if(val == null) {
				return "false";
			}
			sql = col + " IS NOT NULL";
		}
		BigDecimal value = buildDecimalValue(val);
		String part = null;
		if (value != null && !BigDecimal.ZERO.equals(value)) {
			part = applyMeasureOperator(col, value);			
		}	
		else {
			part = applyMeasureOperatorForNullValue(col);
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
	
	private String applyMeasureOperator(String col, BigDecimal value){
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
	
	private String applyMeasureOperatorForNullValue(String col){
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
		String part1 = side1.isLeft() ? "NULL" : col;
		String part2 = side1.isLeft() ? col : "NULL";
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
	public ReconciliationCondition copy() {
		ReconciliationCondition copy = new ReconciliationCondition();
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
		copy.setPeriodCondition(periodCondition);
		copy.setDateSign(dateSign);
		copy.setDateNumber(dateNumber);
		copy.setDateGranularity(dateGranularity);
		copy.setEcludeNullAndEmptyValue(ecludeNullAndEmptyValue);	
				
		copy.setSecondaryToPrimary(secondaryToPrimary);		
		copy.setColumn1(column1);
		copy.setColumn2(column2);
		copy.setUnionGridColumn1(unionGridColumn1);
		copy.setUnionGridColumn2(unionGridColumn2);
		//copy.setDbColumn(dbColumn);
		copy.setPrimaryDbColumn(primaryDbColumn);
		copy.setSecondaryDbColumn(secondaryDbColumn);
		copy.setValue(value);
		
		return copy;
	}
	
}
