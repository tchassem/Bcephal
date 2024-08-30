/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

/**
 * 
 * @author MORISET-004
 *
 */
public class PostingBrowserData extends BrowserData{

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public Date valueDate;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public Date entryDate;
	
	public String username;
	
	public String comment;
	
	public BigDecimal balance;
	
	@Enumerated(EnumType.STRING) 
	public PostingStatus status;
	
	
	public PostingBrowserData(Posting posting) {
		super(posting.getId(), posting.getName(), posting.getCreationDate(), posting.getModificationDate());	
		this.valueDate = posting.getValueDate();
		this.entryDate = posting.getEntryDate();
		this.username = posting.getUsername();
		this.balance = posting.computeBalance();
		this.status = posting.getStatus();
	}
	
}
