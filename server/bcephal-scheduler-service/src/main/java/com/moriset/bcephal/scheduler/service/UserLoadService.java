package com.moriset.bcephal.scheduler.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.loader.domain.UserLoad;
import com.moriset.bcephal.loader.domain.UserLoadBrowserData;
import com.moriset.bcephal.loader.domain.UserLoadEditorData;
import com.moriset.bcephal.loader.domain.UserLoader;
import com.moriset.bcephal.loader.domain.UserLoaderMenu;
import com.moriset.bcephal.loader.domain.UserLoaderTreatment;
import com.moriset.bcephal.loader.repository.UserLoadLogRepository;
import com.moriset.bcephal.loader.repository.UserLoadRepository;
import com.moriset.bcephal.loader.repository.UserLoaderMenuRepository;
import com.moriset.bcephal.loader.repository.UserLoaderRepository;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.security.domain.RightLevel;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;

@Service
public class UserLoadService extends MainObjectService<UserLoad, UserLoadBrowserData> {

	@Autowired
	UserLoadRepository loadRepository;
	
	@Autowired
	UserLoaderRepository loaderRepository;
	
	
	@Autowired
	UserLoadLogRepository userLoadLogRepository;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	UserLoaderService userLoaderService;
	
	
	@Autowired
	UserLoaderMenuRepository userLoaderMenuRepository;
	
	public List<UserLoaderMenu> getActiveMenus() {
		return this.userLoaderMenuRepository.findByActive(true);
	}
	
	
	@Override
	public UserLoadEditorData getEditorData(EditorDataFilter filter,HttpSession session, Locale locale) throws Exception {
		UserLoadEditorData data = new UserLoadEditorData();
		UserLoader loader = userLoaderService.getById(filter.getSecondId());
		if(loader == null) {
			throw new BcephalException("User loader not found : {}", filter.getSecondId());
		}
		data.setUserLoader(loader);
		if(loader.getTreatment() == null) {
			if(loader.getFileLoaderId() != null) {
				loader.setTreatment(UserLoaderTreatment.LOADER);
			}else {
				loader.setTreatment(UserLoaderTreatment.REPOSITORY);
			}
		}
		
		if (filter.isNewData()) {
			data.setItem(getNewItem());			
			data.getItem().setLoaderId(filter.getSecondId());
		} 
		else {		
			data.setItem(getById(filter.getId()));
		}
		if(data.getItem().getTreatment() == null) {
			data.getItem().setTreatment(loader.getTreatment());
		}
		data.getItem().setDescription(loader.getDescription());
		return data;
	}
	
	@Override
	public UserLoad getById(Long id) {
		UserLoad item = super.getById(id);
		if(item != null) {
			item.setLogs(userLoadLogRepository.findAllByLoadId(id));
		}
		return item;
	}
	
	

	@Override
	public UserLoadRepository getRepository() {
		return loadRepository;
	}

	@Override
	protected UserLoad getNewItem() {
		UserLoad load = new UserLoad();
		String baseName = "Load ";
		int i = 1;
		load.setName(baseName + i);
		while (getByName(load.getName()) != null) {
			i++;
			load.setName(baseName + i);
		}
		return load;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_USER_LOAD;
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return new ArrayList<>();
	}

	@Override
	protected UserLoadBrowserData getNewBrowserData(UserLoad item) {
		return new UserLoadBrowserData(item);
	}

	@Override
	protected Specification<UserLoad> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<UserLoad> qBuilder = new RequestQueryBuilder<UserLoad>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter != null && filter.getSubjectId() != null) {
				qBuilder.addEquals("loaderId", filter.getSubjectId());
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
		if ("loaderId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("loaderId");
			columnFilter.setType(Long.class);
		} 
		else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} 
		else if ("treatment".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("treatment");
			columnFilter.setType(UserLoaderTreatment.class);
		} 
		else if ("mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(RunModes.class);
		} 
		else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(RunStatus.class);
		} 
		else if ("fileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("fileCount");
			columnFilter.setType(Integer.class);
		} 
		else if ("emptyFileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("emptyFileCount");
			columnFilter.setType(Integer.class);
		} 
		else if ("errorFileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("errorFileCount");
			columnFilter.setType(Integer.class);
		} 
		else if ("loadedFileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("loadedFileCount");
			columnFilter.setType(Integer.class);
		} 
		else if ("error".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("error");
			columnFilter.setType(Boolean.class);
		} 
		else if ("message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
			columnFilter.setType(Integer.class);
		} 
		else if ("startDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("startDate");
			columnFilter.setType(Date.class);
		} 
		else if ("endDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Date.class);
		}
	}
	
	@Override
	protected Sort getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {
			return super.getBrowserDatasSort(filter, locale);
    	}
		return Sort.by(Order.desc("id"));
	}
	
	
	public List<ClientFunctionality> getFunctionalityRight(Long clientId, String projectCode){
		List<ClientFunctionality> items = new ArrayList<>(0);
		List<UserLoader>  userLoaders = loaderRepository.findAll();
		for (UserLoader userLoader : userLoaders) {
			addModel(items, userLoader, clientId, projectCode);
		}
		return items;
	}

	private void addModel(List<ClientFunctionality> items, UserLoader userLoader,Long clientId, String projectCode) {
		ClientFunctionality f = new ClientFunctionality();
		f.setCode("USER_LOAD_" + clientId + "_" + projectCode + "_" + userLoader.getId());
		f.setName(userLoader.getName());
		f.setClientId(clientId);
		f.setActive(true);
		f.setPosition(items.size());
		f.setActions(Arrays.asList(RightLevel.values()));
		f.setLowLevelActions(Arrays.asList(RightLevel.values()));
		items.add(f);
	}
	
}
