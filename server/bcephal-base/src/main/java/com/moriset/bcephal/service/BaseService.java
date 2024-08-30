/**
 * 
 */
package com.moriset.bcephal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author MORISET-004
 *
 */
public interface BaseService<P extends IPersistent, B> {

	public  Specification<P> getBrowserDatasSpecification(BrowserDataFilter filter, java.util.Locale locale);
	
	default public  Sort  getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null && filter.getColumnFilters().getOperation().equals(BrowserDataFilter.SortBy)) {
    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
    			return Sort.by(Order.desc(filter.getColumnFilters().getName()));
    		}else {
    			return Sort.by(Order.asc(filter.getColumnFilters().getName()));
    		}
    	}
		return Sort.by(Order.asc("id"));
	}

	public  PersistentRepository<P> getRepository();

	default public BrowserDataPage<B> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<B> page = new BrowserDataPage<B>();
		page.setPageSize(filter.getPageSize());
		Specification<P> specification = getBrowserDatasSpecification(filter, locale);
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

	default public List<B> buildBrowserData(List<P> contents) {
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

	default public Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

  public B getNewBrowserData(P item);

}
