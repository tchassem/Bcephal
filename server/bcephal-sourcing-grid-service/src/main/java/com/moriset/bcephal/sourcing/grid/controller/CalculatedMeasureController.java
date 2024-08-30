package com.moriset.bcephal.sourcing.grid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureBrowserData;
import com.moriset.bcephal.grid.service.CalculatedMeasureService;

@RestController
@RequestMapping("/sourcing/calculated-measure")
public class CalculatedMeasureController extends BaseController<CalculatedMeasure, CalculatedMeasureBrowserData>{
	
	@Autowired
	CalculatedMeasureService calculatedMeasureService;
	
	@Override
	protected CalculatedMeasureService getService() {
		return calculatedMeasureService;
	}

}
