/**
 * 
 */
package com.moriset.bcephal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Persistent
 *
 * @author B-Cephal Team
 * @date 15 avr. 2013
 *
 */
@SuppressWarnings("serial")
@jakarta.persistence.MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Persistent implements IPersistent {
	
	/**
	 * <p style="margin-top: 0">
	 * Unique identifier <br>
	 * <br>
	 * 
	 * @return the identifier
	 *         </p>
	 */
	public abstract Long getId();

	/**
	 * <p style="margin-top: 0">
	 * Set identifier to val <br>
	 * <br>
	 * 
	 * @param val new identifier
	 *            </p>
	 */
	public abstract void setId(Long val);

	public abstract Persistent copy();

	@JsonIgnore
	public boolean isPersistent() {
		return getId() != null;
	}
	
	@Override
	public String toString() {
		return "ID = " + getId();
	}

}
