/**
 * 
 */
package com.moriset.bcephal.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class MainObject extends Persistent {

	private static final long serialVersionUID = 8618687456823021763L;

	private String name;
	
	private String description;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "bgroup")
	private BGroup group;

	private boolean visibleInShortcut;
	
	@Transient
	private int documentCount;

	/**
	 * <p style="margin-top: 0">
	 * Creation date time.
	 * </p>
	 */
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	/**
	 * <p style="margin-top: 0">
	 * Last modification date time
	 * </p>
	 */
	// @Version
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp modificationDate;

	/**
	 * Default constructor.
	 */
	public MainObject() {
		creationDate = new Timestamp(System.currentTimeMillis());
		modificationDate = new Timestamp(System.currentTimeMillis());
		visibleInShortcut = true;
	}
	
	public boolean isConfirmAction() {
		return true;
	}

	@Override
	public String toString() {
		return name;
	}


	

}
