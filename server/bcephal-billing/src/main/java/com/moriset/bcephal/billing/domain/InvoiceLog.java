/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.util.Date;

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
@Entity(name = "InvoiceLog")
@Table(name = "BCP_BILLING_INVOICE_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceLog extends Persistent {
	 
	private static final long serialVersionUID = 8623856609449459625L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_invoice_log_seq")
	@SequenceGenerator(name = "billing_invoice_log_seq", sequenceName = "billing_invoice_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long invoice;
			
	private String username;
	
	@Enumerated(EnumType.STRING) 
	private InvoiceStatus status;
	
	private String file;
		
	private int version;
	
	private BigDecimal amountWithoutVatBefore;
	
	private BigDecimal vatAmountBefore;
	
	private BigDecimal totalAmountBefore;
	
	private BigDecimal amountWithoutVatAfter;
	
	private BigDecimal vatAmountAfter;
	
	private BigDecimal totalAmountAfter;
	
	private Date date;
	
	private String oldValue;
	
	private String newValue;
	
	@Enumerated(EnumType.STRING) 
    private RunModes mode;

	private String operationCode;
	
	@Override
	public Persistent copy() {
		return null;
	}

}
