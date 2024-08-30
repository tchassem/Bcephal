package com.moriset.bcephal.planification.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.service.DataSourcableService;
import com.moriset.bcephal.planification.domain.script.Script;
import com.moriset.bcephal.planification.domain.script.ScriptBrowserData;
import com.moriset.bcephal.planification.repository.ScriptRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.FunctionalityCodes;

@Service
public class ScriptService extends DataSourcableService<Script, ScriptBrowserData>{

	@Autowired
	private ScriptRepository scriptRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;

		
	@Override
	public ScriptRepository getRepository() {
		return scriptRepository;
	}

	@Override
	protected Script getNewItem() {
		Script script = new Script();		
		String baseName = "Script ";
		int i = 1;
		script.setName(baseName + i);
		while (getByName(script.getName()) != null) {
			i++;
			script.setName(baseName + i);
		}
		return script;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SCRIPT;
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
	protected ScriptBrowserData getNewBrowserData(Script item) {
		return new ScriptBrowserData(item);
	}
	
	@Override
	protected Specification<Script> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Script> qBuilder = new RequestQueryBuilder<Script>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));
			qBuilder.addNoTInObjectId(hidedObjectIds);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
				filter.getColumnFilters().getItems().forEach(filte -> {
					build(filte);
				});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}
	
}
