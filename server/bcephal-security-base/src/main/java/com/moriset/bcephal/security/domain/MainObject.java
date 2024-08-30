/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class MainObject extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1423434337707642556L;

	@NotNull(message = "{client.name.validation.null.message}") 
	@Size(min = 0, max = 100, message = "{client.name.validation.size.message}")	
	private String name;

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
	}

	@Override
	public String toString() {
		return name;
	}

}
