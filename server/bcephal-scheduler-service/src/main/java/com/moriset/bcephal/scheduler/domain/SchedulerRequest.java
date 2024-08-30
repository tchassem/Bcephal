/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class SchedulerRequest {

	private String projectCode;

	private List<Long> objectIds;

    private SchedulerTypes objectType;
    
    
	public SchedulerRequest() {
		this.objectIds = new ArrayList<Long>();
	}
	
}
