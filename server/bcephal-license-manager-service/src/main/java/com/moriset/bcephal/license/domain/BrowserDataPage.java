/**
 * 
 */
package com.moriset.bcephal.license.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BrowserDataPage<B> {

	private int pageSize;

	private int pageFirstItem;

	private int pageLastItem;

	private int totalItemCount;

	private int pageCount;

	private int currentPage;

	private List<B> items;

	public BrowserDataPage() {
		items = new ArrayList<B>(0);
	}

}
