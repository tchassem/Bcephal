/**
 * 
 */
package com.moriset.bcephal.billing.service;


import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillingModelLog;
import com.moriset.bcephal.billing.repository.BillingModelLogRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.repository.MainObjectRepository;
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
public class BillingModelLogService extends MainObjectService<BillingModelLog, BrowserData> {

	@Autowired
	BillingModelLogRepository billingModelLogRepository;
	
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
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel,profileId);
	}
	
	@Override
	public MainObjectRepository<BillingModelLog> getRepository() {
		// TODO Auto-generated method stub
		return (MainObjectRepository<BillingModelLog>) billingModelLogRepository;
	}

	@Override
	protected BillingModelLog getNewItem() {
		// TODO Auto-generated method stub
		BillingModelLog billingModelLog = new BillingModelLog();
		String baseName = "BillingModelLog ";
		int i = 1;
		billingModelLog.setName(baseName + i);
		while(getByName(billingModelLog.getName()) != null) {
			i++;
			billingModelLog.setName(baseName + i);
		}
		return billingModelLog;
	}

	@Override
	protected BrowserData getNewBrowserData(BillingModelLog item) {
		// TODO Auto-generated method stub
		return new BrowserData(item);
	}

	@Override
	protected Specification<BillingModelLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		// TODO Auto-generated method stub
		return (root, query, cb) -> {
			RequestQueryBuilder<BillingModelLog> qBuilder = new RequestQueryBuilder<BillingModelLog>(root, query, cb);
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
	
	protected void build(ColumnFilter columnFilter) {
		
		if ("BillingName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("billingName");
			columnFilter.setType(String.class);
		} else if ("BillingTypeId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("billingTypeId");
			columnFilter.setType(Long.class);
		} else if ("BillingTypeName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("billingTypeName");
			columnFilter.setType(Long.class);
		} else if ("EndDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Timestamp.class);
		} else if ("Status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(RunStatus.class);
		} else if ("Mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(RunModes.class);
		} else if ("Username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("EventCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("eventCount");
			columnFilter.setType(Long.class);
		} else if ("PeriodCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("periodCount");
			columnFilter.setType(Long.class);
		} else if ("ClientCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientCount");
			columnFilter.setType(Long.class);
		} else if ("GroupCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("groupCount");
			columnFilter.setType(Long.class);
		} else if ("CategoryCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("categoryCount");
			columnFilter.setType(Long.class);
		} else if ("InvoiceCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("invoiceCount");
			columnFilter.setType(Long.class);
		} 
		super.build(columnFilter);
	}


}
