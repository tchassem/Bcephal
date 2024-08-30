package com.moriset.bcephal.messenger.model;



import lombok.Data;

@Data
public class BrowserDataFilter {

	private static int DEFAULT_PAGE_SIZE = 25;
	
	public static final String  SortBy = "SortBy";
	public static final String  SortByAsc = "Asc";
	public static final String  SortByDesc = "Desc";

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

	// private List<BrowserDataFilterItem> items;

	private ColumnFilter ColumnFilters;

	private String reportType;

	private Boolean published;
	
	private AlarmMessageStatus status;
	
	private String type;
	
	private String mode;


	public BrowserDataFilter() {
		this.pageSize = DEFAULT_PAGE_SIZE;
		// this.Items = new List<BrowserDataFilterItem>(0);
		this.orderAsc = true;
		this.showAll = false;
		this.allowRowCounting = true;

	}
	
}
