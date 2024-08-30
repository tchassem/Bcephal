package com.moriset.bcephal.alarm.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.SchedulableObject;
import com.moriset.bcephal.domain.condition.ConditionalExpression;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "Alarm")
@Table(name = "BCP_ALARM")
@Data
@EqualsAndHashCode(callSuper = false)
public class Alarm extends SchedulableObject {

	private static final long serialVersionUID = -8745332749191517469L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_seq")
	@SequenceGenerator(name = "alarm_seq", sequenceName = "alarm_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private AlarmCategory category;
	
	@Enumerated(EnumType.STRING)
	private AlarmInvoiceConsolidation invoiceConsolidation;
	
	@Enumerated(EnumType.STRING)
	private AlarmInvoiceTrigger invoiceTrigger;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne 
	@JoinColumn(name = "filter")
	private UniverseFilter filter;
		
    private boolean sendEmail;
    
	private String emailTitle;
	
    private String email;

    public boolean sendSms;
	
    public String sms;

    private boolean sendChat;
	
    private String chat;

    @ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
    @JoinColumn(name = "condition")
    private ConditionalExpression condition;
	
    
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "alarm")
	private List<AlarmAudience> audiences = new ArrayList<AlarmAudience>();
	
	@Transient
    private ListChangeHandler<AlarmAudience> audienceListChangeHandler;
	
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "alarm")
	private List<AlarmAttachment> attachments = new ArrayList<AlarmAttachment>();
	
	@Transient
    private ListChangeHandler<AlarmAttachment> attachmentListChangeHandler;
    
    
    public Alarm() {
    	this.audienceListChangeHandler = new ListChangeHandler<>();
    	this.attachmentListChangeHandler = new ListChangeHandler<>();
    	setCategory(AlarmCategory.FREE);
    	setInvoiceConsolidation(AlarmInvoiceConsolidation.ONE_MAIL_PER_CLIENT);
    	setInvoiceTrigger(AlarmInvoiceTrigger.INVOICE_VALIDATED_AND_NOT_YET_SENT);
    	setSendEmail(true);
    	setSendChat(false);
    	setSendSms(false);
		setVisibleInShortcut(true);
		setActive(true);
	}

	/**
	 * @param audiences the audiences to set
	 */
	public void setAudiences(List<AlarmAudience> audiences) {
		this.audiences = audiences;
		this.audienceListChangeHandler.setOriginalList(this.audiences);
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(List<AlarmAttachment> attachments) {
		this.attachments = attachments;
		this.attachmentListChangeHandler.setOriginalList(this.attachments);
	}

	@PostLoad
	public void initListChangeHandler() {
		audiences.size();
		attachments.size();
		this.audienceListChangeHandler.setOriginalList(this.audiences);
		this.attachmentListChangeHandler.setOriginalList(this.attachments);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@JsonIgnore
	public Alarm copy() {		
		return null;
	}
    
    
}
