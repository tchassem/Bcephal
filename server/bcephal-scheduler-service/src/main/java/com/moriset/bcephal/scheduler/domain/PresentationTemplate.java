package com.moriset.bcephal.scheduler.domain;

import com.moriset.bcephal.domain.MainObject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "PresentationTemplate")
@Table(name = "BCP_PRESENTATION_TEMPLATE")
@Data
@EqualsAndHashCode(callSuper = false)
public class PresentationTemplate extends MainObject {

	private static final long serialVersionUID = -7298634683488191320L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presentation_template_seq")
	@SequenceGenerator(name = "presentation_template_seq", sequenceName = "presentation_template_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String code;
	
	private String repository;
	
	private boolean hasHeader;
	
	private boolean hasFooter;

	@Override
	public PresentationTemplate copy() {
		PresentationTemplate copy = new PresentationTemplate();
		copy.setCode(code);
		copy.setRepository(repository);
		copy.setDescription(getDescription());
		copy.setName(getName());
		copy.setHasFooter(hasFooter);
		setHasHeader(hasHeader);
		return copy;
	}
	
}
