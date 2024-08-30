/**
 * 
 */
package com.moriset.bcephal.license.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrowserData {

	private Long id;

	private String name;
	
	private String description;

	private Long subscriptionId;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp modificationDate;

	@Override
	public String toString() {
		return name;
	}

	public BrowserData(Long id, String name) {
		setId(id);
		setName(name);
	}

	public BrowserData(Long id, String name, Timestamp creationDate, Timestamp modificationDate) {
		setId(id);
		setName(name);
		setCreationDate(creationDate);
		setModificationDate(modificationDate);
	}
	
	public BrowserData(MainObject persistent) {
		this(persistent.getId(), persistent.getName(), persistent.getCreationDate(), persistent.getModificationDate());
		setDescription(persistent.getDescription());
	}

}
