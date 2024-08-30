package com.moriset.bcephal.scheduler.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.UserLoader;
import com.moriset.bcephal.loader.domain.UserLoaderBrowserData;
import com.moriset.bcephal.loader.domain.UserLoaderCondition;
import com.moriset.bcephal.loader.domain.UserLoaderController;
import com.moriset.bcephal.loader.domain.UserLoaderEditorData;
import com.moriset.bcephal.loader.domain.UserLoaderMenu;
import com.moriset.bcephal.loader.domain.UserLoaderScheduler;
import com.moriset.bcephal.loader.domain.UserLoaderTreatment;
import com.moriset.bcephal.loader.repository.FileLoaderRepository;
import com.moriset.bcephal.loader.repository.UserLoaderConditionRepository;
import com.moriset.bcephal.loader.repository.UserLoaderControllerRepository;
import com.moriset.bcephal.loader.repository.UserLoaderMenuRepository;
import com.moriset.bcephal.loader.repository.UserLoaderRepository;
import com.moriset.bcephal.loader.repository.UserLoaderSchedulerRepository;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserLoaderService extends MainObjectService<UserLoader, UserLoaderBrowserData> {

	@Autowired
	UserLoaderRepository loaderRepository;
	
	@Autowired
	UserLoaderMenuRepository menuRepository;
	
	@Autowired
	UserLoaderConditionRepository conditionRepository;
	
	@Autowired
	UserLoaderSchedulerRepository schedulerRepository;
	
	@Autowired
	UserLoaderControllerRepository controllerRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	

	@Autowired
	SchedulerPlannerRepository schedulerService;
	
	@Autowired
	FileLoaderRepository fileLoaderRepository;
	
	@Autowired
	SpotRepository spotRepository;
	
	
	
	@Override
	public UserLoaderEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		UserLoaderEditorData data = new UserLoaderEditorData();				
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} 
		else {		
			data.setItem(getById(filter.getId()));
		}
		
		data.setSchedulers(schedulerService.findAllAsNameables());
		data.setFileLoaders(fileLoaderRepository.findAllAsNameables());
		data.setSpots(spotRepository.findAllAsNameables());
		return data;
	}
	
	@Override
	public UserLoader getById(Long id) {
		UserLoader item = super.getById(id);
		if(item != null && item.getFileLoaderId() != null) {
			Optional<FileLoader> load = fileLoaderRepository.findById(item.getFileLoaderId());
			item.setFileLoaderName(load.get().getName());
		}
		return item;
	}
	
	
	@Override
	@Transactional
	public UserLoader save(UserLoader loader, Locale locale) {
		log.debug("Try to save UserLoader : {}", loader);
		try {
			if (loader == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.user.loader",
						new Object[] { loader }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(loader.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.user.loader.with.empty.name",
						new String[] { loader.getName() }, locale);
				throw new BcephalException(message);
			}

			ListChangeHandler<UserLoaderCondition> conditions = loader.getConditionListChangeHandler();
			ListChangeHandler<UserLoaderScheduler> schedulers = loader.getSchedulerListChangeHandler();
			ListChangeHandler<UserLoaderController> controllers = loader.getControllerListChangeHandler();

			if (loader.getMenu() == null) {
				loader.setMenu(new UserLoaderMenu());
				loader.getMenu().initialize(loader.getName());
			}
			
			loader.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(loader, locale);
			if(loader.getMenu() != null) {
				loader.getMenu().setLoaderId(loader.getId());
				if (!StringUtils.hasText(loader.getMenu().getName())) {
					loader.getMenu().initializeName(loader.getName());
				}
				loader.setMenu(menuRepository.save(loader.getMenu()));
			}
			loader = loaderRepository.save(loader);
			UserLoader id = loader;
			
			if (loader.getMenu() != null) {
				loader.getMenu().setLoaderId(id.getId());
				loader.setMenu(menuRepository.save(loader.getMenu()));
			}
			
			conditions.getNewItems().forEach(item -> {
				log.trace("Try to save condition : {}", item);
				item.setLoader(id);
				conditionRepository.save(item);
				log.trace("condition saved : {}", item.getId());
			});
			conditions.getUpdatedItems().forEach(item -> {
				log.trace("Try to save condition : {}", item);
				item.setLoader(id);
				conditionRepository.save(item);
				log.trace("condition saved : {}", item.getId());
			});
			conditions.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete condition : {}", item);
					conditionRepository.deleteById(item.getId());
					log.trace("condition deleted : {}", item.getId());
				}
			});
			
			schedulers.getNewItems().forEach( item -> {
				log.trace("Try to save scheduler : {}", item);
				item.setLoader(id);
				schedulerRepository.save(item);
				log.trace("scheduler saved : {}", item.getId());
			});
			schedulers.getUpdatedItems().forEach( item -> {
				log.trace("Try to save scheduler : {}", item);
				item.setLoader(id);
				schedulerRepository.save(item);
				log.trace("scheduler saved : {}", item.getId());
			});
			schedulers.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete scheduler : {}", item);
					schedulerRepository.deleteById(item.getId());
					log.trace("scheduler deleted : {}", item.getId());
				}
			});
			
			controllers.getNewItems().forEach( item -> {
				log.trace("Try to save controller : {}", item);
				item.setLoader(id);
				controllerRepository.save(item);
				log.trace("controller saved : {}", item.getId());
			});
			controllers.getUpdatedItems().forEach( item -> {
				log.trace("Try to save controller : {}", item);
				item.setLoader(id);
				controllerRepository.save(item);
				log.trace("controller saved : {}", item.getId());
			});
			controllers.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete controller : {}", item);
					controllerRepository.deleteById(item.getId());
					log.trace("controller deleted : {}", item.getId());
				}
			});
			
			log.debug("User Loader saved : {} ", loader.getId());
			return loader;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save User Loader : {}", loader, e);
			String message = getMessageSource().getMessage("unable.to.save.user.loader", new Object[] { loader },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public void delete(UserLoader loader) {
		log.debug("Try to delete UserLoader : {}", loader);
		if (loader == null || loader.getId() == null) {
			return;
		}

		ListChangeHandler<UserLoaderCondition> conditions = loader.getConditionListChangeHandler();
		conditions.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete condition : {}", item);
				conditionRepository.deleteById(item.getId());
				log.trace("condition deleted : {}", item.getId());
			}
		});
		conditions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete condition : {}", item);
				conditionRepository.deleteById(item.getId());
				log.trace("condition deleted : {}", item.getId());
			}
		});
		
		ListChangeHandler<UserLoaderScheduler> schedulers = loader.getSchedulerListChangeHandler();
		schedulers.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete scheduler : {}", item);
				schedulerRepository.deleteById(item.getId());
				log.trace("scheduler deleted : {}", item.getId());
			}
		});
		schedulers.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete scheduler : {}", item);
				schedulerRepository.deleteById(item.getId());
				log.trace("scheduler deleted : {}", item.getId());
			}
		});
		
		ListChangeHandler<UserLoaderController> controlles = loader.getControllerListChangeHandler();
		controlles.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete controller : {}", item);
				controllerRepository.deleteById(item.getId());
				log.trace("controller deleted : {}", item.getId());
			}
		});
		controlles.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete controller : {}", item);
				controllerRepository.deleteById(item.getId());
				log.trace("controller deleted : {}", item.getId());
			}
		});

		loaderRepository.deleteById(loader.getId());
		if(loader.getMenu() != null && loader.getMenu().getId() != null) {
			menuRepository.deleteById(loader.getMenu().getId());
		}
		log.debug("UserLoader successfully to delete : {} ", loader);
		return;
	}
	
	

	@Override
	public UserLoaderRepository getRepository() {
		return loaderRepository;
	}

	@Override
	protected UserLoader getNewItem() {
		UserLoader loader = new UserLoader();
		String baseName = "User Loader ";
		int i = 1;
		loader.setName(baseName + i);
		while (getByName(loader.getName()) != null) {
			i++;
			loader.setName(baseName + i);
		}
		return loader;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_USER_LOADER;
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	@Override
	protected UserLoaderBrowserData getNewBrowserData(UserLoader item) {
		return new UserLoaderBrowserData(item);
	}

	@Override
	protected Specification<UserLoader> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<UserLoader> qBuilder = new RequestQueryBuilder<UserLoader>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));
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
		if ("treatment".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("treatment");
			columnFilter.setType(UserLoaderTreatment.class);
		}
	}
}
