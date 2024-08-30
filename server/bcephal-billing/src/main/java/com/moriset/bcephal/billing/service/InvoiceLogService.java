package com.moriset.bcephal.billing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.InvoiceLog;
import com.moriset.bcephal.billing.domain.InvoiceLogBrowserData;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.repository.InvoiceLogRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;

@Service
public class InvoiceLogService extends PersistentService<InvoiceLog, InvoiceLogBrowserData> {

	@Autowired
	InvoiceLogRepository invoiceLogRepository;
	
	@Override
	public InvoiceLogRepository getRepository() {
		return invoiceLogRepository;
	}
	
	public void deleteByInvoice(Long invoiceId) {
		getRepository().deleteByInvoice(invoiceId);
	}

	public BrowserDataPage<InvoiceLogBrowserData> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<InvoiceLogBrowserData> page = new BrowserDataPage<InvoiceLogBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<InvoiceLog> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<InvoiceLog> items = getRepository().findAll(specification, Sort.by(Order.asc("id")));
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
			Page<InvoiceLog> oPage = getRepository().findAll(specification,
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

	public Specification<InvoiceLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<InvoiceLog> qBuilder = new RequestQueryBuilder<InvoiceLog>(root, query, cb);
			qBuilder.select(BrowserData.class);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("file", filter.getCriteria());
				qBuilder.addLikeCriteria("username", filter.getCriteria());
				qBuilder.addLikeCriteria("status", filter.getCriteria());
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
		if ("projectCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectCode");
			columnFilter.setType(String.class);
		} else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("file".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("file");
			columnFilter.setType(String.class);
		} else if ("amountWithoutVatBefore".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("amountWithoutVatBefore");
			columnFilter.setType(String.class);
		} else if ("totalAmountBefore".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("totalAmountBefore");
			columnFilter.setType(String.class);
		} else if ("Status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(InvoiceStatus.class);
		}
	}
	
	protected List<InvoiceLogBrowserData> buildBrowserData(List<InvoiceLog> contents) {
		List<InvoiceLogBrowserData> items = new ArrayList<InvoiceLogBrowserData>(0);
		if (contents != null) {
			contents.forEach(item -> {
				InvoiceLogBrowserData element = getNewBrowserData(item);
				if (element != null) {
					items.add(element);
				}

			});
		}
		return items;
	}

	public InvoiceLogBrowserData getNewBrowserData(InvoiceLog item) {
		return new InvoiceLogBrowserData(item);
	}

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

}
