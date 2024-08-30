package com.moriset.bcephal.dashboard.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.service.DashboardReportService;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/dashboarding/report")
@Slf4j
public class DashboardReportController extends BaseController<DashboardReport, BrowserData> {

	@Autowired
	DashboardReportService dashboardReportService;

	@Override
	protected DashboardReportService getService() {
		return dashboardReportService;
	}

	@GetMapping("/rows/{reportId}")
	public ResponseEntity<?> getRowsById(@PathVariable("reportId") Long reportId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get rows by ID : {}", reportId);
		try {
			ArrayNode datas = getService().getRows(reportId, locale);
			log.trace("Found : {}", "");
			return ResponseEntity.status(HttpStatus.OK).body(datas);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving dashboard report with id : {}", reportId, e);
			String message = messageSource.getMessage("unable.to.retieve.dashboard.report.by.id",
					new Object[] { reportId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	protected String getFunctionalityCode(Long entityId) {
		return getService().getFunctionalityCode(entityId);
	}

	@PostMapping("/rows")
	public ResponseEntity<?> getRows(@RequestBody DashboardReport report,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get rows ");
		try {
			ArrayNode datas = getService().getRows(report, locale);
			log.trace("Found : {}", "");
			return ResponseEntity.status(HttpStatus.OK).body(datas);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving dashboard report with id : {}", report, e);
			String message = messageSource.getMessage("unable.to.retieve.dashboard.report.by.id",
					new Object[] { report }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@Autowired
	ResourceLoader resourceLoader;

	@PostMapping("/export-to-pdf/{chartName}")
	public ResponseEntity<?> exportToPdf(@RequestBody GrilleDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale, @PathVariable("chartName") String chartName) {

		log.debug("Call of searchRows");
		try {
			String path = ((DashboardReportService) getService()).exportToPdf(filter, chartName, locale);
			log.debug("Download PDF form Path : {}", path);
			return ResponseEntity.status(HttpStatus.OK).body(resourceLoader.getResource(path));
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/save-as/{ChartId}/{chartName}")
	public ResponseEntity<?> saveAs(@PathVariable("ChartId") long ChartId ,@PathVariable("chartName") String chartName, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of save as: {}", ChartId);
		try {
			Long result = copy(ChartId,chartName, locale);
			log.debug("save as: {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save as : {}", ChartId, e);
			String message = messageSource.getMessage("unable.to.save.as.by.id", new Object[] { ChartId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	public Long copy(Long id,String chartName, Locale locale) {
		log.debug("Try to copy entity : {}", id);
		DashboardReport item = getService().getById(id);
		if(item != null) {
			DashboardReport copy = item.copy();
			copy.setName(chartName);
			DashboardReport result = getService().save(copy, locale);
			return result.getId();
		}
		return null;
	}
	
}
