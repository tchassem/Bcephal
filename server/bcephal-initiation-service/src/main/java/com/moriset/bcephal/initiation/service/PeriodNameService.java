/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.initiation.domain.PeriodName;
import com.moriset.bcephal.initiation.domain.api.PeriodApi;
import com.moriset.bcephal.initiation.repository.PeriodNameRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class PeriodNameService extends DimensionService<PeriodName> {

	@Autowired
	PeriodNameRepository periodNameRepository;

	@Override
	public PeriodNameRepository getRepository() {
		return periodNameRepository;
	}

	public EditorData<PeriodName> getEditorData(EditorDataFilter filter, Locale locale) {
		EditorData<PeriodName> data = new EditorData<PeriodName>();
		data.setItem(new PeriodName());
		data.getItem().setChildren(getPeriodNames(locale));
		return data;
	}

	public List<PeriodName> getPeriodNames(java.util.Locale locale) {
		log.debug("Try to  retrieve PeriodNames.");
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByParentIsNull();
	}

	@Transactional
	public PeriodName createPeriod(PeriodApi periodApi, Locale locale) {
		log.debug("Try to  create period : {}", periodApi);
		try {
			if (periodApi == null) {
				String message = messageSource.getMessage("unable.to.save.null.period", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(periodApi.getName())) {
				String message = messageSource.getMessage("unable.to.create.period.without.name", new Object[] { "" },
						locale);
				throw new BcephalException(message);
			}
			PeriodName period = periodNameRepository.findByName(periodApi.getName());
			if (period != null) {
				String message = messageSource.getMessage("duplicate.period.name", new Object[] { periodApi.getName() },
						locale);
				throw new BcephalException(message);
			}
			period = new PeriodName();
			period.setName(periodApi.getName());
			PeriodName parent = null;
			if (periodApi.getParent() != null) {
				Optional<PeriodName> result = periodNameRepository.findById(periodApi.getParent());
				if (result.isEmpty()) {
					String message = messageSource.getMessage("period.not.found",
							new Object[] { periodApi.getParent() }, locale);
					throw new BcephalException(message);
				}
				parent = result.get();
				period.setParent(parent);
				period.setPosition(parent.getChildrenListChangeHandler().getItems().size());
			} else {
				period.setPosition(periodNameRepository.findByParentIsNull().size());
			}

			boolean isNewDimension = period.getId() == null;
			period = periodNameRepository.save(period);
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(period);
			}

			log.debug("Period successfully to created : {} ", period.getId());
			return period;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating measure : {}", periodApi, e);
			String message = messageSource.getMessage("unable.to.create.measure", new Object[] { periodApi }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Transactional
	public PeriodName saveRoot(PeriodName period, Locale locale) {
		log.debug("Try to  Save PeriodName : {}", period);
		try {
			if (period == null) {
				String message = messageSource.getMessage("unable.to.save.null.period", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<PeriodName> listHandler = period.getChildrenListChangeHandler();
			saveChildren(null, locale, listHandler);
			log.debug("Measure successfully to save : {} ", period.getId());
			return period;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save PeriodName : {}", period, e);
			String message = messageSource.getMessage("unable.to.save.period", new Object[] { period }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	public PeriodName save(PeriodName period, Locale locale) {
		try {
			log.debug("Try to  Save PeriodName : {}", period);
			if (period == null) {
				String message = messageSource.getMessage("unable.to.save.null.period", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(period.getName())) {
				String message = messageSource.getMessage("unable.to.save.period.with.empty.name",
						new String[] { period.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<PeriodName> listHandler = period.getChildrenListChangeHandler();

			boolean isNewDimension = period.getId() == null;
			period = super.save(period, locale);
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(period);
			}

			saveChildren(period, locale, listHandler);
			log.debug("Measure successfully to save : {} ", period.getId());
			period = getById(period.getId());
			return period;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save period : {}", period, e);
			String message = messageSource.getMessage("unable.to.save.period", new Object[] { period }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void saveChildren(PeriodName parent, Locale locale, ListChangeHandler<PeriodName> listHandler) {
		List<PeriodName> newItems = listHandler.getNewItems();
		List<PeriodName> updatedItems = listHandler.getUpdatedItems();
		List<PeriodName> deletedItems = listHandler.getDeletedItems();
		if (newItems != null) {
			newItems.forEach(item -> {
				item.setParent(parent);
				save(item, locale);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				item.setParent(parent);
				save(item, locale);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				deletePeriod(item, locale);
			});
		}
	}

	public void deletePeriod(PeriodName periodName, Locale locale) {
		log.debug("Try to  delete period name : {}", periodName);
		try {
			if (periodName == null || periodName.getId() == null) {
				String message = messageSource.getMessage("unable.to.delete.null.period.name",
						new Object[] { periodName }, locale);
				throw new BcephalException(message);
			}
			periodName.getChildrenListChangeHandler().getItems().forEach(item -> {
				deletePeriod(item, locale);
			});
			periodName.getChildrenListChangeHandler().getDeletedItems().forEach(item -> {
				deletePeriod(item, locale);
			});
			if (periodName.getId() != null) {
				deleteById(periodName.getId());
				universeGenerator.dropUniverseColumn(periodName);
			}
			log.debug("Period successfully to delete : {} ", periodName);
			return;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete period name : {}", periodName, e);
			String message = messageSource.getMessage("unable.to.delete.period.name", new Object[] { periodName },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public BrowserDataPage<Date> searchPeriodValues(BrowserDataFilter filter, Locale locale) throws Exception {
		Optional<PeriodName> result = periodNameRepository.findById(filter.getGroupId());
		if (result.isPresent()) {
			return getPeriodValues(filter, locale);
		} else {
			throw new BcephalException("Period not found. ID : {}", filter.getGroupId());
		}
	}

	private BrowserDataPage<Date> getPeriodValues(BrowserDataFilter filter, java.util.Locale locale) {

		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<Date> page = new BrowserDataPage<Date>();
		page.setPageSize(filter.getPageSize());
		Integer count = 0;

		String col = new Period(filter.getGroupId()).getUniverseTableColumnName();
		String sql = "SELECT distinct " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME + " WHERE " + col
				+ " IS NOT NULL";
		if (StringUtils.hasText(filter.getCriteria())) {
			sql += " AND  " + col + "  = '" + filter.getCriteria() + "'";
		}

		if (filter.isAllowRowCounting()) {
			String countsql = "SELECT COUNT(1) FROM (" + sql + ") AS A";
			log.trace("Count query : {}", countsql);
			Query query = this.session.createNativeQuery(countsql);
			Number number = (Number) query.getSingleResult();
			count = number.intValue();
			if (count == 0) {
				return page;
			}
			if (filter.isShowAll()) {
				page.setTotalItemCount(count);
				page.setPageCount(1);
				page.setCurrentPage(1);
				page.setPageFirstItem(1);
			} else {
				page.setTotalItemCount(count);
				page.setPageCount(((int) count / filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				if (page.getCurrentPage() > page.getPageCount()) {
					page.setCurrentPage(page.getPageCount());
				}
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
		} else {
			page.setTotalItemCount(1);
			page.setPageCount(1);
			page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
			page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
		}

		sql += " ORDER BY " + col;
		log.trace("Search query : {}", sql);

		Query query = this.session.createNativeQuery(sql);
		if (!filter.isShowAll()) {
			query.setFirstResult(page.getPageFirstItem() - 1);
			query.setMaxResults(page.getPageSize());
		}
		@SuppressWarnings("unchecked")
		List<Date> values = query.getResultList();
		page.setPageLastItem(page.getPageFirstItem() + values.size() - 1);
		page.setPageSize(values.size());
		page.setItems(values);
		log.debug("Row found : {}", values.size());

		return page;
	}
	
}
