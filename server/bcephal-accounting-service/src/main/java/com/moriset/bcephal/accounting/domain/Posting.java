/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */

@Entity(name = "Posting")
@Table(name = "BCP_ACCOUNTING_POSTING")
@Data
@EqualsAndHashCode(callSuper = false)
public class Posting extends MainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4730176480313414552L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posting_seq")
	@SequenceGenerator(name = "posting_seq", sequenceName = "posting_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date valueDate;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date entryDate;
	
	@Column(name="comment_")
	private String comment;
	
	private String username;
	
	private BigDecimal balance;
	
	@Enumerated(EnumType.STRING) 
	private PostingStatus status;
		
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "posting")
	private List<PostingEntry> entries;

	@Transient
	private ListChangeHandler<PostingEntry> entryListChangeHandler;
	
	public Posting() {
		entries = new ArrayList<PostingEntry>(0);
		entryListChangeHandler = new ListChangeHandler<PostingEntry>();
		status = PostingStatus.DRAFT;
	}



	/**
	 * @param entries the entries to set
	 */
	public void setEntries(List<PostingEntry> entries) {
		this.entries = entries;
		this.entryListChangeHandler.setOriginalList(entries);
	}


	@PostLoad
	public void initListChangeHandler() {
		entries.size();
		this.entryListChangeHandler.setOriginalList(entries);
	}
	
	@JsonIgnore
	public BigDecimal computeBalance() {
		BigDecimal balance = BigDecimal.ZERO;;
		for(PostingEntry entry : this.getEntryListChangeHandler().getItems()) {
			balance = balance.add(entry.getSignedAmount());
		}
		return balance;
	}



	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
