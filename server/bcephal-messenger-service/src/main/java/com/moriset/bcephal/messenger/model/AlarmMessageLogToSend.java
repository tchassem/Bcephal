/**
 * 
 */
package com.moriset.bcephal.messenger.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Moriset
 *
 */
@Entity(name = "AlarmMessageLogToSend")
@Table(name = "BCP_ALARM_MESSAGE_LOG_TO_SEND")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlarmMessageLogToSend extends AlarmMessageLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_message_log_seq")
	@SequenceGenerator(name = "alarm_message_log_seq", sequenceName = "alarm_message_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	public AlarmMessageLogToSend(AlarmMessage message) {
		super(message);
	}
	
	public AlarmMessageLogToSend(AlarmMessageLog message) {
		super(message);
	}
	
	
}
