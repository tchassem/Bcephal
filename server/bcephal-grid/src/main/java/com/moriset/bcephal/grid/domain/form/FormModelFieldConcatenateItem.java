package com.moriset.bcephal.grid.domain.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

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


@Entity(name = "FormModelFieldConcatenateItem")
@Table(name = "BCP_FORM_MODEL_FIELD_CONCATENATE_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelFieldConcatenateItem extends Persistent {

	private static final long serialVersionUID = -2660542857273568911L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_field_concatenate_item_seq")
	@SequenceGenerator(name = "form_model_field_concatenate_item_seq", sequenceName = "form_model_field_concatenate_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentId")
	private FormModelField parentId;
	
	private int position;
	
	@Enumerated(EnumType.STRING)
	private ReferenceConditionItemType type;
	
	private String stringValue;
	
	private Long fieldId;

	@Override
	public Persistent copy() {
		return null;
	}
	
}
