package com.moriset.bcephal.scheduler.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.scheduler.domain.Presentation;
import com.moriset.bcephal.scheduler.domain.PresentationBrowserData;
import com.moriset.bcephal.scheduler.repository.PresentationRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.FunctionalityCodes;


@Service
public class PresentationService extends MainObjectService<Presentation, PresentationBrowserData> {

	@Autowired
	PresentationRepository presentationRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	
	@Override
	public PresentationRepository getRepository() {
		return presentationRepository;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION;
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
	protected Presentation getNewItem() {
		Presentation template = new Presentation();
		String baseName = "Presentation ";
		int i = 1;
		template.setName(baseName + i);
		while(getByName(template.getName()) != null) {
			i++;
			template.setName(baseName + i);
		}
		return template;
	}
	
	@Override
	protected PresentationBrowserData getNewBrowserData(Presentation item) {
		return new PresentationBrowserData(item);
	}

	@Override
	protected Specification<Presentation> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Presentation> qBuilder = new RequestQueryBuilder<Presentation>(root, query, cb);
			qBuilder.select(Presentation.class);			
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			qBuilder.addNoTInObjectId(hidedObjectIds);
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
	protected void build(ColumnFilter columnFilter) {
		super.build(columnFilter);
		if ("operationCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("operationCode");
			columnFilter.setType(String.class);
		} 
		else if ("code".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("code");
			columnFilter.setType(String.class);
		} 
		else if ("repository".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("repository");
			columnFilter.setType(String.class);
		} 
	}
	
	@Override
	protected Sort getDefaultSort() {
		return Sort.by(Order.desc("id"));
	}
	
	
}
