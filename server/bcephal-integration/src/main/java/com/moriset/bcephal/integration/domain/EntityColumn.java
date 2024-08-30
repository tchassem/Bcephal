package com.moriset.bcephal.integration.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
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
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "EntityColumn")
@Table(name = "BCP_SEC_ENTITY_COLUMNS")
@Data
@EqualsAndHashCode(callSuper = false)
public class EntityColumn extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2760284628737384050L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_columns__seq")
	@SequenceGenerator(name = "entity_columns__seq", sequenceName = "entity_columns__seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING)
	private DimensionType type;

	private String name;

	private int position;

	@Embedded
	private DimensionFormat format;
	
	private boolean defaultValue;
	
	private String stringValue;	
	
	private BigDecimal decimalValue;
	
	private String copyReferenceName;
	
	@Enumerated(EnumType.STRING)
	private EntityReferenceType referenceType;
	
	@Enumerated(EnumType.STRING)
	private EntityOperationType operationType;
	
	@Embedded	
	private PeriodValue dateValue;
	
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "connectEntity")
	private PontoConnectEntity connectEntity;

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	public EntityColumn() {
		format = new DimensionFormat();
		type = DimensionType.ATTRIBUTE;
		dateValue = new PeriodValue();
		referenceType = EntityReferenceType.TRANSATION;
		operationType = EntityOperationType.COPY;
	}
	
	public void setDateValue(PeriodValue dateValue) {
		if(dateValue == null) {
			this.dateValue = new PeriodValue();
		}else {
			this.dateValue = dateValue;
		}
	}
	
	public void setFormat(DimensionFormat format) {
		if(format == null) {
			this.format = new DimensionFormat();
		}else {
			this.format = format;
		}
	}
	
	@PostLoad
	public void initListChangeHandler() {
		if(dateValue == null) {
			 dateValue = new PeriodValue();
		}
		if(format == null) {
			format = new DimensionFormat();
		}
	}
}
