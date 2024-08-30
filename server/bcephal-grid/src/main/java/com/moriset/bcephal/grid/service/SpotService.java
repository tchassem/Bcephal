package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.dimension.SpotEditorData;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.SpotEvaluator;
import com.moriset.bcephal.service.filters.ISpotService;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class SpotService extends DataSourcableService<Spot, BrowserData> implements  ISpotService{

	@Autowired
	SpotRepository spotRepository;

	@Autowired
	UniverseFilterService universeFilterService;
	
	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	SpotEvaluator spotEvaluator;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired MaterializedGridRepository materializedGridRepository;
	
	@Autowired  MaterializedGridService materializedGridService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_SPOT;
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
	public MainObjectRepository<Spot> getRepository() {
		return spotRepository;
	}

	@Override
	public EditorData<Spot> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		SpotEditorData data = new SpotEditorData();		
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			data.getItem().setGridType(filter.getDataSourceType());
			data.getItem().setGridId( filter.getDataSourceId());
		} else {
			data.setItem(getById(filter.getId()));
		}
		if(data.getItem() != null) {
			if(data.getItem().getGridType().isMaterializedGrid()) {
				String dName = initEditorDataForMaterializedGrid(data, data.getItem().getGridId(), session, locale);
				data.getItem().setGridName(dName);
			}
			else {
				initEditorData(data, session, locale);
			}
		}		
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);
		List<Long> hightGridItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_SPOT, projectCode);
		if(hightGridItems != null && hightGridItems.size() > 0) {
			data.setGrids(grilleRepository.findByTypeExclude(GrilleType.REPORT, hightGridItems));
		}else {
			data.setGrids(grilleRepository.findByType(GrilleType.REPORT));
		}
		data.setMatGrids(materializedGridRepository.findGenericAllAsNameables());
		return data;
	}
	
	@Override
	protected void setDataSourceName(Spot item) {
		if(item != null && item.getGridType() == DataSourceType.MATERIALIZED_GRID && item.getGridId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getGridId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setGridName(grid != null ? grid.getName() : null);
		}
	}

	@Override
	protected Spot getNewItem() {
		Spot spot = new Spot();
		String baseName = "Spot ";
		int i = 1;
		spot.setName(baseName + i);
		while (getByName(spot.getName()) != null) {
			i++;
			spot.setName(baseName + i);
		}
		return spot;
	}

	@Override
	protected BrowserData getNewBrowserData(Spot item) {
		return new BrowserData(item);
	}

	@Override
	protected Specification<Spot> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Spot> qBuilder = new RequestQueryBuilder<Spot>(root, query, cb);
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
	public Spot save(Spot spot, Locale locale) {
		log.debug("Try to  Save spot : {}", spot);
		try {
			if (spot == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.spot", new Object[] { spot },
						locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(spot.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.spot.with.empty.name",
						new String[] { spot.getName() }, locale);
				throw new BcephalException(message);
			}
			if (spot.getFilter() != null) {
				universeFilterService.save(spot.getFilter());
			}
			spot.setModificationDate(new Timestamp(System.currentTimeMillis()));
			spot = super.save(spot, locale);
			log.debug("spot successfully to save : {} ", spot);
			return spot;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save spot : {}", spot, e);
			String message = getMessageSource().getMessage("unable.to.save.spot", new Object[] { spot }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	public void delete(Spot spot) {
		if (spot == null || spot.getId() == null) {
			return;
		}
		if (spot.getFilter() != null) {
			universeFilterService.delete(spot.getFilter());
		}
		getRepository().deleteById(spot.getId());
		log.debug("Spot successfully to delete : {} ", spot);
	}

	public List<Nameable> getSpotsAsNameable() {
		log.debug("Try to  get all spots.");
		return spotRepository.findAllAsNameables();
	}
	
	public BigDecimal evaluate(Long id) throws Exception {
		return evaluate(getById(id));
	}
	
	boolean checkAttributeFilter(AttributeFilter filter) {
		if(filter != null 
				 && filter.getItems() != null
				 && filter.getItems().size() > 0
				 ) {
			return true;
		 }
		return false;
	}
	boolean checkMeasureFilter(MeasureFilter filter) {
		if(filter != null 
				 && filter.getItems() != null
				 && filter.getItems().size() > 0
				 ) {
			return true;
		 }
		return false;
	}
	boolean checkPeriodFilter(PeriodFilter filter) {
		if(filter != null 
				 && filter.getItems() != null
				 && filter.getItems().size() > 0
				 ) {
			return true;
		 }
		return false;
	}

	public List<UniverseFilter> buildSpotReportFilter(Spot spot){
		List<UniverseFilter> filters = new ArrayList<>();
		if(spot != null && spot.getGridId() != null && spot.getGridType() != DataSourceType.MATERIALIZED_GRID) {
		 Optional<Grille> grid = grilleRepository.findById(spot.getGridId());
		 if(grid.isPresent()) {
			Grille grille = grid.get();
			 if(grille.getAdminFilter() != null && (
					 checkAttributeFilter(grille.getAdminFilter().getAttributeFilter())
					 || checkMeasureFilter(grille.getAdminFilter().getMeasureFilter())
					 || checkPeriodFilter(grille.getAdminFilter().getPeriodFilter())
					 )) {
				 filters.add(grille.getAdminFilter());
			 }
			
			 if(grille.getUserFilter() != null 
							 && (
									 checkAttributeFilter(grille.getUserFilter().getAttributeFilter())
									 || checkMeasureFilter(grille.getUserFilter().getMeasureFilter())
									 || checkPeriodFilter(grille.getUserFilter().getPeriodFilter())
									 )) {
				 filters.add(grille.getUserFilter());
			 }
		 }
		}
		return filters;
	}
	
	public BigDecimal evaluate(Spot spot) throws Exception {
		List<UniverseFilter> filters = new ArrayList<>();
		if(spot != null) {
			filters = buildSpotReportFilter(spot);
		}
		return spotEvaluator.evaluate(spot, filters);
	}

}
