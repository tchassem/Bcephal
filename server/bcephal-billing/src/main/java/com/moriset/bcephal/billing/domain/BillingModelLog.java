/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;

import jakarta.persistence.Entity;
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
@Entity(name = "BillingModelLog")
@Table(name = "BCP_BILLING_MODEL_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelLog extends MainObject {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_log_seq")
	@SequenceGenerator(name = "billing_model_log_seq", sequenceName = "billing_model_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long billing;
	
	private String billingName;
	
	private Long billingTypeId;
	
	private String billingTypeName;
	
	private Timestamp endDate;
	
	private RunStatus status;
	
	private RunModes mode;
		
	private String username;
			
	private long eventCount;
	
	private long periodCount;	
	
	private long clientCount;
	
	private long groupCount;
	
	private long categoryCount;
	
	private long invoiceCount;
	
	@Override
	public Persistent copy() {
		return null;
	}

}
