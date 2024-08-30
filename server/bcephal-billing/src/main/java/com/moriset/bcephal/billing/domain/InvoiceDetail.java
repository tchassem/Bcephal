/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

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
@Entity(name = "InvoiceDetail")
@Table(name = "BCP_BILLING_INVOICE_DETAIL")
@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceDetail extends Persistent {

	private static final long serialVersionUID = 1389079201781359474L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_invoice_detail_seq")
	@SequenceGenerator(name = "billing_invoice_detail_seq", sequenceName = "billing_invoice_detail_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@JsonIgnore
	private Long invoice;
	
	private Long eventId;
	
	private int position;
	
	private String description;
	
	private Date date;
	
	private BigDecimal quantity;
	
	private String unit;
	
	private BigDecimal unitCost;
	
	private BigDecimal amountWithoutVat;
		
	private BigDecimal vatRate;

	private String currency;


	@Override
	public Persistent copy() {
		return null;
	}
	
}
