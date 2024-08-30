package com.moriset.bcephal.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "BLabel")
@Table(name = "BCP_LABEL")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BLabel implements IPersistent {

	private static final long serialVersionUID = 7367986538809377085L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "label_seq")
	@SequenceGenerator(name = "label_seq", sequenceName = "label_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	private String code;

	private String lang;

	private String value;

	@Enumerated(EnumType.STRING)
	private LabelCategory category;

	private int position;
	public BLabel() {
		
	}
}
