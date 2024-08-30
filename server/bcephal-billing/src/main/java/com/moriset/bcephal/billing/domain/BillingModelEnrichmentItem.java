/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.grid.domain.JoinColumn;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "BillingModelEnrichmentItem")
@Table(name = "BCP_BILLING_MODEL_ENRICHMENT_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelEnrichmentItem extends Persistent {

	private static final long serialVersionUID = -6120882436124525475L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_enrichment_item_seq")
	@SequenceGenerator(name = "billing_model_enrichment_item_seq", sequenceName = "billing_model_enrichment_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "billing")
	public BillingModel billing;
	
	private int position;
	
	private BigDecimal decimalValue;
	
	private String stringValue;
	
	@Embedded	
	private PeriodValue dateValue;
		
	@Enumerated(EnumType.STRING)
	private DimensionType sourceType;
	
	private Long sourceId;	
		
	@JsonIgnore
	@Transient
	public JoinColumn column;	
	
	@JsonIgnore
	public String getUniverseTableColumnName() {
		if(sourceId != null && sourceType != null) {
			if(sourceType == DimensionType.ATTRIBUTE) {
				return new Attribute(sourceId, "").getUniverseTableColumnName();
			}
			if(sourceType == DimensionType.MEASURE) {
				return new Measure(sourceId, "").getUniverseTableColumnName();
			}
			if(sourceType == DimensionType.PERIOD) {
				return new Period(sourceId, "").getUniverseTableColumnName();
			}
			if(sourceType == DimensionType.BILLING_EVENT && column != null) {
				return column.getDbColName(false, false);
			}
		}
		return null;
	}
	
	@JsonIgnore
	public Object getValue() {
		if(getSourceId() != null && getSourceType() != null) {
			if(getSourceType() == DimensionType.ATTRIBUTE) {
				return getStringValue();
			}
			if(getSourceType() == DimensionType.MEASURE) {
				return getDecimalValue();
			}
			if(getSourceType() == DimensionType.PERIOD && dateValue != null) {
				return dateValue.buildDynamicDate();
			}
		}
		return null;
	}

	@Override
	public BillingModelEnrichmentItem copy() {
		BillingModelEnrichmentItem copy = new BillingModelEnrichmentItem();
		copy.setPosition(getPosition());
		copy.setDecimalValue(getDecimalValue());
		copy.setStringValue(getStringValue());
		copy.setDateValue(dateValue != null ? getDateValue().copy() : null);
		copy.setSourceType(getSourceType());
		copy.setSourceId(getSourceId());
		return copy;
	}
	
}
