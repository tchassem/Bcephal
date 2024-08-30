package com.moriset.bcephal.messenger.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Parameter")
@Table(name = "BCP_PARAMETER")
@Data
@NoArgsConstructor
public class Parameter {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parameter_seq")
	@SequenceGenerator(name = "parameter_seq", sequenceName = "parameter_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String code;	
	
	@Column(name="booleanvalue")
	private boolean booleanValue;
	
	@Column(name="stringvalue")
	private String stringValue;
	
	@Column(name="longvalue")
	private Long longValue;
	
	@Column(name="decimalvalue")
	private BigDecimal decimalValue;
	
	@Column(name="datevalue")
	private Date dateValue;
	
}
