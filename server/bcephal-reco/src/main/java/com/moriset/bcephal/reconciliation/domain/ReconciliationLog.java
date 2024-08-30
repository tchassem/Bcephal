/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
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
@Entity(name = "ReconciliationLog")
@Table(name = "BCP_RECONCILIATION_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class ReconciliationLog extends Persistent {
		
	private static final long serialVersionUID = -1549810246152678992L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_log_seq")
	@SequenceGenerator(name = "reconciliation_log_seq", sequenceName = "reconciliation_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	private Long reconciliation;
	
	private String username;
	
	private String recoType;
		
	@Enumerated(EnumType.STRING) 
	private ReconciliationActions action;
	
	private BigDecimal leftAmount;

	private BigDecimal rigthAmount;
	
	private BigDecimal balanceAmount;
	
	private BigDecimal writeoffAmount;
		
	private String reconciliationNbr;
	
	private String operationCode;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	
	@Override
	public Persistent copy() {
		return null;
	}
	
}
