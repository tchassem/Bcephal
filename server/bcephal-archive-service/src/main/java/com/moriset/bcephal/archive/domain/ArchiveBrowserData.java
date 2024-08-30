package com.moriset.bcephal.archive.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArchiveBrowserData extends BrowserData {

	private String userName;

	private long lineCount;
	
	private ArchiveStatus status;

	public ArchiveBrowserData(Archive item) {
		super(item.getId(), item.getName());
		this.setGroup(item.getGroup().getName());
		this.setUserName(item.getUserName());
		this.setStatus(item.getStatus());
		this.setLineCount(item.getLineCount());
	}

}
