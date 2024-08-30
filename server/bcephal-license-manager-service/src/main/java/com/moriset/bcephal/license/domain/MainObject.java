/**
 * 
 */
package com.moriset.bcephal.license.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class MainObject extends Persistent {

	private static final long serialVersionUID = 8618687456823021763L;

	private String name;
	
	private String description;
	
	@Transient
	private int documentCount;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp modificationDate;

	public MainObject() {
		creationDate = new Timestamp(System.currentTimeMillis());
		modificationDate = new Timestamp(System.currentTimeMillis());
	}

	@Override
	public String toString() {
		return name;
	}

}
