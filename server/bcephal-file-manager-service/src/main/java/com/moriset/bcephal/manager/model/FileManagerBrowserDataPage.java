package com.moriset.bcephal.manager.model;

import java.util.ArrayList;
import java.util.List;

public class FileManagerBrowserDataPage<B> {

	public long pageSize;
	public long totalItems;
	public long page;
	public List<FileManagerBrowserData> items;
	
	public FileManagerBrowserDataPage() {
		items = new ArrayList<FileManagerBrowserData>();
	}
	
	public FileManagerBrowserDataPage(List<FileManagerBrowserData> items) {
		this.items = items;
	}
	
}
