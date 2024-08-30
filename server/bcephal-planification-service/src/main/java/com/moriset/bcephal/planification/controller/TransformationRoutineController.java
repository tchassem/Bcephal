/**
 * 
 */
package com.moriset.bcephal.planification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineBrowserData;
import com.moriset.bcephal.planification.service.TransformationRoutineService;
import com.moriset.bcephal.service.MainObjectService;

/**
 * @author Moriset
 *
 */
@RestController
@RequestMapping("/planification/routine")
public class TransformationRoutineController extends BaseController<TransformationRoutine, TransformationRoutineBrowserData>{

	@Autowired
	TransformationRoutineService routineService;
	
	@Override
	protected MainObjectService<TransformationRoutine, TransformationRoutineBrowserData> getService() {
		return routineService;
	}

}
