package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

public class GridItem{
	public Object[] datas;
	
	public GridItem(Object[] datas) {
		this.datas = datas;
	}
	
	public static List<GridItem> buildItems(List<Object[]> objects) {
		List<GridItem> items = new ArrayList<>();
		if(objects == null || objects.size() == 0) {
			return items;
		}
		for(Object[] item : objects) {
			items.add(new GridItem(item));
		}
		return items;
	}
}
