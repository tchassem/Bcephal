package com.moriset.bcephal.security.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.domain.MainObject;
import com.moriset.bcephal.security.repository.MainObjectRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

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

	
	public P getById(Long id, Long clientId, String projectCode) {
		log.debug("Try to  get by id : {}", id);
		if (getRepository() == null && id != null) {
			return null;
		}
		Optional<P> item = getRepository().findById(id);
		if (item.isPresent()) {
			return item.get();
		}
		return null;
	}
	

	/**
	 * get Main Object by name
	 */
	public P getByName(String name) {
		log.debug("Try to  get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		List<P> objects = getRepository().findByName(name);
		return objects.size() > 0 ? objects.get(0) : null;
	}

	public EditorData<P> getEditorData(EditorDataFilter filter, Long clientId, String projectCode , String username, HttpSession session, Locale locale) throws Exception {
		EditorData<P> data = new EditorData<>();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId(), clientId, projectCode));
		}
		return data;
	}

	protected abstract P getNewItem();

	/**
	 * save entity
	 * 
	 * @param entity
	 * @param locale
	 * @param projectCode 
	 * @return
	 */
	public P save(P entity, Long clientId, Locale locale, String projectCode) {
		log.debug("Try to  Save entity : {}", entity);
		if (getRepository() == null) {
			return entity;
		}
		try {
			if (entity == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { entity },
						locale);
				throw new BcephalException(message);
			}
			setCreationAndModificationDates(entity);
			validateBeforeSave(entity,locale);
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
	protected void build(ColumnFilter columnFilter) {
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
		} else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} else if ("ModificationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("modificationDate");
			columnFilter.setType(Date.class);
		}
	}

	public List<P> getAllByName(String name) {
		log.debug("Try to  get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByName(name);
	}
	
	protected void validateBeforeSave(P entity, Locale locale) {
		List<P> objects = getAllByName(entity.getName());
		for(P obj : objects) {
			if(!obj.getId().equals(entity.getId())) {
				String message = messageSource.getMessage("duplicate.name", new Object[] { entity.getName() },
						locale);
				throw new BcephalException(message);
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
		log.debug("Try to  Save entities : {}", entities);
		try {
			if (getRepository() == null || entities == null) {
				String message = messageSource.getMessage("unable.to.save.null.entities", new Object[] { entities },
						locale);
				throw new BcephalException(message);
			}
			entities.forEach(item -> {
				item = save(item, locale);
			});
			log.debug("entities successfully saved : {} ", entities);
			return entities;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save entities : {}", entities, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { entities }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public void delete(List<Long> ids, Locale locale) {
		log.debug("Try to  delete : {} entities", ids.size());
		if (getRepository() == null) {
			return;
		}
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(message);
			}
			deleteByIds(ids);
			log.debug("{} entities successfully deleted ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete entities : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	protected void setCreationAndModificationDates(MainObject persistent) {
		if(persistent != null) {
			if(persistent.isPersistent()) {
				persistent.setModificationDate(new Timestamp(System.currentTimeMillis()));
			}
			else {
				persistent.setCreationDate(new Timestamp(System.currentTimeMillis()));
				persistent.setModificationDate(persistent.getCreationDate());
			}
		}
	}

	protected  Sort  getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {
			
			if(filter.getColumnFilters().isSortFilter()) {
	    		build(filter.getColumnFilters());
	    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
	    			return Sort.by(Order.desc(filter.getColumnFilters().getName()));
	    		}else {
	    			return Sort.by(Order.asc(filter.getColumnFilters().getName()));
	    		}
	    	}else {
	    		if(filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
	    			for(ColumnFilter columnFilter : filter.getColumnFilters().getItems()){
	    				if(columnFilter.isSortFilter()) {
	    		    		build(columnFilter);
	    		    		if(columnFilter.getLink() != null && columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
	    		    			return Sort.by(Order.desc(columnFilter.getName()));
	    		    		}else {
	    		    			return Sort.by(Order.asc(columnFilter.getName()));
	    		    		}
	    		    	}
	    			}
	    		}
	    	}
    	}
		return Sort.by(Order.asc("id"));
	}
	
	public BrowserDataPage<B> search(BrowserDataFilter filter, Long clientId, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<B> page = new BrowserDataPage<B>();
		page.setPageSize(filter.getPageSize());
		Specification<P> specification = getBrowserDatasSpecification(filter, clientId, locale);
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
		} 
		else {
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

	protected abstract Specification<P> getBrowserDatasSpecification(BrowserDataFilter filter, Long clientId, java.util.Locale locale);

}
