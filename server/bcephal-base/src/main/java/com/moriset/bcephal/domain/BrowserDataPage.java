/**
 * 
 */
package com.moriset.bcephal.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
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
