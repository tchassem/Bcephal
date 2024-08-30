/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.AttributeValue;
import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.api.AttributeApi;
import com.moriset.bcephal.initiation.repository.AttributeRepository;
import com.moriset.bcephal.initiation.repository.EntityRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class AttributeService extends DimensionService<Attribute> {

	@Autowired
	AttributeRepository attributeRepository;

	@Autowired
	EntityRepository entityRepository;

	@Autowired
	AttributeValueService attributeValueService;


	@Override
	public AttributeRepository getRepository() {
		return attributeRepository;
	}

	@Transactional
	public Attribute createAttribute(AttributeApi attributeApi, Locale locale) {
		log.debug("Try to  create attribute : {}", attributeApi);
		try {
			if (attributeApi == null) {
				String message = messageSource.getMessage("unable.to.save.null.attribute", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(attributeApi.getName())) {
				String message = messageSource.getMessage("unable.to.create.attribute.without.name",
						new Object[] { "" }, locale);
				throw new BcephalException(message);
			}

			Attribute attribute = attributeRepository.findByName(attributeApi.getName());
			if (attribute != null) {
				String message = messageSource.getMessage("duplicate.attribute.name",
						new Object[] { attributeApi.getName() }, locale);
				throw new BcephalException(message);
			}
			attribute = new Attribute();
			attribute.setName(attributeApi.getName());
			Attribute parent = null;
			if (attributeApi.getParent() != null) {
				Optional<Attribute> result = attributeRepository.findById(attributeApi.getParent());
				if (result.isEmpty()) {
					String message = messageSource.getMessage("attribute.not.found",
							new Object[] { attributeApi.getParent() }, locale);
					throw new BcephalException(message);
				}
				parent = result.get();
				attribute.setParent(parent);
				attribute.setPosition(parent.getChildrenListChangeHandler().getItems().size());
				attribute.setEntity(parent.getEntity());
			} else {
				if (attributeApi.getEntity() == null) {
					String message = messageSource.getMessage("unable.to.create.attribute.without.entity",
							new Object[] { "" }, locale);
					throw new BcephalException(message);
				}
				Optional<Entity> result = entityRepository.findById(attributeApi.getEntity());
				if (result.isEmpty()) {
					String message = messageSource.getMessage("entity.not.found",
							new Object[] { attributeApi.getEntity() }, locale);
					throw new BcephalException(message);
				}
				Entity entity = result.get();
				attribute.setEntity(entity);
				attribute.setPosition(entity.getAttributeListChangeHandler().getItems().size());
			}

			boolean isNewDimension = attribute.getId() == null;
			attribute = attributeRepository.save(attribute);
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(attribute);
			}

			log.debug("Attribute successfully to created : {} ", attribute.getId());
			return attribute;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating attribute : {}", attributeApi, e);
			String message = messageSource.getMessage("unable.to.create.attribute", new Object[] { attributeApi },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	@Transactional
	public Attribute save(Attribute attribute, Locale locale) {
		log.debug("Try to  Save attribute : {}", attribute);
		try {
			if (attribute == null) {
				String message = messageSource.getMessage("unable.to.save.null.attribute", new Object[] { attribute },
						locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(attribute.getName())) {
				String message = messageSource.getMessage("unable.to.save.attribute.with.empty.name",
						new String[] { attribute.getName() }, locale);
				throw new BcephalException( message);
			}
			ListChangeHandler<AttributeValue> values = attribute.getValueListChangeHandler();
			ListChangeHandler<Attribute> children = attribute.getChildrenListChangeHandler();

			boolean isNewDimension = attribute.getId() == null;

			attribute = super.save(attribute, locale);

			if (isNewDimension) {
				universeGenerator.createUniverseColumn(attribute);
			}

			saveChildren(attribute, locale, children);
			saveAttributeValue(attribute, locale, values);
			log.debug("Attribute successfully saved : {} ", attribute);
			return attribute;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save attribute : {}", attribute, e);
			String message = messageSource.getMessage("unable.to.save.attribute", new Object[] { attribute }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void saveChildren(Attribute attribute, Locale locale, ListChangeHandler<Attribute> listHandler) {
		List<Attribute> newItems = listHandler.getNewItems();
		List<Attribute> updatedItems = listHandler.getUpdatedItems();
		List<Attribute> deletedItems = listHandler.getDeletedItems();
		Attribute id = attribute;
		if (newItems != null) {
			newItems.forEach(item -> {
				item.setParent(id);
				save(item, locale);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				item.setParent(id);
				save(item, locale);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				deleteAttribute(item, locale);
			});
		}
	}

	private void saveAttributeValue(Attribute attribute, Locale locale, ListChangeHandler<AttributeValue> listHandler) {
		List<AttributeValue> newItems = listHandler.getNewItems();
		List<AttributeValue> updatedItems = listHandler.getUpdatedItems();
		List<AttributeValue> deletedItems = listHandler.getDeletedItems();

		Attribute id = attribute;
		if (newItems != null) {
			newItems.forEach(item -> {
				item.setAttribute(id);
				attributeValueService.save(item, locale);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				item.setAttribute(id);
				attributeValueService.save(item, locale);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				attributeValueService.deleteValue(item, locale);
			});
		}
	}


	@Transactional
	public void deleteAttribute(Attribute attribute, Locale locale) {
		log.debug("Try to  delete attribute : {}", attribute);
		try {
			if (attribute == null || attribute.getId() == null) {
				String message = messageSource.getMessage("unable.to.delete.null.attribute.value",
						new Object[] { attribute }, locale);
				throw new BcephalException( message);
			}
			attribute.getValueListChangeHandler().getItems().forEach(item -> {
				attributeValueService.deleteValue(item, locale);
			});
			attribute.getValueListChangeHandler().getDeletedItems().forEach(item -> {
				attributeValueService.deleteValue(item, locale);
			});

			attribute.getChildrenListChangeHandler().getItems().forEach(item -> {
				deleteAttribute(item, locale);
			});
			attribute.getChildrenListChangeHandler().getDeletedItems().forEach(item -> {
				deleteAttribute(item, locale);
			});

			if (attribute.getId() != null) {
				deleteById(attribute.getId());
				universeGenerator.dropUniverseColumn(attribute);
			}
			log.debug("attribute successfully to delete : {} ", attribute);
			return;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete attribute value : {}", attribute, e);
			String message = messageSource.getMessage("unable.to.delete.attribute.value", new Object[] { attribute },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public List<Attribute> getAttributeByEntityId(Long id, Locale locale) {
		log.debug("Try to  get attribute by entity id : {}", id);
		if (id == null) {
			String message = messageSource.getMessage("unable.to.find.model.attribute.by.null.id", new Object[] { id },
					locale);
			throw new BcephalException(message);
		}
		Entity entity = new Entity();
		entity.setId(id);
		return attributeRepository.findByEntity(entity);
	}

	public BrowserDataPage<String> searchAttributeValues(BrowserDataFilter filter, Locale locale) throws Exception {
		Optional<Attribute> result = attributeRepository.findById(filter.getGroupId());
		if (result.isPresent()) {
			Attribute attribute = result.get();
			if (attribute.isDeclared()) {
				BrowserDataPage<String> datas = new BrowserDataPage<String>();
				datas.setPageCount(1);
				datas.setCurrentPage(1);
				datas.setPageFirstItem(1);
				List<AttributeValue> values = attribute.getValues();
				if (values != null && !values.isEmpty()) {
					for (AttributeValue attributeValue : values) {
						datas.getItems().add(attributeValue.getName());
						datas.setTotalItemCount(datas.getTotalItemCount() + 1);
					}
				}
				datas.setPageLastItem(datas.getPageFirstItem() + datas.getTotalItemCount() - 1);
				return datas;
			} else {
				return getAttributeValues(filter, locale);
			}
		} else {
			throw new BcephalException("Attribute not found. ID : {}", filter.getGroupId());
		}
	}
	
private String build(ColumnFilter filter, String col) {
		if(filter == null) {
			return null;
		}
		
		String  criteria = filter.getValue();
		String  sql = " AND UPPER(" + col + ") %s '%s'";
		String operator = "LIKE";
		boolean isNull = false;
		if (filter != null) {
			GridFilterOperator gridFilterOperator = new GridFilterOperator();
			if (gridFilterOperator.isEquals(filter.getOperation())) {
				operator = "=";
			} else if (gridFilterOperator.isGreater(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isGreaterOrEquals(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isLess(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isLessOrEquals(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isNotEquals(filter.getOperation())) {
				operator = "!=";
			} else if (gridFilterOperator.isStartsWith(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isEndsWith(filter.getOperation())) {
				criteria = "%" + criteria ;
			} else if (gridFilterOperator.isContains(filter.getOperation())) {
				criteria = "%" + criteria + "%";
			} else if (gridFilterOperator.isNotContains(filter.getOperation())) {
				criteria = "%" + criteria + "%";
				operator = " NOT LIKE";
			} else if (gridFilterOperator.isNotNullOrEmpty(filter.getOperation())) {
				isNull = true;
				operator = " IS NOT NULL OR " + col + " !=''";
			} else if (gridFilterOperator.isNullOrEmpty(filter.getOperation())) {
				isNull = true;
				operator = " IS NULL OR " + col + " =''";
			}
		}
		
		if(!isNull) {
			if(StringUtils.hasText(criteria)) {
				criteria = criteria.toUpperCase();
			}
			return String.format(sql, operator,criteria);
		}else {
			return String.format(" AND " + col + " %s ", operator);
		}
	}

	private BrowserDataPage<String> getAttributeValues(BrowserDataFilter filter, java.util.Locale locale) {

		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<String> page = new BrowserDataPage<String>();
		page.setPageSize(filter.getPageSize());
		Integer count = 0;

		String col = new Attribute(filter.getGroupId()).getUniverseTableColumnName();
		String sql = "SELECT distinct " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME + " WHERE " + col
				+ " IS NOT NULL AND " + col + " != ''";
		if (StringUtils.hasText(filter.getCriteria())) {
			sql += " AND UPPER(" + col + ") LIKE '" + filter.getCriteria().toUpperCase() + "%'";
		}

		if(filter.getColumnFilters() != null) {
			String v = build(filter.getColumnFilters(), col);
			if(StringUtils.hasText(v)) {
				sql += v;
			}
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
		List<String> values = query.getResultList();
		page.setPageLastItem(page.getPageFirstItem() + values.size() - 1);
		page.setPageSize(values.size());
		page.setItems(values);
		log.debug("Row found : {}", values.size());

		return page;
	}
	
	

}
