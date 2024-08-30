package com.moriset.bcephal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BGroup;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.RightEditorData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.repository.BGroupRepository;
import com.moriset.bcephal.repository.DocumentRepository;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class MainObjectService<P extends MainObject, B> extends PersistentService<P, B> {

	public abstract MainObjectRepository<P> getRepository();

	@Autowired
	InitiationService initiationService;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	BGroupRepository groupRepository;
	
	public BGroup getDefaultGroup() {
		try {
			List<BGroup> groups = groupRepository.findByName(BGroup.DEFAULT_GROUP_NAME);
			return groups.size() > 0 ? groups.get(0) : null;
		} catch (NoResultException e) {
			return null;
		}catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while retrieving default group", e);
		}
		return null;
	}
	
	public int getDocumentCount(Long subjectId) {
		return documentRepository.countBySubjectTypeAndSubjectId(getFunctionalityCode(), subjectId);
	}
		
	/**
	 * get Main Object by name
	 */
	public P getByName(String name) {
		List<P> objects = getAllByName(name);
		return objects.size() > 0 ? objects.get(0) : null;
	}
	
	public List<P> getAllByName(String name) {
		log.debug("Try to get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByName(name);
	}

	public EditorData<P> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<P> data = new EditorData<>();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}
		if(filter.getDataSourceType() == null 
				|| filter.getDataSourceType().isUniverse() 
				|| filter.getDataSourceType().isInputGrid()
				|| filter.getDataSourceType().isReportGrid()) {
			initEditorData(data, session, locale);
		}
		if(data.getItem() != null && data.getItem().getGroup() == null) {
			data.getItem().setGroup(getDefaultGroup());
		}		
		return data;
	}
	
	@Override
	public P getById(Long id) {
		P item = super.getById(id);
		if(item != null) {
			item.setDocumentCount(getDocumentCount(id));
		}
		return item;
	}
	
	protected void initEditorData(EditorData<P> data, HttpSession session, Locale locale) throws Exception {
		data.setModels(initiationService.getModels(session, locale));
		data.setPeriods(initiationService.getPeriods(session, locale));
		data.setMeasures(initiationService.getMeasures(session, locale));
		data.setCalculatedMeasures(initiationService.getCalculatedMeasures(DataSourceType.UNIVERSE, null, session, locale));
		data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
		data.setSpots(initiationService.getSpotsAsNameable(session, locale));
	}
	
	public RightEditorData<P> getRightLowLevelEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		RightEditorData<P> data = new RightEditorData<>();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}
		initEditorData(data, session, locale);
		data.setLowRightLevels(Arrays.asList("NONE", "VIEW","EXPORT","RUN","LOAD","CLEAR","VALIDATE","RESET","ACTION","EDIT","CREATE","ALL"));
		return data;
	}

	protected abstract P getNewItem();
		

	public Long copy(Long id, Locale locale) {
		log.debug("Try to copy entity : {}", id);
		P item = getById(id);
		if(item != null) {
			P copy = getCopy(item);
			P result = save(copy, locale);
			return result.getId();
		}
		return null;
	}
	
	public Long copy(Long id, String newName, Locale locale) {
		log.debug("Try to copy entity : {} as {}", id, newName);
		List<P> objects = getAllByName(newName);
		if(objects.size() > 0) {
			throw new BcephalException("Duplicate name : " + newName);
		}
		P item = getById(id);
		if(item != null) {
			P copy = getCopy(item);
			copy.setName(newName);
			P result = save(copy, locale);
			return result.getId();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected P getCopy(P item) {
		P copy = (P)item.copy();
		return copy;
	}
	
	public List<Long> copy(List<Long> ids, Locale locale) {
		List<Long> result = new ArrayList<>();
		List<P> items = new ArrayList<>();
		for(Long id : ids) {
			P item = getById(id);
			if(item != null) {
				items.add(getCopy(item));
			}
		}
		items = save(items, locale);
		for(P item : items) {
			result.add(item.getId());
		}
		return result;
	}

	/**
	 * save entity
	 * 
	 * @param entity
	 * @param locale
	 * @return
	 */
	public P save(P entity, Locale locale) {
		log.debug("Try to Save entity : {}", entity);
		if (getRepository() == null) {
			return entity;
		}
		try {
			if (entity == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { entity },
						locale);
				throw new BcephalException(message);
			}
			validateBeforeSave(entity, locale);
			entity = getRepository().save(entity);
			log.debug("entity successfully saved : {} ", entity);
			return entity;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", entity, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { entity }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	protected void validateBeforeSave(P entity, Locale locale) {
		List<P> objects = getAllByName(entity.getName());
		for(P obj : objects) {
			if(!obj.getId().equals(entity.getId())) {
				String message = "Duplicate name : " + entity.getName();//messageSource.getMessage("duplicate.name", new Object[] { entity.getName() },
						//locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
		}
	}

	/**
	 * save entities
	 * 
	 * @param entities
	 * @param locale
	 * @return
	 */
	public List<P> save(List<P> entities, Locale locale) {
		log.debug("Try to Save entities : {}", entities);
		try {
			if (getRepository() == null || entities == null) {
				String message = messageSource.getMessage("unable.to.save.null.entities", new Object[] { entities },
						locale);
				throw new BcephalException(message);
			}
			entities.forEach(item -> {
				item = save(item, locale);
			});
			log.debug("entities successfully saved : {}", entities);
			return entities;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save entities : {}", entities, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { entities }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public boolean canDelete(Long id, Locale locale) {
		return true;
	}

	@Transactional
	public void delete(List<Long> ids, Locale locale) {
		log.debug("Try to delete : {} entities", ids.size());
		if (getRepository() == null) {
			return;
		}
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(message);
			}
			deleteByIds(ids);
			log.debug("{} entities successfully deleted", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete entities : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	protected abstract String getBrowserFunctionalityCode();
	
	public  String getFunctionalityCode() {
		return getBrowserFunctionalityCode();
	}
	
	public String getFunctionalityCode(Long entityId) {
		return getBrowserFunctionalityCode();
	}
	public abstract void saveUserSessionLog(String username,Long clientId,String projectCode,String usersession, Long objectId, String functionalityCode, String rightLevel,Long profileId);
	
	protected abstract List<Long> getHidedObjectId(Long profileId, String functionalityCode,String projectCode);
	
	public BrowserDataPage<B> search(BrowserDataFilter filter, java.util.Locale locale, Long profileId,String projectCode) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<B> page = new BrowserDataPage<B>();
		page.setPageSize(filter.getPageSize());
		
		String functionalityCode = getBrowserFunctionalityCode();
		List<Long> hidedObjectIds = new ArrayList<>(0);
		if(StringUtils.hasText(functionalityCode)) {
			hidedObjectIds =  getHidedObjectId(profileId,functionalityCode,projectCode);
		}
		
		
		Specification<P> specification = getBrowserDatasSpecification(filter, locale, hidedObjectIds);
		if (filter.isShowAll()) {
			List<P> items = getRepository().findAll(specification, getBrowserDatasSort(filter, locale));
			page.setItems(buildBrowserData(items));

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<P> oPage = getRepository().findAll(specification, getPageable(filter, getBrowserDatasSort(filter, locale)));
			page.setItems(buildBrowserData(oPage.getContent()));

			page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
			page.setPageCount(oPage.getTotalPages());
			page.setTotalItemCount(Long.valueOf(oPage.getTotalElements()).intValue());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		}

		return page;
	}

	

	protected List<B> buildBrowserData(List<P> contents) {
		List<B> items = new ArrayList<B>(0);
		if (contents != null) {
			contents.forEach(item -> {
				B element = getNewBrowserData(item);
				if (element != null) {
					items.add(element);
				}

			});
		}
		return items;
	}

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

	protected abstract B getNewBrowserData(P item);

	protected abstract Specification<P> getBrowserDatasSpecification(BrowserDataFilter filter, java.util.Locale locale, List<Long> hidedObjectIds);

	protected  Sort  getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {
			
			if(filter.getColumnFilters().isSortFilter()) {
	    		build(filter.getColumnFilters());
	    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
	    			
	    			if(!filter.getColumnFilters().isJoin()) {
	    				return Sort.by(Order.desc(filter.getColumnFilters().getName()));
	    			}else {
	    				String name = filter.getColumnFilters().getJoinName() + "_" + filter.getColumnFilters().getName();
	    				return Sort.by(Order.desc(name));
	    			}
	    		}else {
	    			if(!filter.getColumnFilters().isJoin()) {
	    				return Sort.by(Order.asc(filter.getColumnFilters().getName()));
	    			}else {
	    				String name = filter.getColumnFilters().getJoinName() + "_" + filter.getColumnFilters().getName();
	    				return Sort.by(Order.asc(name));
	    			}
	    		}
	    	}else {
	    		if(filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
	    			List<Order> orders = new ArrayList<Order>();
					for (ColumnFilter columnFilter : filter.getColumnFilters().getItems()) {
						if (columnFilter.isSortFilter()) {
							build(columnFilter);
							if (columnFilter.getLink() != null
									&& columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
								if (!columnFilter.isJoin()) {
									orders.add(Order.desc(columnFilter.getName()));
								} else {
									String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
									orders.add(Order.desc(name));
								}
							} else {
								if (!columnFilter.isJoin()) {
									orders.add(Order.asc(columnFilter.getName()));
								} else {
									String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
									orders.add(Order.asc(name));
								}
							}
						}
					}
					if(orders.size() > 0) {
						return Sort.by(orders);
					}
	    		}
	    	}
    	}
		return getDefaultSort();
	}
	
	protected Sort getDefaultSort() {
		return Sort.by(Order.asc("name"));
	}
	
	
	
	protected void build(ColumnFilter columnFilter) {
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
		} 
		else if ("description".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("description");
			columnFilter.setType(String.class);
		} 
		else if ("group".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
			columnFilter.setJoin(true);
			columnFilter.setJoinName("group");
		} 
		else if ("visibleInShortcut".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("visibleInShortcut");
			columnFilter.setType(Boolean.class);
		} 
		else if ("creationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} 
		else if ("modificationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("modificationDate");
			columnFilter.setType(Date.class);
		}
	}

}
