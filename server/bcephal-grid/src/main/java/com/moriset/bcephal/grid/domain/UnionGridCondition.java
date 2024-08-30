package com.moriset.bcephal.grid.domain;

import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.AttributeOperator;

import jakarta.persistence.Entity;
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

@Entity(name = "UnionGridCondition")
@Table(name = "BCP_UNION_GRID_CONDITION")
@Data 
@EqualsAndHashCode(callSuper = false)
public class UnionGridCondition extends Persistent {

	
	private static final long serialVersionUID = -6443935179883934490L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "union_grid_condition_seq")
	@SequenceGenerator(name = "union_grid_condition_seq", sequenceName = "union_grid_condition_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unionGrid")
	private UnionGrid unionGrid;
	
	private int position;
	
	private String verb;
    private String openingBracket;
    private String closingBracket;
    private String comparator;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "item1")
	private UnionGridConditionItem item1;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "item2")
	private UnionGridConditionItem item2;
	
	
	@Override
	public UnionGridCondition copy() {
		UnionGridCondition copy = new UnionGridCondition();
		copy.setClosingBracket(getClosingBracket());
		copy.setComparator(getComparator());
		copy.setPosition(getPosition());
		copy.setVerb(getVerb());
		copy.setOpeningBracket(getOpeningBracket());
		copy.setItem1(getItem1() != null ? getItem1().copy() : null);
		copy.setItem2(getItem2() != null ? getItem2().copy() : null);
		return copy;
	}


	public String asSql(Map<String, Object> parameters, UnionGridItem unionGridItem) {
		String sql = "";
		String data = null;
		if(getItem1() != null) {
			String col = getItem1().getDbColName(unionGridItem.getGrid().getId());
			if(StringUtils.hasText(col)) {
				Object value = null;
				String col2 = null;
				if(getItem2() != null) {
					if(getItem2().getType() == JoinConditionItemType.PARAMETER) {
						value = getItem2().getDbParameterValue();
					}
					if(getItem2().getType() == JoinConditionItemType.COLUMN) {
						col2 = getItem2().getDbColName(unionGridItem.getGrid().getId());
					}
				}
				
				if(getItem1().isAttribute()) {
					data = getSqlForAttribute(col, col2, value, parameters, unionGridItem);						
				}
				else if(getItem1().isPeriod()) {
					data = getSqlForPeriod(col, col2, value, parameters, unionGridItem);			
				}
				else if(getItem1().isMeasure()) {
					data = getSqlForMeasure(col, col2, value, parameters, unionGridItem);			
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
	
	protected String getSqlForAttribute(String col, String col2, Object val, Map<String, Object> parameters, UnionGridItem unionGridItem){
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
	protected String getSqlForPeriod(String col, String col2, Object value, Map<String, Object> parameters, UnionGridItem unionGridItem){
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
				
				String key = "param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();				
				sql = col + " " + operator + " :" + key;
				parameters.put(key, value);
			}
			else {
				sql = col + " is null";
			}
		}					
		return sql;
	}
	
	@JsonIgnore
	protected String getSqlForMeasure(String col, String col2, Object value, Map<String, Object> parameters, UnionGridItem unionGridItem){
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
	
}
