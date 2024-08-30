package com.moriset.bcephal.sourcing.grid.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
import com.moriset.bcephal.grid.domain.FindReplaceFilter;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleEditedElement;
import com.moriset.bcephal.grid.domain.GrilleEditedResult;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.grid.service.DimensionDataFilter;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sourcing/grid")
@Slf4j
public class GrilleController extends BaseController<Grille, BrowserData> {

	@Autowired
	GrilleService grilleService;

	@Override
	protected GrilleService getService() {
		return grilleService;
	}
	
	
	@PostMapping("/create-report/{gridId}")
	public ResponseEntity<?> createReport(@PathVariable("gridId") Long gridId,
			@RequestBody String reportName, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of createReport");
		try {
			Long result = getService().createReport(gridId, reportName, locale);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.error("", e);
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while .", e);
			String message = messageSource.getMessage("unable.to.create.report", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/set-editable/{gridId}/{editable}")
	public ResponseEntity<?> setEditable(@PathVariable("gridId") Long gridId,
			@PathVariable("editable") boolean editable, @RequestHeader("Accept-Language") java.util.Locale locale) {

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
	public ResponseEntity<?> searchRows(@RequestBody GrilleDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

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
	
	@PostMapping("/rows2")
	public ResponseEntity<?> searchRows2(@RequestBody GrilleDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of searchRows");
		try {
			 BrowserDataPage<GridItem> page = getService().searchRows2(filter, locale);
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

	@PostMapping("/duplicate-rows")
	public ResponseEntity<?> duplicateRows(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of duplicateRows");
		try {
			int count = getService().duplicateRows(ids, locale);
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

	@PostMapping("/delete-rows")
	public ResponseEntity<?> deleteRows(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of deleteRows");
		try {
			int count = getService().deleteRows(ids, locale);
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

	@PostMapping("/delete-all-rows")
	public ResponseEntity<?> deleteAllRows(@RequestBody Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of deleteAllRows");
		try {
			int count = getService().deleteAllRows(id, locale);
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
	public ResponseEntity<?> deleleAllRows(@RequestBody GrilleDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of deleleAllRows");
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
	public ResponseEntity<?> editCell(@RequestBody GrilleEditedElement element,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cell");
		try {
			GrilleEditedResult result = getService().editCell(element, locale);
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
	public ResponseEntity<?> editCell(@RequestBody List<GrilleEditedElement> elements,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cells");
		try {
			Long result = getService().editCells(elements, locale);
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

	@PostMapping("/count-rows-for-default-value/{gridId}")
	public ResponseEntity<?> countAffectedRowsByApplyDefaultValue(@PathVariable("gridId") Long gridId,
			@RequestBody GrilleColumn column, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cell");
		try {
			int count = getService().countAffectedRowsByApplyDefaultValue(gridId, column, locale);
			log.debug("Apply Default value : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while applying default value.", e);
			String message = messageSource.getMessage("unable.to.apply.default.value", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/apply-default-value/{gridId}")
	public ResponseEntity<?> applyDefaultValue(@PathVariable("gridId") Long gridId, @RequestBody GrilleColumn column,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cell");
		try {
			int count = getService().applyDefaultValue(gridId, column, locale);
			log.debug("Apply Default value : {}", count);
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while applying default value.", e);
			String message = messageSource.getMessage("unable.to.apply.default.value", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/find-and-replace")
	public ResponseEntity<?> findAndReplace(@RequestBody FindReplaceFilter criteria,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
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
	public ResponseEntity<?> findAndReplaceCount(@RequestBody FindReplaceFilter criteria,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
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

	@GetMapping("/load/{gridId}")
	public ResponseEntity<?> load(@PathVariable("gridId") Long gridId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cell");
		try {
			boolean success = getService().changeStatus(gridId, GrilleStatus.LOADED, locale);
			log.debug("Load : {}", success);
			return ResponseEntity.status(HttpStatus.OK).body(success);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while loading grid.", e);
			String message = messageSource.getMessage("unable.to.load.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/publish/{gridId}")
	public ResponseEntity<?> publish(@PathVariable("gridId") Long gridId, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		log.debug("Call of Publish Grid");
		try {
			var ed = getService().publish(gridId, locale, session);
			log.debug("Publish : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while publishing grid.", e);
			String message = messageSource.getMessage("unable.to.publish.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/publish")
	public ResponseEntity<?> publish(@RequestBody List<Long> gridIds, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		log.debug("Call of Publish Grids");
		try {
			var ed = getService().publish(gridIds, locale, session);
			log.debug("Publish : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while publishing grids.", e);
			String message = messageSource.getMessage("unable.to.publish.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/reset-publication/{gridId}")
	public ResponseEntity<?> resetPublication(@PathVariable("gridId") Long gridId, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		
		log.debug("Call of Reset Grid");
		try {
			var ed = getService().resetPublication(gridId, locale, session);
			log.debug("ResetPublication : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while resetting grid.", e);
			String message = messageSource.getMessage("unable.to.reset.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/reset-publication")
	public ResponseEntity<?> resetPublication(@RequestBody List<Long> gridIds, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		
		log.debug("Call of Reset Grids");
		try {
			var ed = getService().resetPublication(gridIds, locale, session);
			log.debug("ResetPublication : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while resetting grids.", e);
			String message = messageSource.getMessage("unable.to.reset.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/refresh-publication/{gridId}")
	public ResponseEntity<?> refreshPublication(@PathVariable("gridId") Long gridId, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of refreshPublication Grid");
		try {
			boolean result = getService().refreshPublication(gridId, locale);
			log.debug("RefreshPublication : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while refreshing grid.", e);
			String message = messageSource.getMessage("unable.to.refresh.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/republish")
	public ResponseEntity<?> republish(@RequestBody List<Long> gridIds, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of refreshPublication Grids");
		try {
			boolean result = getService().republish(gridIds, locale);
			log.debug("Republish : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while republish grids.", e);
			String message = messageSource.getMessage("unable.to.republish.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/refresh-publication")
	public ResponseEntity<?> refreshPublication(@RequestBody List<Long> gridIds, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of refreshPublication Grids");
		try {
			boolean result = getService().refreshPublication(gridIds, locale);
			log.debug("RefreshPublication : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while refreshing grids.", e);
			String message = messageSource.getMessage("unable.to.refresh.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/unload/{gridId}")
	public ResponseEntity<?> unload(@PathVariable("gridId") Long gridId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of Edit cell");
		try {
			boolean success = getService().changeStatus(gridId, GrilleStatus.UNLOADED, locale);
			log.debug("Load : {}", success);
			return ResponseEntity.status(HttpStatus.OK).body(success);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while unloading grid.", e);
			String message = messageSource.getMessage("unable.to.unload.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/columns/{gridId}")
	public ResponseEntity<?> getGridColumns(JwtAuthenticationToken principal, @PathVariable("gridId") Long gridId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of getGridColumns by given gridId : {} ", gridId);
		try {
			List<GrilleColumn> columns = grilleService.getColumns(gridId);
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
	public ResponseEntity<?> getColumnCountDetails(@RequestBody GrilleDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
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
	public ResponseEntity<?> getColumnDuplicateCount(@RequestBody GrilleDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {
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
	public ResponseEntity<?> getColumnDuplicate(@RequestBody GrilleDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {
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
	
	@PostMapping("/search-dimension-values")
	public ResponseEntity<?> searchAttributeValues(@RequestBody DimensionDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of searchAttributeValues");
		try {
			BrowserDataPage<Object> page = getService().searchAttributeValues(filter, locale);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getting attribute Values...", e);
			String message = messageSource.getMessage("unable.to.load.attribute.values", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
