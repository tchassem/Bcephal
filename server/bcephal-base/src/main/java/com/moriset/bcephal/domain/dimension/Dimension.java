/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.IPersistent;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Data
@AllArgsConstructor
public abstract class Dimension implements IPersistent {

	@Transient
	private DataSourceType dataSourceType;
	
	@Transient
	private Long dataSourceId;

	@ToString.Include
	@EqualsAndHashCode.Include
	private String name;

	private int position;

//	@JsonIgnore
//	@ManyToOne
//	private Long parent;

	private boolean visibleByAdminOnly;

	public abstract String getFieldId();

	public abstract String getParentId();
	
	public abstract Dimension getParent();
	
	//public abstract void setParent(Dimension parent);
	
	@JsonIgnore
	public String getUniverseTableColumnName(boolean published) {
		if(published) {
			return "COLUMN" + getId();
		}
		return getUniverseTableColumnName();
	}

	@JsonIgnore
	public abstract String getUniverseTableColumnName();

	@JsonIgnore
	public abstract String getUniverseTableColumnType();
	
	public Dimension() {
		this.dataSourceType = DataSourceType.UNIVERSE;
	}
	
	public DataSourceType getDataSourceType() {
		if(dataSourceType == null) {
			this.dataSourceType = DataSourceType.UNIVERSE;
		}
		return dataSourceType;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
