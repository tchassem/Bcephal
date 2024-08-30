/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 
 * @author MORISET-004
 *
 */
@Entity(name = "BookingDetail")
@Table(name = "BCP_ACCOUNTING_BOOKING_DETAIL")
@Data
@EqualsAndHashCode(callSuper = false)
public class BookingDetail extends Persistent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7121625581589340712L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posting_ent_seq")
	@SequenceGenerator(name = "posting_ent_seq", sequenceName = "posting_ent_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long bookingId;	
	private Long entryId;	
	private int position;	
	private String accountId;	
	private String accountName;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date date_;	
	private BigDecimal amount;	
	private String sign_;
	
	
	@Override
	public String toString() {
		return accountId;
	}


	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
