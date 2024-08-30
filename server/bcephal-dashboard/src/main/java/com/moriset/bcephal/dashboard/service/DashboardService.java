package com.moriset.bcephal.dashboard.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.dashboard.domain.Dashboard;
import com.moriset.bcephal.dashboard.domain.DashboardBrowserData;
import com.moriset.bcephal.dashboard.domain.DashboardEditorData;
import com.moriset.bcephal.dashboard.domain.DashboardItem;
import com.moriset.bcephal.dashboard.domain.DashboardItemFilter;
import com.moriset.bcephal.dashboard.domain.DashboardItemUserLoader;
import com.moriset.bcephal.dashboard.domain.DashboardItemVariable;
import com.moriset.bcephal.dashboard.domain.DashboardItemVariableReference;
import com.moriset.bcephal.dashboard.domain.DashboardItemVariableReferenceCondition;
import com.moriset.bcephal.dashboard.domain.ProfileDashboard;
import com.moriset.bcephal.dashboard.domain.ProfileDashboardEditorData;
import com.moriset.bcephal.dashboard.repository.DashboardItemFilterRepository;
import com.moriset.bcephal.dashboard.repository.DashboardItemRepository;
import com.moriset.bcephal.dashboard.repository.DashboardItemUserLoaderRepository;
import com.moriset.bcephal.dashboard.repository.DashboardItemVariableReferenceConditionRepository;
import com.moriset.bcephal.dashboard.repository.DashboardItemVariableReferenceRepository;
import com.moriset.bcephal.dashboard.repository.DashboardItemVariableRepository;
import com.moriset.bcephal.dashboard.repository.DashboardRepository;
import com.moriset.bcephal.dashboard.repository.ProfileDashboardRepository;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class DashboardService extends MainObjectService<Dashboard, DashboardBrowserData> {

	@Autowired
	DashboardRepository dashboardRepository;
	@Autowired
	DashboardItemRepository dashboardItemRepository;
	
	@Autowired
	DashboardItemFilterRepository dashboardItemFilterRepository;
	
	@Autowired
	DashboardItemVariableRepository dashboardItemVariableRepository;
	
	@Autowired
	DashboardItemVariableReferenceRepository dashboardItemVariableReferenceRepository;
	
	@Autowired
	DashboardItemVariableReferenceConditionRepository dashboardItemVariableReferenceConditionRepository;
	
	@Autowired
	DashboardItemUserLoaderRepository dashboardItemUserLoaderRepository;
	
	@Autowired
	ProfileDashboardRepository profileDashboardRepository;
	
	@Autowired
	GrilleService grilleService;

	@Autowired
	SmartGrilleRepository smartGrilleRepository;

	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.DASHBOARDING_DASHBOARD;
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
	public DashboardRepository getRepository() {
		return dashboardRepository;
	}
	
	@Override
	public DashboardEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);
		DashboardEditorData data = new DashboardEditorData(super.getEditorData(filter, session, locale));
				
		List<GrilleType> types = getSmallGridTypes();
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		List<Long> hightGridItems = getHidedObjectId(profileId, grilleService.getFunctionalityCode(), projectCode);
		List<Long> hightMaterializedItems = getHidedObjectId(profileId,
				FunctionalityCodes.SOURCING_MATERIALIZED_GRID, projectCode);

		if (hightGridItems != null && hightGridItems.size() > 0) {
			data.getGrids().addAll(smartGrilleRepository.findByTypesAllExclude(types, hightGridItems));
		} else {
			data.getGrids().addAll(smartGrilleRepository.findByTypes(types));
		}

		if (hightMaterializedItems != null && hightMaterializedItems.size() > 0) {
			data.getGrids().addAll(smartMaterializedGridRepository.findAllExclude(hightMaterializedItems));
		} else {
			data.getGrids().addAll(smartMaterializedGridRepository.findAll());
		}
		return data;

	}
	
	protected List<GrilleType> getSmallGridTypes() {
		List<GrilleType> types = new ArrayList<>();
		types.add(GrilleType.INPUT);
		types.add(GrilleType.REPORT);
		return types;
	}

	@Override
	protected Dashboard getNewItem() {
		Dashboard dashboard = new Dashboard();
		int i = 0;
		String baseName = "Dashboard";
		dashboard.setName(baseName);

		while (getByName(dashboard.getName()) != null) {
			i++;
			dashboard.setName(baseName + i);
		}
		return dashboard;
	}
	
	@Override
	public BrowserDataPage<DashboardBrowserData> search(BrowserDataFilter filter, Locale locale, Long profileId, String projectCode) {
		if (filter != null && filter.getProfileId() != null) {
			BrowserDataPage<DashboardBrowserData> page = new BrowserDataPage<DashboardBrowserData>();
			page.setPageSize(filter.getPageSize());
			List<ProfileDashboard> pdashboards = profileDashboardRepository.getProfileDashboardByProfileId(filter.getProfileId());
			List<DashboardBrowserData> items = new ArrayList<>(0);
			pdashboards.forEach(pd -> {
				Optional<Dashboard> dashboard = dashboardRepository.findById(pd.getDashboardId());
				if(dashboard.isPresent()) {
					DashboardBrowserData element = new DashboardBrowserData(dashboard.get(), pd.isDefaultDashboard());
					if (element != null) {
						items.add(element);
					}
				}
			});
			Collections.sort(items, new Comparator<DashboardBrowserData>() {
				@Override
				public int compare(DashboardBrowserData o1, DashboardBrowserData o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			page.setItems(items);
			return page;
		}
		return super.search(filter, locale,profileId, projectCode);
	}

//	@Override
//	public BrowserDataPage<BrowserData> search(BrowserDataFilter filter, Locale locale, Long profileId, String projectCode) {
//		if (filter != null && filter.getProfileId() != null) {
//			BrowserDataPage<BrowserData> page = new BrowserDataPage<BrowserData>();
//			page.setPageSize(filter.getPageSize());
//			List<Dashboard> dashboards = profileDashboardRepository.getDashboardByProfileId(filter.getProfileId());
//			List<BrowserData> items = new ArrayList<>(0);
//			dashboards.forEach(dashboard -> {
//				BrowserData element = getNewBrowserData(dashboard);
//				if (element != null) {
//					items.add(element);
//				}
//			});
//			page.setItems(items);
//			return page;
//		}
//		return super.search(filter, locale,profileId, projectCode);
//	}
	
	@Override
	protected DashboardBrowserData getNewBrowserData(Dashboard item) {
		return new DashboardBrowserData(item);
	}

	@Override
	protected Specification<Dashboard> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Dashboard> qBuilder = new RequestQueryBuilder<Dashboard>(root, query, cb);
			qBuilder.select(Dashboard.class);			
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
		if ("id".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("id");
			columnFilter.setType(Long.class);
		}
	}
	
	@Override
	@Transactional
	public Dashboard save(Dashboard dashboard, Locale locale) {
		log.debug("Try to  Save Grid : {}", dashboard);
		try {
			if (dashboard == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.dashboard",
						new Object[] { dashboard }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(dashboard.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.dashboard.with.empty.name",
						new String[] { dashboard.getName() }, locale);
				throw new BcephalException(message);
			}

			ListChangeHandler<DashboardItem> items = dashboard.getItemsListChangeHandler();

			dashboard.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(dashboard, locale);
			if(dashboard.getDefaultItem() != null) {
				DashboardItem defaultItem = dashboard.getDefaultItem();
				DashboardItem item = dashboardItemRepository.save(defaultItem);
				ListChangeHandler<DashboardItemVariable> variables = defaultItem.getVariableListChangeHandler();
				
				variables.getNewItems().forEach(variable -> {
					log.trace("Try to save Dashboard Item variable : {}", variable);
					variable.setItemId(item);
					if(variable.getReference() != null) {
						variable.getReference().setVariable(variable);
						save(variable.getReference());
					}
					dashboardItemVariableRepository.save(variable);
					log.trace("Dashboard Item variable saved : {}", variable.getId());
				
				});
				variables.getUpdatedItems().forEach(variable -> {
					log.trace("Try to save Dashboard Item variable : {}", variable);
					variable.setItemId(item);
					if(variable.getReference() != null) {
						variable.getReference().setVariable(variable);
						save(variable.getReference());
					}
					dashboardItemVariableRepository.save(variable);
					log.trace("Dashboard Item variable saved : {}", variable.getId());
					
				});
				variables.getDeletedItems().forEach(variable -> {
					if (variable.getId() != null) {
						log.trace("Try to delete Dashboard Item variable : {}", variable);
						if(variable.getReference() != null && variable.getReference().getId() != null) {
							dashboardItemVariableReferenceRepository.deleteById(variable.getReference().getId());
						}
						dashboardItemVariableRepository.deleteById(variable.getId());
						log.trace("Dashboard Item variable deleted : {}", variable.getId());
						
					}
				});
				
				dashboard.setDefaultItem(item);
				
			}
			
			dashboard = dashboardRepository.save(dashboard);
			Dashboard id = dashboard;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save Dashboard Item : {}", item);
				item.setDashboard(id);
				save(item);
				log.trace("Dashboard Item saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save Dashboard Item : {}", item);
				item.setDashboard(id);
				save(item);
				log.trace("Dashboard Item saved : {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Dashboard Item : {}", item);
					delete(item);
					log.trace("Dashboard Item deleted : {}", item.getId());
				}
			});
			
			
			log.debug("Dashboard saved : {} ", dashboard.getId());
			return dashboard;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save dashboard : {}", dashboard, e);
			String message = getMessageSource().getMessage("unable.to.save.dashboard", new Object[] { dashboard },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	private void save(DashboardItem item) {
		ListChangeHandler<DashboardItemFilter> filters = item.getFilterListChangeHandler();
		ListChangeHandler<DashboardItemUserLoader> userLoaders = item.getUserLoaderListChangeHandler();
		
		
		item = dashboardItemRepository.save(item);
		DashboardItem id = item;
		filters.getNewItems().forEach(filter -> {
			log.trace("Try to save Dashboard Item filter : {}", filter);
			filter.setItemId(id);
			dashboardItemFilterRepository.save(filter);
			log.trace("Dashboard Item filter saved : {}", filter.getId());
		});
		filters.getUpdatedItems().forEach(filter -> {
			log.trace("Try to save Dashboard Item filter : {}", filter);
			filter.setItemId(id);
			dashboardItemFilterRepository.save(filter);
			log.trace("Dashboard Item filter saved : {}", filter.getId());
		});
		filters.getDeletedItems().forEach(filter -> {
			if (filter.getId() != null) {
				log.trace("Try to delete Dashboard Item filter : {}", filter);
				dashboardItemFilterRepository.deleteById(filter.getId());
				log.trace("Dashboard Item filter deleted : {}", filter.getId());
			}
		});
		
		
		userLoaders.getNewItems().forEach(userLoader -> {
			log.trace("Try to save Dashboard Item User Loader : {}", userLoader);
			userLoader.setItemId(id);
			dashboardItemUserLoaderRepository.save(userLoader);
			log.trace("Dashboard Item User Loader saved : {}", userLoader.getId());
		});
		userLoaders.getUpdatedItems().forEach(userLoader -> {
			log.trace("Try to save Dashboard Item User Loader : {}", userLoader);
			userLoader.setItemId(id);
			dashboardItemUserLoaderRepository.save(userLoader);
			log.trace("Dashboard Item User Loader saved : {}", userLoader.getId());
		});
		userLoaders.getDeletedItems().forEach(userLoader -> {
			if (userLoader.getId() != null) {
				log.trace("Try to delete Dashboard Item User Loader : {}", userLoader);
				dashboardItemUserLoaderRepository.deleteById(userLoader.getId());
				log.trace("Dashboard Item User Loader deleted : {}", userLoader.getId());
			}
		});
		
	}
	
	private void save(DashboardItemVariableReference item) {
		ListChangeHandler<DashboardItemVariableReferenceCondition> conditions = item.getConditionListChangeHandler();
		item = dashboardItemVariableReferenceRepository.save(item);
		DashboardItemVariableReference id = item;
		conditions.getNewItems().forEach(condition -> {
			log.trace("Try to save Dashboard Item reference condition : {}", condition);
			condition.setReference(id);
			dashboardItemVariableReferenceConditionRepository.save(condition);
			log.trace("Dashboard Item condition saved : {}", condition.getId());
		});
		conditions.getUpdatedItems().forEach(condition -> {
			log.trace("Try to save Dashboard Item reference condition : {}", condition);
			condition.setReference(id);
			dashboardItemVariableReferenceConditionRepository.save(condition);
			log.trace("Dashboard Item condition saved : {}", condition.getId());
		});
		conditions.getDeletedItems().forEach(condition -> {
			if (condition.getId() != null) {
				log.trace("Try to delete Dashboard Item reference condition : {}", condition);
				dashboardItemVariableReferenceConditionRepository.deleteById(condition.getId());
				log.trace("Dashboard Item condition deleted : {}", condition.getId());
			}
		});
		
		
	}

	@Override
	@Transactional
	public void delete(Dashboard dashboard) {
		log.debug("Try to delete dashboard : {}", dashboard);
		if (dashboard == null || dashboard.getId() == null) {
			return;
		}
		List<ProfileDashboard> profileDashboards = profileDashboardRepository.findByDashboardId(dashboard.getId());
		ListChangeHandler<DashboardItem> items = dashboard.getItemsListChangeHandler();
		items.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete Dashboard Item : {}", item);
				delete(item);
				log.trace("Dashboard Item deleted : {}", item.getId());
			}
		});
		if(dashboard.getDefaultItem() != null && dashboard.getDefaultItem().getId() != null) {
			log.trace("Try to delete default dashboard Item : {}", dashboard.getDefaultItem());
			delete(dashboard.getDefaultItem());
			log.trace("Default dashboard Item deleted : {}", dashboard.getDefaultItem().getId());
		}
		profileDashboards.forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete Dashboard profil : {}", item);
				profileDashboardRepository.deleteById(item.getId());
				log.trace("Dashboard profil deleted : {}", item.getId());
			}
		});
		dashboardRepository.deleteById(dashboard.getId());
		log.debug("Dashboard successfully to delete : {} ", dashboard);
		return;
	}
	
	private void delete(DashboardItem item) {
		if (item == null || item.getId() == null) {
			return;
		}
		ListChangeHandler<DashboardItemFilter> filters = item.getFilterListChangeHandler();		
		filters.getItems().forEach(filter -> {
			if (item.getId() != null) {
				log.trace("Try to delete Dashboard Item filter : {}", filter);
				dashboardItemFilterRepository.deleteById(filter.getId());
				log.trace("Dashboard Item filter deleted : {}", filter.getId());
			}
		});
		
		ListChangeHandler<DashboardItemVariable> variables = item.getVariableListChangeHandler();		
		variables.getItems().forEach(variable -> {
			if (item.getId() != null) {
				log.trace("Try to delete Dashboard Item variable : {}", variable);
				if(variable.getReference() != null && variable.getReference().getId() != null) {
					delete(variable.getReference());
				}
				dashboardItemVariableRepository.deleteById(variable.getId());
				log.trace("Dashboard Item variable deleted : {}", variable.getId());
			}
		});
		dashboardItemRepository.deleteById(item.getId());
		log.debug("Dashboard Item successfully to delete : {} ", item);
		return;
	}

	private void delete(DashboardItemVariableReference item) {
		ListChangeHandler<DashboardItemVariableReferenceCondition> conditions = item.getConditionListChangeHandler();	
		conditions.getItems().forEach(condition -> {
			if (item.getId() != null) {
				log.trace("Try to delete Dashboard Item variable reference condition : {}", condition);
				dashboardItemVariableReferenceConditionRepository.deleteById(condition.getId());
				log.trace("Dashboard Item variable reference condition deleted : {}", condition.getId());
			}
		});
		
		dashboardItemVariableReferenceRepository.deleteById(item.getId());
		log.debug("Dashboard Item variable reference successfully to delete : {} ", item);
		return;
	}
	
	
	public ProfileDashboardEditorData getProfileDashboardEditorData(Long profileId, Locale locale) {
		ProfileDashboardEditorData data = new ProfileDashboardEditorData();
		data.getItemListChangeHandler().setOriginalList(profileDashboardRepository.findByProfileId(profileId));
		List<Dashboard> dashboards = dashboardRepository.findAll();
		dashboards.forEach(dashboard -> {data.getDashboards().add(new Nameable(dashboard.getId(), dashboard.getName()));});
		data.getItemListChangeHandler().getOriginalList().forEach(item -> {
			dashboards.forEach(dashboard -> {
				if(item.getDashboardId() == dashboard.getId()) {
					item.setName(dashboard.getName());
				}
			});
		});
		return data;
	}
	
	@Transactional
	public void save(ListChangeHandler<ProfileDashboard> dashboards, Long profileId, Locale locale) {
		log.debug("Try to  Save profile dashboards : {}", dashboards);
		try {			
			dashboards.getNewItems().forEach(item -> {
				log.trace("Try to save Profile Dashboard : {}", item);
				item.setProfileId(profileId);
				profileDashboardRepository.save(item);
				log.trace("Profile Dashboard saved : {}", item.getId());
			});
			dashboards.getUpdatedItems().forEach(item -> {
				log.trace("Try to save Profile Dashboard : {}", item);
				item.setProfileId(profileId);
				profileDashboardRepository.save(item);
				log.trace("Profile Dashboard saved : {}", item.getId());
			});
			dashboards.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Profile Dashboard : {}", item);
					profileDashboardRepository.deleteById(item.getId());
					log.trace("Profile Dashboard deleted : {}", item.getId());
				}
			});

			log.debug("profileId Dashboards saved");
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save profile dashboards : {}", dashboards, e);
			String message = getMessageSource().getMessage("unable.to.save.profile.dashboards", new Object[] { },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	

}
