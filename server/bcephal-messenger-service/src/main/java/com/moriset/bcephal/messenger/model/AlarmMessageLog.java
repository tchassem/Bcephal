/**
 * 
 */
package com.moriset.bcephal.messenger.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AlarmMessageLog {

//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_message_log_seq")
//	@SequenceGenerator(name = "alarm_message_log_seq", sequenceName = "alarm_message_log_seq", initialValue = 1,  allocationSize = 1)
//	private Long id;

	private String reference1;
	private String reference2;
	private String category;
	private String clientCode;

	@Column(name = "automaticmailmodelname")
	private String automaticMailModelName;

	private String projectCode;

	private String username;

	private String messageType;

	private String title;

	private String content;

	@Column(name = "cccontacts")
	private String ccContacts;

	@Transient
	private List<String> contacts;

	@JsonIgnore
	private String contactsImpl;

	@Transient
	private List<String> filesId;

	@JsonIgnore
	private String filesImpl;

	@Enumerated(EnumType.STRING)
	private AlarmMessageStatus status;

	private String errorMessage;

	private Integer errorCode;

	private Long maxSendCount;

	private Long sendCount;

	private String mode;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp firstSendDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp lastSendDate;

	private String headers;

	public AlarmMessageLog() {
		this.contacts = new ArrayList<>(0);
		this.filesId = new ArrayList<>(0);
		this.creationDate = new Timestamp(System.currentTimeMillis());
	}

	public AlarmMessageLog(AlarmMessage message) {
		this();
		this.projectCode = message.getProjectCode();
		this.username = message.getUsername();
		this.messageType = message.getMessageType();
		this.title = message.getTitle();
		this.content = message.getContent();
		this.contacts = message.getContacts();
		this.contactsImpl = getContactsAsString();
		this.filesId = message.getFilesId();
		this.filesImpl = getFilesAsString();
		this.status = AlarmMessageStatus.NEW;
		this.maxSendCount = message.getMaxSendCount();
		this.sendCount = 0L;
		this.mode = message.getMode();
		this.reference1 = message.getReference1();
		this.reference2 = message.getReference2();
		this.category = message.getCategory();
		this.automaticMailModelName = message.getAutomaticMailModelName();
		this.clientCode = message.getClientCode();
		this.ccContacts = message.getCcContacts();
	}

	public AlarmMessageLog(AlarmMessageLog message) {
		this();
		this.projectCode = message.getProjectCode();
		this.username = message.getUsername();
		this.messageType = message.getMessageType();
		this.title = message.getTitle();
		this.content = message.getContent();
		this.contacts = message.getContacts();
		this.contactsImpl = getContactsAsString();
		this.filesId = message.getFilesId();
		this.filesImpl = getFilesAsString();
		this.status = message.getStatus();
		this.maxSendCount = message.getMaxSendCount();
		this.sendCount = message.getSendCount();
		this.setId(message.getId());
		this.creationDate = message.getCreationDate();
		this.firstSendDate = message.getFirstSendDate();
		this.lastSendDate = message.getLastSendDate();
		this.errorMessage = message.getErrorMessage();
		this.errorCode = message.getErrorCode();
		this.mode = message.getMode();
		this.reference1 = message.getReference1();
		this.reference2 = message.getReference2();
		this.category = message.getCategory();
		this.automaticMailModelName = message.getAutomaticMailModelName();
		this.clientCode = message.getClientCode();
		this.ccContacts = message.getCcContacts();
	}

	@JsonIgnore
	public boolean canBeSend() {
		return getMaxSendCount() == null || getMaxSendCount() > getSendCount();
	}

	@JsonIgnore
	public boolean isMail() {
		return AlarmMessage.MAIL.equalsIgnoreCase(messageType);
	}

	@JsonIgnore
	public boolean isSms() {
		return AlarmMessage.SMS.equalsIgnoreCase(messageType);
	}

	@JsonIgnore
	public String getContactsAsString() {
		String result = "";
		String coma = "";
		for (String contact : contacts) {
			result += coma + contact;
			coma = ";";
		}
		return result;
	}

	@JsonIgnore
	public String getFilesAsString() {
		String result = "";
		String coma = "";
		if(filesId != null) {
			for (String file : filesId) {
				result += coma + file;
				coma = ";";
			}
		}
		return result;
	}

	@PostLoad
	public void initContacts() {
		this.contacts = new ArrayList<String>();
		if (StringUtils.hasText(contactsImpl)) {
			for (String contact : contactsImpl.split(";")) {
				contacts.add(contact);
			}
		}

		this.filesId = new ArrayList<String>();
		if (StringUtils.hasText(filesImpl)) {
			for (String file : filesImpl.split(";")) {
				filesId.add(file);
			}
		}
	}

	@PreUpdate
	public void initContactsImpl() {
		this.contactsImpl = getContactsAsString();
		this.filesImpl = getFilesAsString();
	}

	public void updateSendDate() {
		this.sendCount = this.sendCount + 1;
		this.lastSendDate = new Timestamp(System.currentTimeMillis());
		if (this.firstSendDate == null) {
			this.firstSendDate = this.lastSendDate;
		}
		if (this.status == AlarmMessageStatus.NEW) {
			this.status = AlarmMessageStatus.IN_PROCESS;
		}
	}

	public Address[] getAdresseReceipt() {
		List<Address> adresses = new ArrayList<>();
		if (contacts != null) {
			contacts.forEach(addresse -> {
				try {
					adresses.add(new InternetAddress(addresse));
				} catch (AddressException e) {
					e.printStackTrace();
				}
			});
		}
		return adresses.toArray(new Address[adresses.size()]);
	}
	
	public abstract Long getId();

	public abstract void setId(Long id);

}
