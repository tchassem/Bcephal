package com.moriset.bcephal.grid.domain.form;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

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

@Entity(name = "FormModelFieldValidationItem")
@Table(name = "BCP_FORM_MODEL_FIELD_VALIDATION_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelFieldValidationItem extends Persistent {

	private static final long serialVersionUID = -6942516221381807037L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_field_val_item_seq")
	@SequenceGenerator(name = "form_model_field_val_item_seq", sequenceName = "form_model_field_val_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fieldId")
	private FormModelField fieldId;
	
	@Enumerated(EnumType.STRING)
	private FormModelFieldValidationType type;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	private Integer integerValue;
	
	private String stringValue;
	
	private BigDecimal decimalValue;
	
	@Embedded	
	private PeriodValue dateValue;
	
	private String errorMessage;
	
	private boolean active;
	
	
	public FormModelFieldValidationItem(){
		dateValue = new PeriodValue();
		active = true;
	}
	
	@JsonIgnore
	public Set<String> getLabelCodes() {
		Set<String> labels = new HashSet<>();
		if(StringUtils.hasText(getErrorMessage())) {
			labels.add(getErrorMessage());
		}
		return labels;
	}
	
	public void setLabels(Map<String, String> labels) {
		if(StringUtils.hasText(getErrorMessage())) {
			String value = labels.get(getErrorMessage());
			if(StringUtils.hasText(value)) {
				setErrorMessage(value);
			}
		}
	}
	

	@Override
	public Persistent copy() {
		return null;
	}
	
}
