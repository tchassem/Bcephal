package com.moriset.bcephal.domain;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.filters.PeriodGranularity;

import lombok.Data;

@Data
public class FileGroupByItem {

	public static final String ITEM_SEPARATOR = ";";
	private static final String SUB_ITEM_SEPARATOR = "-";
	
	private Long columnId;
	private PeriodGranularity dateGranularity;
	
	public String asText() {
		if(columnId != null) {
			String text = "" + columnId;
			if(dateGranularity != null) {
				text += SUB_ITEM_SEPARATOR + dateGranularity.name();
			}
			return text;
		}
		return null;
	}
	
	public static FileGroupByItem FromText(String text) {
		if(StringUtils.hasText(text)) {
			String[] vars = text.split(SUB_ITEM_SEPARATOR);
			FileGroupByItem item = new FileGroupByItem();
			if(vars[0] != null) {
				item.setColumnId(Long.valueOf(vars[0]));
			}
			if(vars.length > 1 && vars[1] != null) {
				item.setDateGranularity(PeriodGranularity.valueOf(vars[1]));
			}			
			return item;
		}
		return null;
	}
	
}
