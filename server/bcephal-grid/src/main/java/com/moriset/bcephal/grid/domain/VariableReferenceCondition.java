package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.service.condition.VariableReferenceConditionItemType;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
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

@Entity(name =  "VariableReferenceCondition")
@Table(name = "BCP_VARIABLE_REFERENCE_CONDITION")
@Data
@EqualsAndHashCode(callSuper=false)
public class VariableReferenceCondition extends Persistent {

	private static final long serialVersionUID = 7748668538645971626L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "var_reference_cond_seq")
	@SequenceGenerator(name = "var_reference_cond_seq", sequenceName = "var_reference_cond_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reference")
	private VariableReference reference;
	
	@Enumerated(EnumType.STRING)
	private DimensionType parameterType;	
	private int position;			
	private String verb;
    private String openingBracket;
    private String closingBracket;
    private String comparator;
	
    private Long keyId;
    
    @Enumerated(EnumType.STRING)
    private VariableReferenceConditionItemType conditionItemType;	
		
	private String stringValue;
	private BigDecimal decimalValue;
	
	@AttributeOverrides({
	    @AttributeOverride(name="variableName", 	column = @Column(name="periodValueVariableName"))
	})
	@Embedded
	private PeriodValue periodValue;	
	
	private String variableName;
	
	
	@Override
	public VariableReferenceCondition copy() {
		VariableReferenceCondition p = new VariableReferenceCondition();
		p.setClosingBracket(getClosingBracket());
		p.setComparator(getComparator());
		p.setConditionItemType(getConditionItemType());
		p.setDecimalValue(getDecimalValue());
		p.setVariableName(variableName);
		p.setKeyId(getKeyId());
		p.setOpeningBracket(getOpeningBracket());
		p.setParameterType(getParameterType());
		p.setPeriodValue(getPeriodValue() != null ? getPeriodValue().copy() : null);
		p.setPosition(getPosition());
		p.setStringValue(getStringValue());
		p.setVerb(getVerb());
		return p;
	}
	
}
