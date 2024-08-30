/**
 * 
 */
package com.moriset.bcephal.domain.socket;

import com.moriset.bcephal.utils.ConfirmationRequest;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public abstract class TaskProgressListener {

	private TaskProgressInfo info;
	
	private boolean sendError = true;
	
	public void createInfo(Long id, String name) {
		this.info = new TaskProgressInfo();
		this.info.setId(id);
		this.info.setName(name);
	}

	public void createSubInfo(Long id, String name) {
		this.info.setCurrentSubTask(new TaskProgressInfo());
		this.info.getCurrentSubTask().setId(id);
		this.info.getCurrentSubTask().setName(name);
	}

	public void start(long stepCount) {
		this.info.setStepCount(stepCount);
		this.info.setCurrentStep(0);
		OnChange();
	}

	public void startSubInfo(long stepCount) {
		this.info.getCurrentSubTask().setStepCount(stepCount);
		this.info.getCurrentSubTask().setCurrentStep(0);
		OnChange();
	}

	public void end() {
		this.info.setTaskEnded(true);
		OnChange();
	}

	public void endSubInfo() {
		this.info.getCurrentSubTask().setTaskEnded(true);
		OnChange();
	}

	public void error(String message, boolean ended) {
		if(sendError) {
			this.info.setTaskInError(true);
			this.info.setMessage(message);
			this.info.setTaskEnded(ended);
			OnChange();
		}
	}

	public void nextStep(long step) {
		this.info.setCurrentStep(this.info.getCurrentStep() + step);
		OnChange();
	}

	public void nextSubInfoStep(long step) {
		this.info.getCurrentSubTask().setCurrentStep(this.info.getCurrentSubTask().getCurrentStep() + step);
		OnChange();
	}

	public void OnChange() {
		SendInfo();
	}

	public abstract void SendInfo();
	
	
	public ConfirmationRequest sendConfirmation(ConfirmationRequest request) {		
		return null;
	}
	
	public void sendMessage(Object request) {		
		
	}

}
