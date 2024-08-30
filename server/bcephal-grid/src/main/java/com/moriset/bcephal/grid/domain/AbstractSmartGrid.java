package com.moriset.bcephal.grid.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractSmartGrid<C extends AbstractSmartGridColumn> extends Persistent {

	private static final long serialVersionUID = 4133089772869678897L;

	private String name;
	
	private Boolean published;
	
	@Transient
	private JoinGridType gridType;
		
	@JsonIgnore
	public abstract String getDbTableName();
	
	public abstract List<C> getColumns();
	
	public Boolean getPublished() {
		if(published == null) {
			published = false;
		}
		return isPublished();
	}
	
	public boolean isPublished() {
		if(published == null) {
			published = false;
		}
		return published;
	}
	
	@PostLoad
	public void initListChangeHandler() {
		getColumns().forEach( item -> { });
	}
	
	public C getColumnById(Long id) {
		for (C column : this.getColumns()) {
			if (column.getId() != null && column.getId().equals(id)) {				
				return column;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}
	
	@Override
	public Persistent copy() {
		return null;
	}
	
}
