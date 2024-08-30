/**
 * 
 */
package com.moriset.bcephal.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class SchedulableObject extends MainObject {

	private static final long serialVersionUID = 6926593648917474853L;

	private boolean active;
	
	private boolean scheduled;

	private String cronExpression;
	
	public SchedulableObject() {
		this.active = true;
	}
	
}
