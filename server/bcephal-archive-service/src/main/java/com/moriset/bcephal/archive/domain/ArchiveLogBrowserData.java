/**
 * 
 */
package com.moriset.bcephal.archive.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ArchiveLogBrowserData extends BrowserData {
	
	private String userName;

	private String message;

	private long lineCount;

	private ArchiveLogStatus status;

	private ArchiveLogAction action;

	public ArchiveLogBrowserData(ArchiveLog item) {
		super(item.getId(), item.getName());
		this.message = item.getMessage();
		this.userName = item.getUserName();
		this.action = item.getAction();
		this.status = item.getStatus();
		this.lineCount = item.getLineCount();
	}
}
