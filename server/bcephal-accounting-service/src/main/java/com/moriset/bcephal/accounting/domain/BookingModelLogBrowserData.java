/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.accounting.domain.BookingModelLog.BookingModelLogStatus;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author MORISET-004
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BookingModelLogBrowserData extends BrowserData {
	
	private Long modelId;
	
	private BookingModelLogStatus status;
	
	private RunModes mode;
		
	private String user;
			
	private Long accountCount;
	
	private Long postingEntryCount;
	
	private Long periodCount;	
	
	private Long bookingItemCount;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	public Timestamp endDate;
	
	
	
	public BookingModelLogBrowserData(BookingModelLog log) {
		super(log.getId(),log.getModelName());
		this.modelId = log.getModelId();		
		this.postingEntryCount = log.getPostingEntryCount();	
		endDate = log.getEndDate();		
		status = log.getStatus();		
		mode = log.getMode();			
		user = log.getUser();		
		periodCount = log.getPeriodCount();
		bookingItemCount = log.getBookingItemCount();
		accountCount = log.getAccountCount();
	}
	
}
