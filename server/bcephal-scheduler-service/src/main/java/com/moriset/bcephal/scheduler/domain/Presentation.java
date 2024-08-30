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

@Entity(name = "Presentation")
@Table(name = "BCP_PRESENTATION")
@Data
@EqualsAndHashCode(callSuper = false)
public class Presentation extends MainObject {

	private static final long serialVersionUID = 2743989184375715752L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presentation_seq")
	@SequenceGenerator(name = "presentation_seq", sequenceName = "presentation_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String code;
	
	private String repository;
	
	private String operationCode;
	
	@Override
	public Presentation copy() {
		Presentation copy = new Presentation();
		return copy;
	}
	
}
