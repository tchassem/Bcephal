/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

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
public class SchedulerBrowserDataFilter extends BrowserDataFilter {

	private String projectCode;

	private List<Long> objectIds;

    private SchedulerTypes objectType;
    
    
	public SchedulerBrowserDataFilter() {
		this.objectIds = new ArrayList<Long>();
	}
}
