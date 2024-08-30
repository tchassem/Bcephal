/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity(name = "BookingItem")
@Table(name = "BCP_ACCOUNTING_BOOKING_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class BookingItem extends Persistent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7049397624454666698L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_item_seq")
	@SequenceGenerator(name = "booking_item_seq", sequenceName = "booking_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking")
	private Booking booking;
	
	private String accountId;
	
	private String accountName;
	
	private int position;
	
	private BigDecimal amount;
	
	private BigDecimal creditAmount;
	
	private BigDecimal debitAmount;
		
	private String pivot;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date bookingDate1;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date bookingDate2;
		
	@Transient @JsonIgnore
	public List<Long> postingEntryIds;
	
	
	public BookingItem() {
		super();
		postingEntryIds = new ArrayList<Long>(0);
	}
	
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}	
}
