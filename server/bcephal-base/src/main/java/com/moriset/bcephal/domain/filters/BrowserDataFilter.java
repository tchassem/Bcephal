/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
@AllArgsConstructor
@Builder
public class BrowserDataFilter {

	private static int DEFAULT_PAGE_SIZE = 25;
	
	public static final String  SortBy = "SortBy";
	public static final String  SortByAsc = "Asc";
	public static final String  SortByDesc = "Desc";
	
	private DataSourceType dataSourceType;
	
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

	private Boolean published;
	
	private boolean myTasks;
	
	private Boolean forModel;
	
	private ReconciliationDataFilter recoData;
	
	@JsonIgnore
	private String username;
	
	@JsonIgnore
	private boolean orderById;

	public BrowserDataFilter() {
		this.pageSize = DEFAULT_PAGE_SIZE;
		// this.Items = new List<BrowserDataFilterItem>(0);
		this.orderAsc = true;
		this.showAll = false;
		this.allowRowCounting = true;
		this.dataSourceType = DataSourceType.UNIVERSE;

	}

}
