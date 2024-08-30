/**
 * 
 */
package com.moriset.bcephal.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joseph Wambo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrowserData {

	private Long id;

	private String name;
	
	private String description;

	private Long subscriptionId;

	private String group;

	private boolean visibleInShortcut;
	
	private boolean confirmAction;

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

	public BrowserData(Long id, String name, String group, boolean visibleInShortcut, Timestamp creationDate,
			Timestamp modificationDate) {
		setId(id);
		setName(name);
		setGroup(group);
		setVisibleInShortcut(visibleInShortcut);
		setCreationDate(creationDate);
		setModificationDate(modificationDate);
	}

	public BrowserData(Long id, String name, boolean visibleInShortcut, Timestamp creationDate,
			Timestamp modificationDate) {
		setId(id);
		setName(name);
		setVisibleInShortcut(visibleInShortcut);
		setCreationDate(creationDate);
		setModificationDate(modificationDate);
	}

	public BrowserData(Long id, String name, Timestamp creationDate, Timestamp modificationDate) {
		setId(id);
		setName(name);
		setCreationDate(creationDate);
		setModificationDate(modificationDate);
	}
	
	public BrowserData(MainObject persistent) {
		this(persistent.getId(), persistent.getName(), persistent.isVisibleInShortcut(), persistent.getCreationDate(), persistent.getModificationDate());
		setGroup(persistent.getGroup() != null ? persistent.getGroup().getName() : null);
		setDescription(persistent.getDescription());
		setConfirmAction(persistent.isConfirmAction());
	}

}
