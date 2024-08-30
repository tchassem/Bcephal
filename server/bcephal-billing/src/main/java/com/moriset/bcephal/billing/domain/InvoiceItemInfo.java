/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "InvoiceItemInfo")
@Table(name = "BCP_BILLING_INVOICE_ITEM_INFO")
@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceItemInfo extends Persistent {

	private static final long serialVersionUID = 7047425952611485577L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_invoice_item_info_seq")
	@SequenceGenerator(name = "billing_invoice_item_info_seq", sequenceName = "billing_invoice_item_info_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item")
	private InvoiceItem item;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice")
	private Invoice invoice;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	private int position;
	
	@Column(name = "tringValue")
	private String stringValue;
	
	private BigDecimal decimalValue;
	
	private Date dateValue1;
	
	private Date dateValue2;
	
	private boolean globalParameter;

	
	@Override
	public Persistent copy() {
		return null;
	}
	
}
