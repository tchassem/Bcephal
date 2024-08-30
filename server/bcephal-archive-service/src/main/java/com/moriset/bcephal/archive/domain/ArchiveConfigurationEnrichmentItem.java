/**
 * 
 */
package com.moriset.bcephal.archive.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * this class configure enrichment item
 * 
 * @author MORISET-004
 *
 */
@Entity(name = "ArchiveConfigurationEnrichmentItem")
@Table(name = "BCP_ARCHIVE_CONFIGURATION_ENRICHMENT_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class ArchiveConfigurationEnrichmentItem extends Persistent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3813704054207190390L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archive_config_enrich_item_seq")
	@SequenceGenerator(name = "archive_config_enrich_item_seq", sequenceName = "archive_config_enrich_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "configurationId")
	private ArchiveConfiguration configurationId;

	private BigDecimal decimalValue;

	private String stringValue;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateValue;

	private String dateDynamicPeriod;

	private String dateOperation;

	private Integer dateOperationNumber;

	private String dateOperationGranularity;

	private int position;

	@Enumerated(EnumType.STRING)
	private ParameterType type;

	private Long sourceId;

	
	@Transient
	private PeriodValue periodValue;

	@JsonIgnore
	@Transient
	public GrilleColumn column;

	@JsonIgnore
	@Transient
	public Object columnValue;

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@PostLoad
	private void postLoad() {
		periodValue = new PeriodValue();
		if(StringUtils.hasText(dateOperationGranularity)) {
			periodValue.setDateGranularity(PeriodGranularity.valueOf(dateOperationGranularity));
		}
		if(dateOperationNumber != null) {
			periodValue.setDateNumber(dateOperationNumber);
		}
		if(StringUtils.hasText(dateOperationGranularity)) {
			periodValue.setDateOperator(PeriodOperator.valueOf(dateOperation));
		}
		periodValue.setDateSign(dateDynamicPeriod);
		periodValue.setDateValue(dateValue);
	}
	
	
	public void prePersistOrUpdate() {
		if(periodValue != null) {
			if(periodValue.getDateGranularity() != null) {
				dateOperationGranularity = periodValue.getDateGranularity().name();
			}else {
				dateOperationGranularity = null;
			}
		
			dateOperationNumber = periodValue.getDateNumber();
			if(periodValue.getDateOperator() != null) {
				dateOperation = periodValue.getDateOperator().name();
			}else {
				dateOperation = null;
			}
			dateDynamicPeriod = periodValue.getDateSign();
			dateValue = periodValue.getDateValue();
		}
	}
	
	@JsonIgnore
	public String getUniverseColumnName() {
		if (getSourceId() != null && getType() != null) {
			if (getType() == ParameterType.ATTRIBUTE) {
				return new Attribute(sourceId, "").getUniverseTableColumnName();
			}
			if (getType() == ParameterType.MEASURE) {
				return new Measure(sourceId, "").getUniverseTableColumnName();
			}
			if (getType() == ParameterType.PERIOD && periodValue != null) {
				return new Period(sourceId, "").getUniverseTableColumnName();
			}
			if (getType() == ParameterType.BACKUP_GRID && column != null) {
				return column.getUniverseTableColumnName();
			}
		}
		return null;
	}

	@JsonIgnore
	public Object getValue() {
		if (getSourceId() != null && getType() != null) {
			if (getType() == ParameterType.BACKUP_GRID) {
				return columnValue;
			}
			if (getType() == ParameterType.ATTRIBUTE) {
				return getStringValue();
			}
			if (getType() == ParameterType.MEASURE) {
				return getDecimalValue();
			}
			if (getType() == ParameterType.PERIOD) {
//				if(getDateDynamicPeriod() != null) {
//					Period item = new Period();
//					item.dynamicPeriod = getDateDynamicPeriod();
//					item.numberPeriod = getDateOperationNumber() != null ? getDateOperationNumber() : null;
//					item.operationNumber = getDateOperationNumber() != null ? "" + getDateOperationNumber() : null;
//					item.operationGranularity = getDateOperationGranularity();
//					item.operation = getDateOperation();
//					item.value = dateValue;			
//					item.buildDynamicDateOf();
//					return item.value;
//				}
				return dateValue;
			}
		}
		return null;
	}
	
	public String getBackupValueSql(String table) {
		if(column != null && getType() == ParameterType.BACKUP_GRID) {
			String col = column.getUniverseTableColumnName();
			if(col != null) {
				return "SELECT " + col + " FROM " + table + " WHERE " + col + " IS NOT NULL LIMIT 1";
			}
		}
		return null;
	}
}
