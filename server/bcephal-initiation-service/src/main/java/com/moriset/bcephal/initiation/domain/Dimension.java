/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class Dimension extends Persistent {

	private static final long serialVersionUID = 9012046045206985498L;

	private String name;

	private int position;

	private boolean visibleByAdminOnly;
	

	@JsonIgnore
	public abstract String getUniverseTableColumnName();

	@JsonIgnore
	public abstract String getUniverseTableColumnType();

	@Override
	public String toString() {
		return name;
	}

}
