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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.ListChangeHandler;
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
@Entity(name = "Booking")
@Table(name = "BCP_ACCOUNTING_BOOKING")
@Data
@EqualsAndHashCode(callSuper = false)
public class Booking extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6794993647590604036L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
	@SequenceGenerator(name = "booking_seq", sequenceName = "booking_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String bookingId;
	
	private String runNumber;
		
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date entryDate;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date bookingDate1;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date bookingDate2;
	
	private BigDecimal creditAmount;
	
	private BigDecimal debitAmount;
	
	private String username;
	
	private boolean manual;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "booking")
	private List<BookingItem> items;

	@Transient
	private ListChangeHandler<BookingItem> itemListChangeHandler;
	
	@JsonIgnore @Transient
	private List<BookingDetail> details;
	
	@JsonIgnore @Transient
	public BookingModelLogItem logItem;
	
	public Booking() {
		super();
		items = new ArrayList<BookingItem>(0);
		itemListChangeHandler = new ListChangeHandler<BookingItem>();
		details = new ArrayList<BookingDetail>(0);
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<BookingItem> items) {
		this.items = items;
		this.itemListChangeHandler.setOriginalList(items);
	}

	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemListChangeHandler.setOriginalList(items);
	}


	public BookingItem getItem(String accountId, String pivotValues) {
		for(BookingItem item : getItemListChangeHandler().getItems()) {
			if(item.getAccountId().equals(accountId) && item.getPivot().equals(pivotValues)) {
				return item;
			}
		}
		return null;
	}


	@JsonIgnore
	public BigDecimal getBalance() {
		BigDecimal balance = BigDecimal.ZERO;
		for(BookingItem item : getItemListChangeHandler().getItems()) {
			balance = balance.add(item.getAmount());
		}
		return balance;
	}
	
	@JsonIgnore
	public boolean validateBalance(BigDecimal minDelta, BigDecimal maxDelta) {
		BigDecimal balance = getBalance();
		return balance == BigDecimal.ZERO 
				|| BigDecimal.ZERO.equals(balance) 
				|| BigDecimal.ZERO.compareTo(balance) == 0
				|| (balance.compareTo(minDelta) >= 0 && balance.compareTo(maxDelta) <= 0);
				//|| balance.abs().compareTo(new BigDecimal(0.00019)) <= 0;
	}

	@JsonIgnore
	public List<Long> getPostingEntryOids() {
		List<Long> oids = new ArrayList<Long>(0);
		for(BookingItem item : getItemListChangeHandler().getItems()) {
			oids.addAll(item.postingEntryIds);
		}
		return oids;
	}
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
