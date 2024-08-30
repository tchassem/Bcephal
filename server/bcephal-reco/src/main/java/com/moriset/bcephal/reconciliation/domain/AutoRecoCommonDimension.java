/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodValue;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "AutoRecoCommonDimension")
@Table(name = "BCP_AUTO_RECO_COMMON_DIMENSION")
@Data
@EqualsAndHashCode(callSuper = false)
public class AutoRecoCommonDimension extends Persistent {
	
	private static final long serialVersionUID = 3234923830535725220L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auto_reco_common_dimension_seq")
	@SequenceGenerator(name = "auto_reco_common_dimension_seq", sequenceName = "auto_reco_common_dimension_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long recoId;
		
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	private Long dimensionId;
	
	private String dimensionName;
			
	@Enumerated(EnumType.STRING) 
	private AutoRecoPeriodCondition periodCondition;
	
	@Embedded	
	private PeriodValue dateValue;

	
	@JsonIgnore
	@Override
	public Persistent copy() {
		return null;
	}

		
	
}
