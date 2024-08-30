/**
 * 
 */
package com.moriset.bcephal.license.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@SuppressWarnings("serial")
@jakarta.persistence.MappedSuperclass
@Data
public abstract class Persistent implements IPersistent {

	@Override
	public abstract Long getId();

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
