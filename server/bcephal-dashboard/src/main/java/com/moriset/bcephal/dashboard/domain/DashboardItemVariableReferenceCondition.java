package com.moriset.bcephal.dashboard.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.service.condition.VariableReferenceConditionItemType;

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

@Entity(name =  "DashboardItemVariableReferenceCondition")
@Table(name = "BCP_DASHBOARD_ITEM_VARIABLE_REFERENCE_CONDITION")
@Data
@EqualsAndHashCode(callSuper=false)
public class DashboardItemVariableReferenceCondition extends Persistent {

	private static final long serialVersionUID = 3008783542101569087L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_item_var_reference_cond_seq")
	@SequenceGenerator(name = "dashboard_item_var_reference_cond_seq", sequenceName = "dashboard_item_var_reference_cond_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reference")
	private DashboardItemVariableReference reference;
	
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
	
	@Embedded
	private PeriodValue periodValue;	
	
	private String variavleName;
	
	
	@Override
	public DashboardItemVariableReferenceCondition copy() {
		DashboardItemVariableReferenceCondition p = new DashboardItemVariableReferenceCondition();
		p.setClosingBracket(getClosingBracket());
		p.setComparator(getComparator());
		p.setConditionItemType(getConditionItemType());
		p.setDecimalValue(getDecimalValue());
		p.setVariavleName(variavleName);
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
