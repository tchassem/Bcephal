/**
 * 
 */
package com.moriset.bcephal.integration.scheduler;

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

    private Long clientId;
    
    
	public SchedulerRequest() {
		this.objectIds = new ArrayList<Long>();
	}
	
}
