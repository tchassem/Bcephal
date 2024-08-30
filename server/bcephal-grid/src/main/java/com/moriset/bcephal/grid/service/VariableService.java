/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.Variable;
import com.moriset.bcephal.domain.VariableBrowserData;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.VariableEditorData;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartJoinRepository;
import com.moriset.bcephal.repository.VariableRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class VariableService extends DataSourcableService<Variable, VariableBrowserData> {
	
	@Autowired
	VariableRepository attributeRepository;

	@Autowired
	InitiationService initiationService;
	
	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	SmartGrilleRepository smartGrilleRepository;
	
	@Autowired
	SmartJoinRepository smartJoinRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	public VariableRepository getRepository() {
		return attributeRepository;
	}
	
	
	protected Specification<Variable> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {

		return (root, query, cb) -> {
			RequestQueryBuilder<Variable> qBuilder = new RequestQueryBuilder<Variable>(root, query, cb);
			qBuilder.select(VariableBrowserData.class);			
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
				List<ColumnFilter> toRemove = new ArrayList<>();
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    		if ("dataSourceId".equalsIgnoreCase(filte.getName()) 
		    				|| "dataSourceType".equalsIgnoreCase(filte.getName())) {
						toRemove.add(filte);
					} 
		    	});		    	
		    	filter.getColumnFilters().getItems().removeAll(toRemove);		    	
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}
	
	@Override
	public EditorData<Variable> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		
		VariableEditorData data = new VariableEditorData();
		
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			data.getItem().setDataSourceType(filter.getDataSourceType());
			data.getItem().setDataSourceId( filter.getDataSourceId());
			data.getItem().setDimensionType(DimensionType.ATTRIBUTE);
		} else {
			data.setItem(getById(filter.getId()));
		}
		
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);
		List<Long> hightGridItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_INPUT_GRID, projectCode);
		if(data.getItem() != null) {
			if(data.getItem().getDataSourceType().isMaterializedGrid()) {				
				Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(data.getItem().getDataSourceId());
				if(response.isPresent()) {
					data.grids = new ArrayList<>();
					data.grids.add(new Nameable(response.get().getId(), response.get().getName()));
				}
				
				String dName = initEditorDataForMaterializedGrid(data, data.getItem().getDataSourceId(), session, locale);
				data.getItem().setDataSourceName(dName);
			} else {
				if(hightGridItems != null && hightGridItems.size() > 0) {
					data.grids = grilleRepository.findByTypeExclude(GrilleType.INPUT, hightGridItems);
				}else {
					data.grids = grilleRepository.findByType(GrilleType.INPUT);
				}
				
				initEditorData(data, session, locale);				
			}
		}			
		
		List<Long> hightMaterializedItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_MATERIALIZED_GRID, projectCode);
		List<Long> hightJointItems = getHidedObjectId(profileId, FunctionalityCodes.REPORTING_REPORT_JOIN_GRID, projectCode);		
		List<GrilleType> types = getSmallGridTypes();
		
		if(hightGridItems != null && hightGridItems.size() > 0) {
			data.getSmartGrids().addAll(smartGrilleRepository.findByTypesAllExclude(types, hightGridItems));
		} else {
			data.getSmartGrids().addAll(smartGrilleRepository.findByTypes(types));
		}
		if(hightMaterializedItems != null && hightMaterializedItems.size() > 0) {
			data.getSmartGrids().addAll(smartMaterializedGridRepository.findAllExclude(hightMaterializedItems));
		} else {
			data.getSmartGrids().addAll(smartMaterializedGridRepository.findAll());
		}
		if(hightJointItems != null && hightJointItems.size() > 0) {
			data.getSmartGrids().addAll(smartJoinRepository.findAllExclude(hightJointItems));
		} else {
			data.getSmartGrids().addAll(smartJoinRepository.findAll());
		}	
		// data.setDateFormats(this.dateFormats);
		return data;
	}

	@Override
	protected void setDataSourceName(Variable item) {
		if(item != null && item.getDataSourceType() == DataSourceType.MATERIALIZED_GRID && item.getDataSourceId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getDataSourceId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setDataSourceName(grid != null ? grid.getName() : null);
		}
	}
	
	protected List<GrilleType> getSmallGridTypes(){
		List<GrilleType> types = new ArrayList<>();
		types.add(GrilleType.INPUT);
		types.add(GrilleType.REPORT);
		//types.add(GrilleType.BILLING_EVENT_REPOSITORY);
		//types.add(GrilleType.CLIENT_REPOSITORY);
		return types;
	}

	public Variable getByName(String name) {
		List<Variable> objects = getAllByName(name);
		return objects.size() > 0 ? objects.get(0) : null;
	}
	
	public List<Variable> getAllByName(String name) {
		log.debug("Try to get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByName(name);
	}
	
	protected Variable getNewItem() {
		Variable group = new Variable();
		String baseName = "Group ";
		int i = 1;
		group.setName(baseName + i);
		while (getByName(group.getName()) != null) {
			i++;
			group.setName(baseName + i);
		}
		return group;
	}

	@Transactional
	public boolean saveList(List<Variable> variables, Locale locale) {
		if(variables == null) {
			throw new BcephalException("Variable is null");
		}
		for (Variable variable : variables) {
			save(variable, locale);
		}		
		return true;
	}
	
	@Override
	public Variable save(Variable entity, Locale locale) {
		log.debug("Try to Save Variable : {}", entity);
		if (getRepository() == null) {
			return entity;
		}
		try {
			if (entity == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { entity },
						locale);
				throw new BcephalException(message);
			}
			if(!entity.isPersistent()) {
				Variable var = getDuplicate(entity, locale);
				if(var != null) {
					return var;
				}
			}
					
			if(entity.getCreationDate() == null) {
				entity.setCreationDate(new Timestamp(System.currentTimeMillis()));
			}
			entity.setModificationDate(new Timestamp(System.currentTimeMillis()));			
			entity = getRepository().save(entity);
			log.debug("entity successfully saved : {} ", entity);
			return entity;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", entity, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { entity }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	protected Variable getDuplicate(Variable entity, Locale locale) {
		List<Variable> objects = getAllByName(entity.getName());
		for(Variable obj : objects) {
			if(!obj.getId().equals(entity.getId())) {
				return obj;
			}
		}
		return null;
	}
	
	
	protected void build(ColumnFilter columnFilter) {
		super.build(columnFilter);
//		if ("dataSourceId".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("dataSourceId");
//			columnFilter.setType(Long.class);
//		} 
//		else if ("dataSourceType".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("dataSourceType");
//			columnFilter.setType(DataSourceType.class);
//		} 
		if ("dimensionType".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("dimensionType");
			columnFilter.setType(DimensionType.class);
		} 
		else if ("dimensionId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("dimensionId");
			columnFilter.setType(Long.class);
		}	
	}


	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.VARIABLE;
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
	protected VariableBrowserData getNewBrowserData(Variable item) {
		return new VariableBrowserData(item);
	}
	

}
