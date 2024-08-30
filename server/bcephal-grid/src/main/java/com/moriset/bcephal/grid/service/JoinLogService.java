/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.grid.domain.JoinLog;
import com.moriset.bcephal.grid.domain.JoinLogBrowserData;
import com.moriset.bcephal.grid.domain.JoinPublicationMethod;
import com.moriset.bcephal.grid.repository.JoinLogRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Service
public class JoinLogService extends MainObjectService<JoinLog, JoinLogBrowserData>{

	@Autowired
	JoinLogRepository repository;
	
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG;
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
	
	public  JoinLogRepository getRepository(){
		return repository;
	}
	
	@Override
	protected JoinLog getNewItem() {
		JoinLog join = new JoinLog();
		String baseName = "Join ";
		int i = 1;
		join.setName(baseName + i);
		while (getByName(join.getName()) != null) {
			i++;
			join.setName(baseName + i);
		}		
		return join;
	}

	@Override
	protected JoinLogBrowserData getNewBrowserData(JoinLog item) {
		return new JoinLogBrowserData(item);
	}

	@Override
	protected Specification<JoinLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<JoinLog> qBuilder = new RequestQueryBuilder<JoinLog>(root, query, cb);
			qBuilder.select(JoinLog.class);		
			if (filter != null && filter.getSubjectId() != null) {
				qBuilder.addEqualsCriteria("joinId", filter.getSubjectId());
			}
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
		if(filter.getColumnFilters() != null && (filter.getColumnFilters().isSortFilter() || filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0)) {
			return super.getBrowserDatasSort(filter, locale);
    	}
		return Sort.by(Order.desc("creationDate"));
	}

	@Override
	protected void build(ColumnFilter columnFilter) {
		super.build(columnFilter);
		if ("publicationGridName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationGridName");
			columnFilter.setType(String.class);
		} else if ("publicationNumber".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationNumber");
			columnFilter.setType(String.class);
		} else if ("publicationNbrAttributeId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationNbrAttributeId");
			columnFilter.setType(Long.class);
		} else if ("publicationNbrAttributeName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationNbrAttributeName");
			columnFilter.setType(String.class);
		} else if ("endDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Date.class);
		} else if ("status".equalsIgnoreCase(columnFilter.getName()) || "runStatus".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(RunStatus.class);
		} else if ("mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(RunModes.class);
		} else if ("user".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("user");
			columnFilter.setType(String.class);
		} else if ("rowCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("rowCount");
			columnFilter.setType(Long.class);
		} else if ("message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
			columnFilter.setType(String.class);
		} else if ("publicationMethod".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationMethod");
			columnFilter.setType(JoinPublicationMethod.class);
		} else if ("publicationGridType".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationGridType");
			columnFilter.setType(DataSourceType.class);
		}
	}

}
