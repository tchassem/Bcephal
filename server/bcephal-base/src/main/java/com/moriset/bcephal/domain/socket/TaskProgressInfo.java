/**
 * 
 */
package com.moriset.bcephal.domain.socket;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class TaskProgressInfo {

	private Long id;

	private String name;

	private boolean taskEnded;

	private boolean taskInError;

	private String message;

	private long stepCount;

	private long currentStep;

	private TaskProgressInfo currentSubTask;	
	
}
