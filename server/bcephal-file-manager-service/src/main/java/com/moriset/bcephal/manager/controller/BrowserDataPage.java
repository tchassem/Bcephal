package com.moriset.bcephal.manager.controller;

import java.util.ArrayList;
import java.util.List;

public class BrowserDataPage<B> {

	public long pageSize;
	public long totalItems;
	public long page;
	public List<BrowserData> items;
	
	public BrowserDataPage() {
		items = new ArrayList<BrowserData>();
	}
	
	public BrowserDataPage(List<BrowserData> items) {
		this.items = items;
	}
	
}
