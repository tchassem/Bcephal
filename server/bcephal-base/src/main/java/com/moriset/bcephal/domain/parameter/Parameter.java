/**
 * 
 */
package com.moriset.bcephal.domain.parameter;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.ListChangeHandler;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "Parameter")
@Table(name = "BCP_PARAMETER")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Parameter implements IPersistent {

	private static final long serialVersionUID = 1867111201914489839L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parameter_seq")
	@SequenceGenerator(name = "parameter_seq", sequenceName = "parameter_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String code;

	private String parentCode;

	@Enumerated(EnumType.STRING)
	private ParameterType parameterType;

	private Integer integerValue;

	private Long longValue;

	private BigDecimal decimalValue;

	private String stringValue;

	private Date dateValue;

	private Boolean booleanValue;

	@Transient
	private ListChangeHandler<Parameter> parameters;

	public Parameter() {

	}

	public Parameter(String code, ParameterType type) {
		this.code = code;
		this.parameterType = type;
	}

	@JsonIgnore
	public boolean isPeriod() {
		return this.getParameterType() == ParameterType.PERIOD;
	}

	@JsonIgnore
	public boolean isMeasure() {
		return this.getParameterType() == ParameterType.MEASURE;
	}

	@JsonIgnore
	public boolean isAttribute() {
		return this.getParameterType() == ParameterType.ATTRIBUTE;
	}

	@JsonIgnore
	public boolean isValue() {
		return this.getParameterType() == ParameterType.ATTRIBUTE_VALUE;
	}

	
	public void setParameterType(String parameterType) {
		if(StringUtils.hasText(parameterType)) {
			this.parameterType = ParameterType.valueOf(parameterType);
		}		
	}
}
