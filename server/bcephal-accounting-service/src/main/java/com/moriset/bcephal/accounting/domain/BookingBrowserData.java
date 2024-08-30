/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

/**
 * @author Joseph Wambo
 *
 */
public class BookingBrowserData extends BrowserData {

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public Date entryDate;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public Date bookingDate1;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public Date bookingDate2;
	
	public BigDecimal creditAmount;
	
	public BigDecimal debitAmount;
	
	public String username;
	
	public boolean manual;
	
	public BookingBrowserData() {
		this.creditAmount = BigDecimal.ZERO;
		this.debitAmount = BigDecimal.ZERO;
	}
		
	public BookingBrowserData(Booking booking) {
		super(booking.getId(), booking.getBookingId());	
		this.entryDate = booking.getEntryDate();
		this.bookingDate1 = booking.getBookingDate1();
		this.bookingDate2 = booking.getBookingDate2();
		this.creditAmount = booking.getCreditAmount();
		this.debitAmount = booking.getDebitAmount();
		this.username = booking.getUsername();
		this.manual = booking.isManual();
	}
	
	
}
