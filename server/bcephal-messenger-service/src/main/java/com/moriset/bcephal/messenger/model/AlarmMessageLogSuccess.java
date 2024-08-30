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
@Entity(name = "AlarmMessageLogSuccess")
@Table(name = "BCP_ALARM_MESSAGE_LOG_SUCCESS")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlarmMessageLogSuccess extends AlarmMessageLog {
	
	@Id
	private Long id;
	
	public AlarmMessageLogSuccess(AlarmMessageLog message) {
		super(message);
	}	
	
}
