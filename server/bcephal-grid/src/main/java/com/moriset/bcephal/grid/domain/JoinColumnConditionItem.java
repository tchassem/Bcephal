package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "JoinColumnConditionItem")
@Table(name = "BCP_JOIN_COLUMN_CONDITION_ITEM")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinColumnConditionItem extends Persistent {
	
	private static final long serialVersionUID = -8766729166002655678L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_condition_seq")
	@SequenceGenerator(name = "join_column_condition_seq", sequenceName = "join_column_condition_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "propertiesId")
	private JoinColumnProperties propertiesId;
	
	private int position;	
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "thenField")
	private JoinColumnField thenField;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "itemId")
	private List<JoinColumnConditionItemOperand> operands;	
	@Transient
	private ListChangeHandler<JoinColumnConditionItemOperand> operandListChangeHandler;
	
	
	public JoinColumnConditionItem() {
		this.dimensionType = DimensionType.ATTRIBUTE;
		this.operands = new ArrayList<>();
		this.operandListChangeHandler = new ListChangeHandler<JoinColumnConditionItemOperand>();
	}
	
	
	public void setOperands(List<JoinColumnConditionItemOperand> operands) {
		this.operands = operands;
		operandListChangeHandler.setOriginalList(operands);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		operands.forEach(x->{});
		operandListChangeHandler.setOriginalList(operands);
	}	
	
	
	@JsonIgnore
	public String asSql(Join join, JoinColumnConditionItem item, Map<String, Object> parameters, String measureFunction, JoinColumnField field, Map<String, Object> variableValues) {
		String sql = null;	
		if(getThenField() != null && getOperands().size() > 0){
			String ifSql = "";
			for(JoinColumnConditionItemOperand operand : getOperands()) {
				String operandSql = operand.asSql(join, item, parameters, measureFunction, field, variableValues);
				if (StringUtils.hasText(operandSql)) {
					boolean isFirst = operand.getPosition() == 0;
					FilterVerb verb = operand.getVerb() != null ? operand.getVerb() : FilterVerb.AND;
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
						operandSql = "(" + operandSql + ")";
					}
					ifSql = ifSql.concat(verbString).concat(operandSql);
				}
			}
			if (StringUtils.hasText(ifSql)) {
				String thenpart = getCol(getThenField(), join, parameters, measureFunction, variableValues);
				//String key = "param_" + RandomUtils.nextInt(0, 100000) + "_" + System.currentTimeMillis();
				sql = "WHEN " + ifSql + " THEN " + thenpart;
				//parameters.put(key, thenpart);
			}
		}
		
		return StringUtils.hasText(sql) ? sql : "null";
	}
	
	
	@JsonIgnore
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


	@JsonIgnore
	public String getGroupByCols(Join grid) {
		String sql = "";
		String coma = "";
		if(getThenField() != null && getOperands().size() > 0){
			for(JoinColumnConditionItemOperand operand : getOperands()) {
				if(operand.getField1() != null) {
					String part = operand.getField1().getGroupByCols(grid);
					if(part != null && StringUtils.hasText(part.trim())) {
						sql += coma + part;
						coma = ", ";
					}
				}
				if(operand.getField2() != null) {
					String part = operand.getField2().getGroupByCols(grid);
					if(part != null && StringUtils.hasText(part.trim())) {
						sql += coma + part;
						coma = ", ";
					}
				}
			}
			String thenpart = getThenField().getGroupByCols(grid);
			if(thenpart != null && StringUtils.hasText(thenpart.trim())) {
				sql += coma + thenpart;
				coma = ", ";
			}
		}
		return StringUtils.hasText(sql) ? sql : null;
	}
	

	@Override
	public Persistent copy() {
		JoinColumnConditionItem copy = new JoinColumnConditionItem();
		copy.setDimensionType(getDimensionType());
		copy.setPosition(getPosition());
		//copy.setPropertiesId(getPropertiesId());
		
		if(getThenField() != null) {
			JoinColumnField joinColumnField = (JoinColumnField)getThenField().copy();
			copy.setThenField(joinColumnField);
		}
		for (JoinColumnConditionItemOperand item : this.getOperandListChangeHandler().getItems()) {
			if (item == null)
				continue;
			JoinColumnConditionItemOperand copyField = (JoinColumnConditionItemOperand)item.copy();
			copy.getOperandListChangeHandler().addNew(copyField);
		}
		
		return copy;
	}
	
	
	public boolean updateCopy(Join copy, HashMap<Long, com.moriset.bcephal.grid.domain.JoinColumn> columns) {
		boolean updated = false;
		if(getThenField() != null) {
			boolean res = getThenField().updateCopy(copy, columns);
			if(res) {
				updated = true;
			}
		}
		setOperands(this.getOperandListChangeHandler().getItems());
		for (JoinColumnConditionItemOperand item : this.getOperands()) {
			if (item == null) continue;
			boolean res = item.updateCopy(copy, columns);
			if(res) {
				getOperandListChangeHandler().addUpdated(item);
				updated = true;
			}
		}
		return updated;
	}

}
