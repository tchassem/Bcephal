/**
 * 
 */
package com.moriset.bcephal.license.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class BrowserDataFilter {

	private static int DEFAULT_PAGE_SIZE = 25;
	
	public static final String  SortBy = "SortBy";
	public static final String  SortByAsc = "Asc";
	public static final String  SortByDesc = "Desc";
		
	private Long dataSourceId;
	
	private Long subjectId;
	private String subjectType;

	private int page;

	private int pageSize;

	private Long groupId;

	private Long userId;

	private String criteria;

	private boolean orderAsc;

	private boolean showAll;

	private boolean allowRowCounting;

	private Long clientId;
	
	private Long profileId;
	
	private boolean selectAllRows;

	private ColumnFilter ColumnFilters;

	private String reportType;

	
	@JsonIgnore
	private String username;

	public BrowserDataFilter() {
		this.pageSize = DEFAULT_PAGE_SIZE;
		this.orderAsc = true;
		this.showAll = false;
		this.allowRowCounting = true;
	}

}
