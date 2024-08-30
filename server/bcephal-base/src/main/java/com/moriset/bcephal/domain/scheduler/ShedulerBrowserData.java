/**
 * 
 */
package com.moriset.bcephal.domain.scheduler;

import java.sql.Timestamp;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author MORISET-004
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ShedulerBrowserData extends BrowserData {

	private String projectName;
	private String projectCode;
	private boolean currentlyExecuting;
	private String currentExecutionFirstTime;
	private String nextFireTime;

	public ShedulerBrowserData(String projectCode, String projectName, boolean currentlyExecuting,
			String currentExecutionFirstTime, String nextFireTime, long itemId, String itemName,
			Timestamp itemCreationDate, Timestamp itemModificationDate) {
		super(itemId, itemName, itemCreationDate, itemModificationDate);
		this.projectName = projectName;
		this.projectCode = projectCode;
		this.currentlyExecuting = currentlyExecuting;
		this.currentExecutionFirstTime = currentExecutionFirstTime;
		this.nextFireTime = nextFireTime;
	}
}
