/**
 * 
 */
package com.moriset.bcephal.reporting.service;

import org.springframework.stereotype.Service;

import com.moriset.bcephal.sheet.domain.SpreadSheet;
import com.moriset.bcephal.sheet.domain.SpreadSheetType;
import com.moriset.bcephal.sheet.service.SpreadSheetService;

/**
 * @author Joseph Wambo
 *
 */
@Service
public class ReportSpreadSheetService extends SpreadSheetService {

	@Override
	protected SpreadSheetType getSpreadSheetType() {
		return SpreadSheetType.REPORT;
	}

	@Override
	protected SpreadSheet getNewItem() {
		return getNewItem("Report spreadsheet ");
	}

}
