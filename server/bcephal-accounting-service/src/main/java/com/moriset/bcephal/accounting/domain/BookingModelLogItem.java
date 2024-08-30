/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.moriset.bcephal.domain.Persistent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author MORISET-004
 *
 */
@Entity(name = "BookingModelLogItem")
@Table(name = "BCP_ACCOUNTING_BOOKING_MODEL_LOG_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class BookingModelLogItem  extends Persistent {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6120421483911482601L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_model_item_seq")
	@SequenceGenerator(name = "booking_model_item_seq", sequenceName = "booking_model_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	public enum BookingModelLogItemStatus{OK, KO}
	private Long logId;	
	private String name;	
	private String message;		
	private BigDecimal creditAmount;	
	private BigDecimal debitAmount;	
	private BigDecimal balanceAmount;			
	@Enumerated(EnumType.STRING) 
	private BookingModelLogItemStatus status;				
	private Long accountCount;	
	private Long postingEntryCount;
	
				
	public BookingModelLogItem(){
		this.accountCount = 0L;
		this.postingEntryCount = 0L;
		this.creditAmount = BigDecimal.ZERO;
		this.debitAmount = BigDecimal.ZERO;
		this.balanceAmount = BigDecimal.ZERO;
	}


	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
}

