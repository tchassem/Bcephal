/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
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
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.reconciliation.domain.ReconciliationActions;
import com.moriset.bcephal.reconciliation.domain.ReconciliationLog;
import com.moriset.bcephal.reconciliation.repository.ReconciliationLogRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;

import jakarta.persistence.criteria.Predicate;

/**
 * @author MORISET-004
 *
 */
@Service
public class ReconciliationLogService extends PersistentService<ReconciliationLog, ReconciliationLog> {

	@Autowired
	ReconciliationLogRepository reconciliationLogRepository;
	
	@Override
	public ReconciliationLogRepository getRepository() {
		return reconciliationLogRepository;
	}
	
	public BrowserDataPage<ReconciliationLog> search(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<ReconciliationLog> page = new BrowserDataPage<ReconciliationLog>();
		page.setPageSize(filter.getPageSize());
		Specification<ReconciliationLog> specification = getBrowserDatasSpecification(filter, locale);
		if(filter.isShowAll()) {
			List<ReconciliationLog> items = getRepository().findAll(specification, Sort.by(Order.desc("id")));
			page.setItems(items);
			
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
			Page<ReconciliationLog> oPage = getRepository().findAll(specification, getPageable(filter, Sort.by(Order.desc("id"))));
			page.setItems(oPage.getContent());
			
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
		Pageable paging = PageRequest.of(filter.getPage()-1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage()-1, filter.getPageSize(), sort);
		}
		return paging;
	}
	
	
	protected Specification<ReconciliationLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<ReconciliationLog> qBuilder = new RequestQueryBuilder<ReconciliationLog>(root, query, cb);
		    qBuilder.select(ReconciliationLog.class);	
		    if (filter != null && filter.getGroupId() != null) {
		    	Predicate predicate = qBuilder.getCriteriaBuilder().equal(root.get("reconciliation"), filter.getGroupId());
		    	qBuilder.add(predicate);
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
	
	private void build(ColumnFilter columnFilter) {
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
		} else if ("RecoType".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("recoType");
		} else if ("Username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
		} else if ("Action".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("action");
			columnFilter.setType(ReconciliationActions.class);
		} else if ("ReconciliationNbr".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("reconciliationNbr");
			columnFilter.setType(Long.class);
		} else if ("LeftAmount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("leftAmount");
			columnFilter.setType(BigDecimal.class);
		} else if ("RigthAmount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("rigthAmount");
			columnFilter.setType(BigDecimal.class);
		} else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} else if ("BalanceAmount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("balanceAmount");
			columnFilter.setType(BigDecimal.class);
		} else if ("WriteoffAmount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("writeoffAmount");
			columnFilter.setType(BigDecimal.class);
		}
	}
	
	
}
