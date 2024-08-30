/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

import com.moriset.bcephal.domain.BrowserData;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineBrowserData extends BrowserData {
	
	public TransformationRoutineBrowserData(TransformationRoutine persistent) {
		super(persistent);
	}
	
}
