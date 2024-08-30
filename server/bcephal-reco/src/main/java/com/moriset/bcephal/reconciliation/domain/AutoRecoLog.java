/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;

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
@Entity(name = "AutoRecoLog")
@Table(name = "BCP_AUTO_RECO_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class AutoRecoLog extends Persistent {
	
	private static final long serialVersionUID = 3687855201366814247L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auto_reco_log_seq")
	@SequenceGenerator(name = "auto_reco_log_seq", sequenceName = "auto_reco_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long recoId;
	
	private String recoName;
	
	private Long recoAttributeId;
	
	private String recoAttributeName;
	
//	@JsonSerialize(using = JsonDateTimeSerializer.class)
//	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
//	private Timestamp endDate;
	
	@Enumerated(EnumType.STRING) 
	private RunStatus status;
	
	@Enumerated(EnumType.STRING)
	private RunModes mode;
		
	private String username;
		
	private Long leftRowCount;
	
	private Long rigthRowCount;	
	
	private Long reconciliatedLeftRowCount;
	
	private Long reconciliatedRigthRowCount;

	@JsonIgnore
	@Override
	public Persistent copy() {
		return null;
	}
	
}
