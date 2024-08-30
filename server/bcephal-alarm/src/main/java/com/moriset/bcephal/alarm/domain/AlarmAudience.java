/**
 * 
 */
package com.moriset.bcephal.alarm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "AlarmAudience")
@Table(name = "BCP_ALARM_AUDIENCE")
@Data
@EqualsAndHashCode(callSuper = false)
public class AlarmAudience extends Persistent {

	private static final long serialVersionUID = -3228934629560509283L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_aud_seq")
	@SequenceGenerator(name = "alarm_aud_seq", sequenceName = "alarm_aud_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alarm")
	private Alarm alarm;
	
	private int position;

	private boolean active;
	
	@Enumerated(EnumType.STRING)
	private AlarmAudienceType audienceType;
	
	@Enumerated(EnumType.STRING)
	private EmailType emailType;

	private Long userOrProfilId;
	
	private String name;
	
	private String email;
	
	private String phone;
	
    private boolean sendEmail;
	
    public boolean sendSms;
	
    private boolean sendChat;
		
	
	public AlarmAudience() {
		setActive(true);
		setEmailType(EmailType.TO);
	}

	
	@JsonIgnore
	public AlarmAudience copy() {
		return null;
	}

	
}
