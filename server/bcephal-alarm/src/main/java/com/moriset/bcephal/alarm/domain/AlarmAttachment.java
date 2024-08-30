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
@Entity(name = "AlarmAttachment")
@Table(name = "BCP_ALARM_ATTACHMENT")
@Data
@EqualsAndHashCode(callSuper = false)
public class AlarmAttachment extends Persistent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8749214780814920673L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_atch_seq")
	@SequenceGenerator(name = "alarm_atch_seq", sequenceName = "alarm_atch_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alarm")
	private Alarm alarm;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private AlarmAttachmentType attachmentType;
	
	private int position;
	
	private Long templateId;
	
	
	@Override
	public Persistent copy() {
		return null;
	}
	
	
}
