package com.moriset.bcephal.sourcing.grid.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.GrilleEditedResult;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridCopyData;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.grid.domain.MaterializedGridEditedElement;
import com.moriset.bcephal.grid.domain.MaterializedGridFindReplaceFilter;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sourcing/mat-grid")
@Slf4j
public class MaterializedGridController extends BaseController<MaterializedGrid, BrowserData> {

	@Autowired
	MaterializedGridService materializedGridService;

	@Override
	protected MaterializedGridService getService() {
		return materializedGridService;
	}
	
	@PostMapping("/create-report/{gridId}")
	public ResponseEntity<?> createReport(@PathVariable("gridId") Long gridId,
			@RequestBody String reportName, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of createReport");
		try {
			Long result = getService().createReport(gridId, reportName, locale);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while .", e);
			String message = messageSource.getMessage("unable.to.create.report", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/set-editable/{gridId}/{editable}")
	public ResponseEntity<?> setEditable(@PathVariable("gridId") Long gridId, @PathVariable("editable") boolean editable, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of setEditable");
		try {
			boolean result = getService().setEditable(gridId, editable, locale);
			log.debug("setEditable : {}", editable);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while .", e);
			String message = messageSource.getMessage("unable.to.edit.cell", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/rows")
	public ResponseEntity<?> searchRows(@RequestBody MaterializedGridDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of searchRows");
		try {
			BrowserDataPage<Object[]> page = getService().searchRows(filter, locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
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

	@PostMapping("/duplicate-rows/{matGridId}")
	public ResponseEntity<?> duplicateRows(@PathVariable("matGridId") Long matGridId, @RequestBody List<Long> ids, 
			JwtAuthenticationToken principal,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of duplicateRows");
		try {
			int count = getService().duplicateRows(matGridId, ids, locale, principal.getName());
			log.debug("Duplicated : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while duplicating rows : {}", ids, e);
			String message = messageSource.getMessage("unable.to.duplicate.rows", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/delete-rows/{matGridId}")
	public ResponseEntity<?> deleteRows(@PathVariable("matGridId") Long matGridId, @RequestBody List<Long> ids, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of deleteRows");
		try {
			int count = getService().deleteRows(matGridId, ids, locale);
			log.debug("Deleted : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting rows : {}", ids, e);
			String message = messageSource.getMessage("unable.to.delete.rows", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/delete-all-rows/{matGridId}")
	public ResponseEntity<?> deleteAllRows(@PathVariable("matGridId") Long matGridId, @RequestBody Long id, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of deleteAllRows");
		try {
			int count = getService().deleteAllRows(matGridId, id, locale);
			log.debug("Deleted : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting all rows : {}", id, e);
			String message = messageSource.getMessage("unable.to.delete.all.rows", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/delete-all-rows-by-filter")
	public ResponseEntity<?> deleteAllRows(@RequestBody MaterializedGridDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of deleteAllRows");
		try {
			int count = getService().deleteAllRows(filter, locale);
			log.debug("Deleted : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting all rows : {}", filter, e);
			String message = messageSource.getMessage("unable.to.delete.all.rows", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/edit-cell")
	public ResponseEntity<?> editCell(@RequestBody MaterializedGridEditedElement element,			 
			JwtAuthenticationToken principal,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cell");
		try {
			GrilleEditedResult result = getService().editCell(element, locale, principal.getName());
			result.setDatas(getService().getGridRow(element.getGrid(), element.getId(), locale));
			log.debug("Edied cell : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while editing cell.", e);
			String message = messageSource.getMessage("unable.to.edit.cell", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/edit-cells")
	public ResponseEntity<?> editCell(@RequestBody List<MaterializedGridEditedElement> elements, 
			JwtAuthenticationToken principal,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cells");
		try {
			Long result = getService().editCells(elements, locale, principal.getName());
			log.debug("Edied cells : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while editing cells.", e);
			String message = messageSource.getMessage("unable.to.edit.cells", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/find-and-replace")
	public ResponseEntity<?> findAndReplace(@RequestBody MaterializedGridFindReplaceFilter criteria, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of findAndReplace");
		try {
			int count = getService().findAndReplace(criteria, locale);
			log.debug("Replace value count : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while replacing value.", e);
			String message = messageSource.getMessage("unable.to.replace.value", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/find-and-replace-count")
	public ResponseEntity<?> findAndReplaceCount(@RequestBody MaterializedGridFindReplaceFilter criteria, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of findAndReplaceCount");
		try {
			int count = getService().findAndReplaceCount(criteria, locale);
			log.debug("Found value count : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while counting value.", e);
			String message = messageSource.getMessage("unable.to.count.value", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/publish/{matGridId}")
	public ResponseEntity<?> publish(@PathVariable("matGridId") Long matGridId, @RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		log.debug("Call of Publish Grid");
		try {
			var ed = getService().publish(matGridId, locale, session);
			log.debug("Publish : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while publishing grid.", e);
			String message = messageSource.getMessage("unable.to.publish.grid", new Object[] { matGridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/publish")
	@Transactional
	public ResponseEntity<?> publish(@RequestBody List<Long> matGridIds, @RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		log.debug("Call of Publish Grids");
		try {
			var ed = getService().publish(matGridIds, locale, session);
			log.debug("Publish : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while publishing grids.", e);
			String message = messageSource.getMessage("unable.to.publish.grids", new Object[] { matGridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}		
	
	@GetMapping("/columns/{matGridId}")
	public ResponseEntity<?> getGridColumns(JwtAuthenticationToken principal, @PathVariable("matGridId") Long matGridId, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of getMaterializedGridColumns by given matGridId : {} ", matGridId);
		try {
			List<MaterializedGridColumn> columns = materializedGridService.getColumns(matGridId);
			return ResponseEntity.status(HttpStatus.OK).body(columns);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getGridColumns ...", e);
			String message = messageSource.getMessage("unable.to.load.columns", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/column/count")
	public ResponseEntity<?> getColumnCountDetails(@RequestBody MaterializedGridDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of count rows");
		try {
			GrilleColumnCount gridColumn = getService().getColumnCountDetails(filter, locale);	
			log.debug("Found : {}", gridColumn);
			return ResponseEntity.status(HttpStatus.OK).body(gridColumn);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while count column details by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.count.column.detail.by.filter", new Object[] { },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/rows/duplicate-count")
	public ResponseEntity<?> getColumnDuplicateCount(@RequestBody MaterializedGridDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of count rows");
		try {
			long duplicateRows = getService().getColumnDuplicateCount(filter, locale);
			log.debug("Found : {}", duplicateRows);
			return ResponseEntity.status(HttpStatus.OK).body(duplicateRows);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while count duplicate rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.count.duplicate.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/rows/duplicate")
	public ResponseEntity<?> getColumnDuplicate(@RequestBody MaterializedGridDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of count rows");
		try {
			BrowserDataPage<Object[]> page = getService().getColumnDuplicate(filter, locale);
			//log.debug("Found : {}", duplicateRows);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while count duplicate rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.count.duplicate.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/search-attribute-values")
	public ResponseEntity<?> searchColumnValues(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of searchColumnValues");
		try {
			BrowserDataPage<String> page = getService().searchColumnValues(filter, locale);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getting column values...", e);
			String message = messageSource.getMessage("unable.to.load.column.values", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@PostMapping("/copy-rows")
	public ResponseEntity<?> copyRows(MaterializedGridCopyData data,
			@RequestHeader("Accept-Language") java.util.Locale locale) {		
		try {
			MaterializedGridService service = getService();
			int count = service.copyRows(data, locale);
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while copying rows...", e);
			String message = messageSource.getMessage("unable.to.copy.rows", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
