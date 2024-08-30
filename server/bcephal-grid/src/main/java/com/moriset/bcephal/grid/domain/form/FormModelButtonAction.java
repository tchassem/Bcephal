package com.moriset.bcephal.grid.domain.form;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
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

@Entity(name = "FormModelButtonAction")
@Table(name = "BCP_FORM_MODEL_BUTTON_ACTION")
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelButtonAction extends Persistent {

	private static final long serialVersionUID = 7308424242701981840L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_button_action_seq")
	@SequenceGenerator(name = "form_model_button_action_seq", sequenceName = "form_model_button_action_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private int position;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buttonId")
	private FormModelButton buttonId;
	
	@Enumerated(value = EnumType.STRING)
	private FormModelButtonType type;
		
	private Long fieldId;
	
	private Long schedulerId;
	
	@Embedded	
	private PeriodValue dateValue;
	
	private String stringValue;
	
	private BigDecimal decimalValue;
	
	
	public FormModelButtonAction() {
		this.dateValue = new PeriodValue();
	}
	
	public String buildValue(FormModelField field) {
		if(field.getDimensionType().isAttribute()) {
			return getStringValue() != null ? "'" + getStringValue().toString() + "'" : null;
		}
		else if(field.getDimensionType().isMeasure()) {
			return getDecimalValue() != null ? getDecimalValue().toString() : null;
		}
		else if(field.getDimensionType().isPeriod()) {
			Date date = getDateValue().buildDynamicDate();
			return date != null ? "'" + getDateValue().asDbString(date) + "'" : null;
		}
		return null;
	}

	@Override
	public FormModelButtonAction copy() {
		FormModelButtonAction copy = new FormModelButtonAction();
		copy.setPosition(position);
		copy.setType(type);
		//copy.setFieldId(fieldId);
		copy.setSchedulerId(schedulerId);
		copy.setDateValue(dateValue.copy());
		copy.setStringValue(stringValue);
		copy.setDecimalValue(decimalValue);
		return copy;
	}
	
}
