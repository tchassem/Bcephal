/**
 * 
 */
package com.moriset.bcephal.billing.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.BillingRunOutcomeBrowserData;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.repository.BillingRunOutcomeRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.FunctionalityCodes;

/**
 * @author MORISET-6
 *
 */
@Service
public class BillingRunOutcomeService extends MainObjectService<BillingRunOutcome, BillingRunOutcomeBrowserData> {

	@Autowired
	BillingRunOutcomeRepository billingRunOutcomeRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_FILE_LOADER;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
	
	
	@Override
	public BillingRunOutcomeRepository getRepository() {
		return billingRunOutcomeRepository;
	}

	@Override
	protected BillingRunOutcome getNewItem() {
		BillingRunOutcome billingRunOutcome = new BillingRunOutcome();
		String baseName = "BillingOutcome ";
		int i = 1;
		billingRunOutcome.setName(baseName + i);
		while (getByName(billingRunOutcome.getName()) != null) {
			i++;
			billingRunOutcome.setName(baseName + i);
		}
		return billingRunOutcome;
	}

	@Override
	protected BillingRunOutcomeBrowserData getNewBrowserData(BillingRunOutcome item) {
		return new BillingRunOutcomeBrowserData(item);
	}

	@Override
	protected Specification<BillingRunOutcome> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<BillingRunOutcome> qBuilder = new RequestQueryBuilder<BillingRunOutcome>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));
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
	
	@Override
	protected Sort getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null && filter.getColumnFilters().getOperation().equals(BrowserDataFilter.SortBy)) {
    		build(filter.getColumnFilters());
    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
    			return Sort.by(Order.desc(filter.getColumnFilters().getName()));
    		}else {
    			return Sort.by(Order.asc(filter.getColumnFilters().getName()));
    		}
    	}
		return Sort.by(Order.desc("id"));
	}
	@Override
	protected void build(ColumnFilter columnFilter) {
		super.build(columnFilter);
		if ("runNumber".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("runNumber");
			columnFilter.setType(String.class);
		} 
		else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(InvoiceStatus.class);
		} 
		else if ("mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(RunModes.class);
		} 
		else if ("Username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} 
		else if ("invoiceCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("invoiceCount");
			columnFilter.setType(Long.class);
		} 
		else if ("invoiceAmount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("invoiceAmount");
			columnFilter.setType(BigDecimal.class);
		} 
		else if ("creditNoteCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creditNoteCount");
			columnFilter.setType(Long.class);
		} 
		else if ("creditNoteAmount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creditNoteAmount");
			columnFilter.setType(BigDecimal.class);
		} 
		else if ("periodFrom".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("periodFrom");
			columnFilter.setType(Date.class);
		} 
		else if ("periodTo".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("periodTo");
			columnFilter.setType(Date.class);
		} 
	}
	
	@Override
	protected void validateBeforeSave(BillingRunOutcome entity, Locale locale) {
		
	}


}
