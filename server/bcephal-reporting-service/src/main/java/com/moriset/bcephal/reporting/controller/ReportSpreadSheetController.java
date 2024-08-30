/**
 * 
 */
package com.moriset.bcephal.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.reporting.service.ReportSpreadSheetService;
import com.moriset.bcephal.sheet.domain.SpreadSheet;
import com.moriset.bcephal.sheet.domain.SpreadSheetBrowserData;
import com.moriset.bcephal.sheet.domain.SpreadSheetCell;
import com.moriset.bcephal.sheet.service.SpreadSheetManager;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/reporting/sheet")
@Slf4j
public class ReportSpreadSheetController extends BaseController<SpreadSheet, SpreadSheetBrowserData> {

	@Autowired
	ReportSpreadSheetService reportSpreadSheetService;

	@Override
	protected ReportSpreadSheetService getService() {
		return reportSpreadSheetService;
	}

	@PostMapping("/save-spreadsheet")
	public ResponseEntity<?> save(@RequestBody SpreadSheetManager manager,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of save SpreadSheet : {}", manager.getSpreadSheet().getName());
		try {
			SpreadSheet sheet = getService().save(manager, locale);
			sheet = getService().getById((Long) sheet.getId());
			log.debug("SpreadSheet saved : {}", sheet.getId());
			return ResponseEntity.status(HttpStatus.OK).body(sheet);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save SpreadSheet : {}", manager.getSpreadSheet().getName(), e);
			String message = messageSource.getMessage("unable.to.save.spreadsheet",
					new Object[] { manager.getSpreadSheet().getName() }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/cell/{spreadsheetId}/{sheet}/{col}/{row}")
	public ResponseEntity<?> getCell(@PathVariable("spreadsheetId") Long spreadSheet, @PathVariable("col") Integer col,
			@PathVariable("row") Integer row, @PathVariable("sheet") Integer sheet,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get Cell : SpreadsheetId = {}, Sheet index = {}, Col = {}, Row = {}", spreadSheet, sheet,
				col, row);
		try {
			SpreadSheetCell cell = getService().getCell(spreadSheet, col, row, sheet);
			log.debug("Found : {}", cell);
			return ResponseEntity.status(HttpStatus.OK).body(cell);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error(
					"Unexpected error while retrieving cell : SpreadsheetId = {}, Sheet index = {}, Col = {}, Row = {}",
					spreadSheet, sheet, col, row, e);
			String message = messageSource.getMessage("unable.to.retieve.cell", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
