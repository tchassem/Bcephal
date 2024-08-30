/**
 * 
 */
package com.moriset.bcephal.sheet.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.repository.filters.MeasureFilterItemRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.sheet.domain.SpreadSheet;
import com.moriset.bcephal.sheet.domain.SpreadSheetBrowserData;
import com.moriset.bcephal.sheet.domain.SpreadSheetCell;
import com.moriset.bcephal.sheet.domain.SpreadSheetSource;
import com.moriset.bcephal.sheet.domain.SpreadSheetType;
import com.moriset.bcephal.sheet.repository.SpreadSheetCellRepository;
import com.moriset.bcephal.sheet.repository.SpreadSheetRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.criteria.Predicate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class SpreadSheetService extends MainObjectService<SpreadSheet, SpreadSheetBrowserData> {

	@Autowired
	SpreadSheetRepository spreadSheetRepository;

	@Autowired
	SpreadSheetCellRepository spreadSheetCellRepository;

	@Autowired
	UniverseFilterService universeFilterService;

	@Autowired
	MeasureFilterItemRepository cellMeasureRepository;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	public SpreadSheetRepository getRepository() {
		return spreadSheetRepository;
	}

	protected SpreadSheetType getSpreadSheetType() {
		return SpreadSheetType.INPUT;
	}

	@Transactional
	public SpreadSheet save(SpreadSheetManager manager, Locale locale) {
		log.debug("Try to  Save SpreadSheet : {}", manager.getSpreadSheet());
		SpreadSheet spreadSheet = manager.getSpreadSheet();
		try {
			if (spreadSheet == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.spreadsheet",
						new Object[] { spreadSheet }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if (!StringUtils.hasLength(spreadSheet.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.spreadsheet.with.empty.name",
						new String[] { spreadSheet.getName() }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}

			if (spreadSheet.getFilter() != null) {
				spreadSheet.setFilter(universeFilterService.save(spreadSheet.getFilter()));
			}

			manager.setService(this);
			manager.buildCells();

			ListChangeHandler<SpreadSheetCell> cells = spreadSheet.getCellListChangeHandler();

			spreadSheet.setModificationDate(new Timestamp(System.currentTimeMillis()));
			spreadSheet = spreadSheetRepository.save(spreadSheet);
			Long id = spreadSheet.getId();

			cells.getNewItems().forEach(item -> {
				log.trace("Try to save Cell : {}", item);
				item.setSpreadSheet(id);
				if (item.getFilter() != null) {
					item.setFilter(universeFilterService.save(item.getFilter()));
				}
				if (item.getCellMeasure() != null) {
					item.setCellMeasure(cellMeasureRepository.save(item.getCellMeasure()));
				}
				spreadSheetCellRepository.save(item);
				log.trace("Cell saved : {}", item.getId());
			});
			cells.getUpdatedItems().forEach(item -> {
				log.trace("Try to save Cell : {}", item);
				item.setSpreadSheet(id);
				if (item.getFilter() != null) {
					item.setFilter(universeFilterService.save(item.getFilter()));
				}
				if (item.getCellMeasure() != null) {
					item.setCellMeasure(cellMeasureRepository.save(item.getCellMeasure()));
				}
				spreadSheetCellRepository.save(item);
				log.trace("Cell saved : {}", item.getId());
			});
			cells.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Cell : {}", item);
					spreadSheetCellRepository.deleteById(item.getId());
					log.trace("Cell deleted : {}", item.getId());
				}
			});

			log.debug("SpreadSheet saved : {} ", spreadSheet.getId());
			return spreadSheet;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save SpreadSheet : {}", spreadSheet, e);
			String message = getMessageSource().getMessage("unable.to.save.spreadsheet", new Object[] { spreadSheet },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public SpreadSheetCell getCell(Long spreadSheet, int col, int row, int sheetIndex) {
		Optional<SpreadSheetCell> cell = spreadSheetCellRepository
				.findBySpreadSheetAndColAndRowAndSheetIndex(spreadSheet, col, row, sheetIndex);
		return cell.isPresent() ? cell.get() : null;
	}

	public List<SpreadSheetCell> getCells(Long spreadSheet) {
		return spreadSheetCellRepository.findBySpreadSheet(spreadSheet);
	}

	protected SpreadSheet getNewItem(String baseName, boolean startWithOne) {
		SpreadSheet spreadSheet = new SpreadSheet();
		spreadSheet.setType(getSpreadSheetType());
		spreadSheet.setSourceType(SpreadSheetSource.USER);
		int i = 0;
		spreadSheet.setName(baseName);
		if (startWithOne) {
			i = 1;
			spreadSheet.setName(baseName + i);
		}
		while (getByName(spreadSheet.getName()) != null) {
			i++;
			spreadSheet.setName(baseName + i);
		}
		return spreadSheet;
	}

	protected SpreadSheet getNewItem(String baseName) {
		return getNewItem(baseName, true);
	}

	@Override
	protected SpreadSheet getNewItem() {
		return getNewItem("Input spreadsheet ");
	}

	@Override
	protected Specification<SpreadSheet> getBrowserDatasSpecification(BrowserDataFilter filter,java.util.Locale locale,	List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<SpreadSheet> qBuilder = new RequestQueryBuilder<SpreadSheet>(root, query, cb);
			qBuilder.select(SpreadSheetBrowserData.class);
			Predicate predicate = qBuilder.getCriteriaBuilder().equal(qBuilder.getRoot().get("type"),
					getSpreadSheetType());
			qBuilder.add(predicate);
			qBuilder.addNoTInObjectId(hidedObjectIds);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if(filter.getColumnFilters() != null) {
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	@Override
	protected SpreadSheetBrowserData getNewBrowserData(SpreadSheet item) {
		return new SpreadSheetBrowserData(item);
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return null;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
}
