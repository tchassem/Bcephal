/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.math.BigDecimal;
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
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.initiation.domain.Measure;
import com.moriset.bcephal.initiation.domain.api.MeasureApi;
import com.moriset.bcephal.initiation.repository.MeasureRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class MeasureService extends DimensionService<Measure> {

	@Autowired
	MeasureRepository measureRepository;

	

	@Override
	public MeasureRepository getRepository() {
		return measureRepository;
	}

	public EditorData<Measure> getEditorData(EditorDataFilter filter, Locale locale) {
		EditorData<Measure> data = new EditorData<Measure>();
		data.setItem(new Measure());
		data.getItem().setChildren(getMeasures(locale));
		return data;
	}

	public List<Measure> getMeasures(java.util.Locale locale) {
		log.debug("Try to  retrieve Measures.");
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByParentIsNull();
	}
	
	@Transactional
	public Measure createMeasure(MeasureApi measureApi, Locale locale) {
		log.debug("Try to  create measure : {}", measureApi);
		try {
			if (measureApi == null) {
				String message = messageSource.getMessage("unable.to.save.null.measure", new Object[] { "" }, locale);
				throw new BcephalException( message);
			}
			if (!StringUtils.hasText(measureApi.getName())) {
				String message = messageSource.getMessage("unable.to.create.measure.without.name", new Object[] { "" },
						locale);
				throw new BcephalException(message);
			}
			Measure measure = measureRepository.findByName(measureApi.getName());
			if (measure != null) {
				String message = messageSource.getMessage("duplicate.measure.name",
						new Object[] { measureApi.getName() }, locale);
				throw new BcephalException(message);
			}
			measure = new Measure();
			measure.setName(measureApi.getName());
			Measure parent = null;
			if (measureApi.getParent() != null) {
				Optional<Measure> result = measureRepository.findById(measureApi.getParent());
				if (result.isEmpty()) {
					String message = messageSource.getMessage("measure.not.found",
							new Object[] { measureApi.getParent() }, locale);
					throw new BcephalException(message);
				}
				parent = result.get();
				measure.setParent(parent);
				measure.setPosition(parent.getChildrenListChangeHandler().getItems().size());
			} else {
				measure.setPosition(measureRepository.findByParentIsNull().size());
			}

			boolean isNewDimension = measure.getId() == null;
			measure = measureRepository.save(measure);
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(measure);
			}

			log.debug("Measure successfully to created : {} ", measure.getId());
			return measure;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating measure : {}", measureApi, e);
//			String message = messageSource.getMessage("unable.to.create.measure", new Object[] { measureApi }, locale);
			String message = "Unable to create measure : " + measureApi.getName();
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Transactional
	public Measure saveRoot(Measure measure, Locale locale) {
		log.debug("Try to  Save measure : {}", measure);
		try {
			if (measure == null) {
				String message = messageSource.getMessage("unable.to.save.null.measure", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<Measure> listHandler = measure.getChildrenListChangeHandler();
			saveChildren(null, locale, listHandler);
			log.debug("Measure successfully to save : {} ", measure.getId());
			return measure;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save model : {}", measure, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { measure }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	@Transactional
	public Measure save(Measure measure, Locale locale) {
		try {
			log.debug("Try to  Save measure : {}", measure);
			if (measure == null) {
				String message = messageSource.getMessage("unable.to.save.null.measure", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(measure.getName())) {
				String message = messageSource.getMessage("unable.to.save.measure.with.empty.name",
						new String[] { measure.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<Measure> listHandler = measure.getChildrenListChangeHandler();

			boolean isNewDimension = measure.getId() == null;
			measure = super.save(measure, locale);
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(measure);
			}

			saveChildren(measure, locale, listHandler);
			log.debug("Measure successfully to save : {} ", measure.getId());
			measure = getById(measure.getId());
			return measure;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save measure : {}", measure, e);
			String message = messageSource.getMessage("unable.to.save.measure", new Object[] { measure }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void saveChildren(Measure parent, Locale locale, ListChangeHandler<Measure> listHandler) {
		List<Measure> newItems = listHandler.getNewItems();
		List<Measure> updatedItems = listHandler.getUpdatedItems();
		List<Measure> deletedItems = listHandler.getDeletedItems();
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
				deleteMeasure(item, locale);
			});
		}
	}
	
	@Transactional
	public void deleteMeasure(Measure measure, Locale locale) {
		log.debug("Try to  delete measure : {}", measure);
		try {
			if (measure == null || measure.getId() == null) {
				String message = messageSource.getMessage("unable.to.delete.null.measure", new Object[] { measure },
						locale);
				throw new BcephalException(message);
			}
			measure.getChildrenListChangeHandler().getItems().forEach(item -> {
				deleteMeasure(item, locale);
			});
			measure.getChildrenListChangeHandler().getDeletedItems().forEach(item -> {
				deleteMeasure(item, locale);
			});
			if (measure.getId() != null) {
				deleteById(measure.getId());
				universeGenerator.dropUniverseColumn(measure);
			}
			log.debug("Measure successfully to delete : {} ", measure);
			return;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete measure : {}", measure, e);
			String message = messageSource.getMessage("unable.to.delete.measure", new Object[] { measure }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public BrowserDataPage<BigDecimal> searchMeasureValues(BrowserDataFilter filter, Locale locale) throws Exception {
		Optional<Measure> result = measureRepository.findById(filter.getGroupId());
		if (result.isPresent()) {
			return getMeasureValues(filter, locale);
		} else {
			throw new BcephalException("Measure not found. ID : {}", filter.getGroupId());
		}
	}

	private BrowserDataPage<BigDecimal> getMeasureValues(BrowserDataFilter filter, java.util.Locale locale) {

		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<BigDecimal> page = new BrowserDataPage<BigDecimal>();
		page.setPageSize(filter.getPageSize());
		Integer count = 0;

		String col = new Measure(filter.getGroupId()).getUniverseTableColumnName();
		String sql = "SELECT distinct " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME + " WHERE " + col
				+ " IS NOT NULL ";
		if (StringUtils.hasText(filter.getCriteria())) {
			sql += " AND  " + col + "  = " + filter.getCriteria();
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
		List<BigDecimal> values = query.getResultList();
		page.setPageLastItem(page.getPageFirstItem() + values.size() - 1);
		page.setPageSize(values.size());
		page.setItems(values);
		log.debug("Row found : {}", values.size());
		return page;

	}
	
}
