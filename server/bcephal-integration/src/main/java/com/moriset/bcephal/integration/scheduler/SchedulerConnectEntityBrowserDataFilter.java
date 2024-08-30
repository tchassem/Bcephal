/**
 * 
 */
package com.moriset.bcephal.integration.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.filters.BrowserDataFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerConnectEntityBrowserDataFilter extends BrowserDataFilter {

	private String projectCode;

	private List<Long> objectIds;

	public SchedulerConnectEntityBrowserDataFilter() {
		this.objectIds = new ArrayList<Long>();
	}
}
