/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author MORISET-004
 *
 */

@Entity(name = "PostingEntry")
@Table(name = "BCP_ACCOUNTING_POSTING_ENTRY")
@Data
@EqualsAndHashCode(callSuper = false)

public class PostingEntry extends Persistent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4629946820510457050L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posting_ent__seq")
	@SequenceGenerator(name = "posting_ent__seq", sequenceName = "posting_ent__seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posting")
	private Posting posting;
	
	private String accountId;
	
	private String accountName;
	
	@Column(name="comment_")
	private String comment;
	
	private String username;
	
	private BigDecimal amount;
	
	private int position;
	
	@Enumerated(EnumType.STRING) 
	@Column(name="sign_")
	private PostingSign sign;
	
	@Enumerated(EnumType.STRING) 
	private PostingStatus status;
	

	@JsonIgnore
	public BigDecimal getSignedAmount() {
		BigDecimal value = getAmount();
		if(getSign() != null && getSign().isDebit()) {
			value = value.negate();
		}
		return value;
	}


	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
