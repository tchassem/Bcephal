/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.moriset.bcephal.domain.MainObject;
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
@Entity(name = "BillingRunOutcome")
@Table(name = "BCP_BILLING_RUN_OUTCOME")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingRunOutcome extends MainObject {

	private static final long serialVersionUID = -1546257208893867224L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_run_outcome_seq")
	@SequenceGenerator(name = "billing_run_outcome_seq", sequenceName = "billing_run_outcome_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String runNumber;
	
	@Enumerated(EnumType.STRING) 
	private InvoiceStatus status;
	
	@Enumerated(EnumType.STRING) 
    private RunModes mode;
	
	private String username;
	
	private long invoiceCount;
	
	private BigDecimal invoiceAmount;
	
	private long creditNoteCount;
	
	private BigDecimal creditNoteAmount;
		
	private Date periodFrom;
	
	private Date periodTo;

	private String operationCode;
	
	public BigDecimal getInvoiceAmount(){
		if(invoiceAmount == null) {
			invoiceAmount = BigDecimal.ZERO;
		}
		return invoiceAmount;
	}
	
	public BigDecimal getCreditNoteAmount(){
		if(creditNoteAmount == null) {
			creditNoteAmount = BigDecimal.ZERO;
		}
		return creditNoteAmount;
	}
	
	@Override
	public Persistent copy() {
		return null;
	}
	
}
