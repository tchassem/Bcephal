package com.moriset.bcephal.grid.domain.form;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.AbstractSmartGridColumn;

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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "FormModelSpotItem")
@Table(name = "BCP_FORM_MODEL_SPOT_ITEM")
@Data
@EqualsAndHashCode(callSuper=false)
public class FormModelSpotItem extends Persistent {

	private static final long serialVersionUID = 5880277285048708892L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_spot_item_seq")
	@SequenceGenerator(name = "form_model_spot_item_seq", sequenceName = "form_model_spot_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "spot")
	private FormModelSpot spot;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;	
	private Long dimensionId;
	private int position;			
	private String verb;
    private String openingBracket;
    private String closingBracket;
    private String comparator;	  
    
    @Enumerated(EnumType.STRING)
    private ReferenceConditionItemType conditionItemType;	
		
	private String stringValue;
	private BigDecimal decimalValue;
	
	@Embedded
	private PeriodValue periodValue;	
	
	private Long fieldId;
	
	
	@Override
	public FormModelSpotItem copy() {
		FormModelSpotItem p = new FormModelSpotItem();
		p.setClosingBracket(getClosingBracket());
		p.setComparator(getComparator());
		p.setConditionItemType(getConditionItemType());
		p.setDecimalValue(getDecimalValue());
		p.setFieldId(getFieldId());
		p.setDimensionType(getDimensionType());
		p.setDimensionId(getDimensionId());
		p.setOpeningBracket(getOpeningBracket());
		p.setPeriodValue(getPeriodValue() != null ? getPeriodValue().copy() : null);
		p.setPosition(getPosition());
		p.setStringValue(getStringValue());
		p.setVerb(getVerb());
		return p;
	}
	
	
	 @Transient
	 @JsonIgnore
	 private AbstractSmartGrid<?> grid;
	    
	@Transient
    @JsonIgnore
	private AbstractSmartGridColumn column;
	    
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
