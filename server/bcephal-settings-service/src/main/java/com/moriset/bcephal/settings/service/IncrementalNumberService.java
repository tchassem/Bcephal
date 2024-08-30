/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.ArrayList;
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
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.parameter.IncrementalNumberVariables;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.settings.domain.IncrementalNumberEditorData;
import com.moriset.bcephal.utils.BcephalException;

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
public class IncrementalNumberService extends PersistentService<IncrementalNumber, BrowserData> {

	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;

	@Override
	public IncrementalNumberRepository getRepository() {
		return incrementalNumberRepository;
	}

	public EditorData<IncrementalNumber> getEditorData(EditorDataFilter filter, String sessionId, Locale locale) {
		IncrementalNumberEditorData data = new IncrementalNumberEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}
		data.setVariables(new IncrementalNumberVariables().getAll());
		return data;
	}

	protected IncrementalNumber getNewItem() {
		IncrementalNumber sequence = new IncrementalNumber();
		String baseName = "sequence ";
		int i = 1;
		sequence.setName(baseName + i);
		while (getByName(sequence.getName()) != null) {
			i++;
			sequence.setName(baseName + i);
		}
		return sequence;
	}

	protected IncrementalNumber getByName(String name) {
		log.debug("Try to  get IncrementalNumber by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByName(name);
	}

	public IncrementalNumber save(IncrementalNumber incrementalNumber, Locale locale) {
		log.debug("Call of save incrementalNumber : {}", incrementalNumber);
		try {
			if (incrementalNumber == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.incremental.number",
						new Object[] { incrementalNumber }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}

			incrementalNumber = getRepository().save(incrementalNumber);
			log.debug("incrementalNumber : {}", incrementalNumber);
			return incrementalNumber;
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save incrementalNumber : {}", incrementalNumber, e);
			String message = getMessageSource().getMessage("unable.to.save.incremental.number",
					new Object[] { incrementalNumber }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public BrowserDataPage<BrowserData> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<BrowserData> page = new BrowserDataPage<BrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<IncrementalNumber> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<IncrementalNumber> items = getRepository().findAll(specification, Sort.by(Order.asc("id")));
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
			Page<IncrementalNumber> oPage = getRepository().findAll(specification,
					getPageable(filter, Sort.by(Order.asc("id"))));
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

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

	protected Specification<IncrementalNumber> getBrowserDatasSpecification(BrowserDataFilter filter,
			java.util.Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<IncrementalNumber> qBuilder = new RequestQueryBuilder<IncrementalNumber>(root, query,
					cb);
//			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("visibleInShortcut"),
//					root.get("creationDate"), root.get("modificationDate"));
			
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}
	
	
	protected void build(ColumnFilter columnFilter) {
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
		} else if ("visibleInShortcut".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("visibleInShortcut");
			columnFilter.setType(Boolean.class);
		} else if ("enabled".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("enabled");
			columnFilter.setType(Boolean.class);
		} else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} else if ("ModificationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("modificationDate");
			columnFilter.setType(Date.class);
		} else if ("initialValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("initialValue");
			columnFilter.setType(Long.class);
		} else if ("incrementValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("incrementValue");
			columnFilter.setType(Long.class);
		} else if ("minimumValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("minimumValue");
			columnFilter.setType(Long.class);
		} else if ("maximumValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("maximumValue");
			columnFilter.setType(Long.class);
		}
		else if ("currentValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("currentValue");
			columnFilter.setType(Long.class);
		}
		else if ("cycle".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("cycle");
			columnFilter.setType(Boolean.class);
		}
		else if ("size".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("size");
			columnFilter.setType(Integer.class);
		}
		else if ("prefix".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("prefix");
			columnFilter.setType(String.class);
		}
		else if ("suffix".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("suffix");
			columnFilter.setType(String.class);
		}
	}

	protected List<BrowserData> buildBrowserData(List<IncrementalNumber> contents) {
		List<BrowserData> items = new ArrayList<BrowserData>(0);
		if (contents != null) {
			contents.forEach(item -> {
				BrowserData element = getNewBrowserData(item);
				if (element != null) {
					items.add(element);
				}
			});
		}
		return items;
	}

	protected IncrementalNumberBrowserData getNewBrowserData(IncrementalNumber item) {
		return new IncrementalNumberBrowserData(item);
	}

}
