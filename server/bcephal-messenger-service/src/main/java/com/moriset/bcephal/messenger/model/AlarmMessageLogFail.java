/**
 * 
 */
package com.moriset.bcephal.messenger.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Moriset
 *
 */
@Entity(name = "AlarmMessageLogFail")
@Table(name = "BCP_ALARM_MESSAGE_LOG_FAIL")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlarmMessageLogFail extends AlarmMessageLog {
	
	@Id
	private Long id;
	
	public AlarmMessageLogFail(AlarmMessageLog message) {
		super(message);
	}	
	
}
