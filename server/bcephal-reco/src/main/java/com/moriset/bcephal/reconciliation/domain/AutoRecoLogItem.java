/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;

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
@Entity(name = "AutoRecoLogItem")
@Table(name = "BCP_AUTO_RECO_LOG_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class AutoRecoLogItem extends Persistent {

	private static final long serialVersionUID = 868476699279895699L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auto_reco_log_item_seq")
	@SequenceGenerator(name = "auto_reco_log_item_seq", sequenceName = "auto_reco_log_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long logId;
	
	private String username;
	
	private Integer recoNumber;
	
	@Enumerated(EnumType.STRING) 
	private RunModes mode;
	
	private Long leftRowCount;
	
	private Long rigthRowCount;	
					
	private BigDecimal leftAmount;

	private BigDecimal rigthAmount;
	
	private BigDecimal balanceAmount;
	
	private BigDecimal writeoffAmount;


	@JsonIgnore
	@Override
	public Persistent copy() {
		return null;
	}
	
}
