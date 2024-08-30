/**
 * 
 */
package com.moriset.bcephal.messenger.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.messenger.services.DimensionType;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class ColumnFilter {

	private String name;

	private String link;

	private String value;

	private String operation;

	private List<ColumnFilter> items;

	private boolean grouped;

	private DimensionType dimensionType;

	private Long dimensionId;

	@JsonIgnore
	private Class<?> Type;
	
	public ColumnFilter() {
		this.items = new ArrayList<ColumnFilter>(0);
		this.grouped = false;
	}
	
	@JsonIgnore
	public boolean isSortFilter() {
		return StringUtils.hasText(getOperation()) &&  getOperation().equals(BrowserDataFilter.SortBy);
	}

}
