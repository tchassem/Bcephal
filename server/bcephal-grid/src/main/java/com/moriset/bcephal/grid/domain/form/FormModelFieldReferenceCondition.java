package com.moriset.bcephal.grid.domain.form;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
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

@Entity(name = "FormModelFieldReferenceCondition")
@Table(name = "BCP_FORM_MODEL_FIELD_REFERENCE_CONDITION")
@Data
@EqualsAndHashCode(callSuper=false)
public class FormModelFieldReferenceCondition extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1666199504277523674L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_filed_reference_cond_seq")
	@SequenceGenerator(name = "form_model_filed_reference_cond_seq", sequenceName = "form_model_filed_reference_cond_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reference")
	private FormModelFieldReference reference;
	
	@Enumerated(EnumType.STRING)
	private DimensionType parameterType;	
	private int position;			
	private String verb;
    private String openingBracket;
    private String closingBracket;
    private String comparator;
	
    private Long keyId;
    
    @Enumerated(EnumType.STRING)
    private ReferenceConditionItemType conditionItemType;	
		
	private String stringValue;
	private BigDecimal decimalValue;
	
	@Embedded
	private PeriodValue periodValue;	
	
	private Long fieldId;
	
	
	@Override
	public Persistent copy() {
		FormModelFieldReferenceCondition p = new FormModelFieldReferenceCondition();
		p.setClosingBracket(getClosingBracket());
		p.setComparator(getComparator());
		p.setConditionItemType(getConditionItemType());
		p.setDecimalValue(getDecimalValue());
		p.setFieldId(getFieldId());
		p.setKeyId(getKeyId());
		p.setOpeningBracket(getOpeningBracket());
		p.setParameterType(getParameterType());
		p.setPeriodValue(getPeriodValue());
		p.setPosition(getPosition());
		p.setReference(getReference());
		p.setStringValue(getStringValue());
		p.setVerb(getVerb());
		return p;
	}


}
