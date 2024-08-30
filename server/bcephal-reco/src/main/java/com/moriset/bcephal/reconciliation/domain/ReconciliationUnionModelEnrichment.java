/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity(name = "ReconciliationUnionModelEnrichment")
@Table(name = "BCP_RECONCILIATION_UNION_MODEL_ENRICHMENT")
@Data 
@EqualsAndHashCode(callSuper = false)
public class ReconciliationUnionModelEnrichment extends Persistent {

	private static final long serialVersionUID = -4661921463609930050L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_union_model_enrichment_seq")
	@SequenceGenerator(name = "reconciliation_union_model_enrichment_seq", sequenceName = "reconciliation_union_model_enrichment_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "model")
	private ReconciliationUnionModel model;
	
	private int position;

	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
			
	@Enumerated(EnumType.STRING) 
	private ReconciliationModelSide targetSide;
	private Long targetColumnId;
	
	@Enumerated(EnumType.STRING) 
	private ReconciliationModelSide sourceSide;
	private Long sourceColumnId;
	
	private String stringValue;	
	private BigDecimal decimalValue;	
	@Embedded	
	private PeriodValue dateValue;
	
	
	public ReconciliationUnionModelEnrichment() {
		dateValue = new PeriodValue();
	}
	
	public void setDateValue(PeriodValue dateValue) {
		if(dateValue == null) {
			this.dateValue = new PeriodValue();
		}else {
			this.dateValue = dateValue;
		}
	}
	
	@PostLoad
	public void initListChangeHandler() {
		if(dateValue == null) {
			 dateValue = new PeriodValue();
		}
	}
	
	@JsonIgnore
	public boolean isAttribute() {
		return getDimensionType() != null && getDimensionType().isAttribute();
	}
	
	@JsonIgnore
	public boolean isMeasure() {
		return getDimensionType() != null && getDimensionType().isMeasure();
	}
	
	@JsonIgnore
	public boolean isPeriod() {
		return getDimensionType() != null && getDimensionType().isPeriod();
	}
	
	
	@Override
	public ReconciliationUnionModelEnrichment copy() {
		ReconciliationUnionModelEnrichment copy = new ReconciliationUnionModelEnrichment();
		copy.setPosition(position);
		copy.setDimensionType(dimensionType);
		copy.setTargetSide(targetSide);
		copy.setTargetColumnId(targetColumnId);
		copy.setSourceSide(sourceSide);
		copy.setSourceColumnId(sourceColumnId);
		copy.setStringValue(stringValue);
		copy.setDecimalValue(decimalValue);
		copy.setDateValue(dateValue.copy());
		return copy;
		
	}
	
}
