/**
 * 
 */
package com.moriset.bcephal.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.grid.domain.JoinLog;
import com.moriset.bcephal.grid.domain.JoinLogBrowserData;
import com.moriset.bcephal.grid.service.JoinLogService;

/**
 * 
 * @author MORISET-004
 *
 */
 
@RestController
@RequestMapping("/join-log")
public class JoinLogController extends BaseController<JoinLog, JoinLogBrowserData> {

	@Autowired
	JoinLogService joinLogService;

	@Override
	protected JoinLogService getService() {
		return joinLogService;
	}
}
