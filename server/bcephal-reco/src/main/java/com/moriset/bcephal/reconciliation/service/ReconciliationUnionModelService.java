/**
 * 4 avr. 2024 - ReconciliationUnionModelService.java
 *
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.RightEditorData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.AttributeValue;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.EnrichmentValue;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridCreateColumnRequest;
import com.moriset.bcephal.grid.domain.UnionGridCreateColumnRequestData;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.repository.SmartUnionGridRepository;
import com.moriset.bcephal.grid.service.DimensionDataFilter;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.grid.service.UnionGridService;
import com.moriset.bcephal.reconciliation.domain.PartialRecoItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationActions;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationLog;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModel;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelEnrichment;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModel;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelButtonColumn;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelEditorData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelEnrichment;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelGrid;
import com.moriset.bcephal.reconciliation.repository.ReconciliationLogRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationUnionModelButtonColumnRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationUnionModelEnrichmentRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationUnionModelGridRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationUnionModelRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.repository.filters.AttributeRepository;
import com.moriset.bcephal.repository.filters.AttributeValueRepository;
import com.moriset.bcephal.repository.filters.EntityRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.repository.filters.MeasureRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Emmanuel Emmeni
 *
 */
@Service
@Slf4j
public class ReconciliationUnionModelService extends MainObjectService<ReconciliationUnionModel, BrowserData> {

	@Autowired
	ReconciliationUnionModelButtonColumnRepository reconciliationUnionModelButtonColumnRepository;
	
	@Autowired
	ReconciliationUnionModelGridRepository reconciliationUnionModelGridRepository;
	
	@Autowired
	ReconciliationUnionModelRepository reconciliationModelRepository;
	
	@Autowired
	ReconciliationUnionModelEnrichmentRepository reconciliationUnionModelEnrichmentRepository;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	EntityRepository entityRepository;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	UnionGridService unionGridService;
	
	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	ReconciliationUnionModelGridService reconciliationUnionModelGridService;
		
	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;
	
	@Autowired
	AttributeRepository attributeRepository;
	
	@Autowired
	MeasureRepository measureRepository;
	
	@Autowired
	AttributeValueRepository attributeValueRepository;
	
	@Autowired
	ReconciliationLogRepository reconciliationLogRepository;
	
	@Autowired
	WriteOffUnionModelService writeOffModelService;
	
	@Autowired
	MaterializedGridRepository materializedGridRepository;	
	
	@Autowired
	SmartGrilleRepository smartGrilleRepository;
	
	@Autowired
	SmartUnionGridRepository smartUnionGridRepository;
	
	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.RECONCILIATION_FILTER;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}

	@Override
	public ReconciliationUnionModelRepository getRepository() {
		return reconciliationModelRepository;
	}
	
	@Override
	public ReconciliationUnionModelEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		ReconciliationUnionModelEditorData data = new ReconciliationUnionModelEditorData();
		if(filter.isNewData()) {
			data.setItem(getNewItem());
		}
		else {
			data.setItem(getById(filter.getId()));
			if(data.getItem() != null) {
				if(data.getItem().getLeftGrid().getGrid() != null) {
					data.getItem().getLeftGrid().getGrid().setGridType(GrilleType.RECONCILIATION);
					BuildColumns(data.getItem().getLeftGrid().getGrid());
				}
				if(data.getItem().getRigthGrid().getGrid() != null) {
					data.getItem().getRigthGrid().getGrid().setGridType(GrilleType.RECONCILIATION);
					BuildColumns(data.getItem().getRigthGrid().getGrid());
				}
			}
		}
		initEditorData(data, session, locale);
		
		if(data.getItem() != null && data.getItem().getGroup() == null) {
			data.getItem().setGroup(getDefaultGroup());
		}	
		
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);
		List<Long> hightMaterializedItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_MATERIALIZED_GRID, projectCode);
		if(hightMaterializedItems != null && hightMaterializedItems.size() > 0) {
			data.getGrids().addAll(smartMaterializedGridRepository.findAllExclude(hightMaterializedItems));
		} else {
			data.getGrids().addAll(smartMaterializedGridRepository.findAll());
		}
		return data;
	}

	public void BuildColumns(UnionGrid grid) {
		if (grid != null) {
			for (UnionGridItem item : grid.getItems()) {
				Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository
						.findById(item.getGrid().getId());
				if (response.isPresent()) {
					item.setGrid(response.get());
					for (UnionGridColumn column : grid.getColumns()) {
						List<Long> ids = column.buildColumnIds();
						for (Long columnId : ids) {
							SmartMaterializedGridColumn c = item.getGrid().getColumnById(columnId);
							if (c != null) {
								column.getColumns().add(c);
							}
						}
					}

				}
			}
		}
	}
	
	protected List<GrilleType> getSmallGridTypes(){
		List<GrilleType> types = new ArrayList<>();
		types.add(GrilleType.INPUT);
		types.add(GrilleType.REPORT);
		return types;
	}
	
	@Override
	protected void initEditorData(EditorData<ReconciliationUnionModel> data_, HttpSession session, Locale locale) throws Exception {
		
		ReconciliationUnionModelEditorData data = (ReconciliationUnionModelEditorData)data_;
		data.setModels(getInitiationService().getModels(session, locale));
		data.setPeriods(getInitiationService().getPeriods(session, locale));
		data.setMeasures(getInitiationService().getMeasures(session, locale));
		data.setCalendarCategories(getInitiationService().getCalendarsAsNameable(session, locale));
		data.setSpots(getInitiationService().getSpotsAsNameable(session, locale));
		
		for(IncrementalNumber sequence : incrementalNumberRepository.findAll()) {
			data.getSequences().add(new Nameable(sequence.getId(), sequence.getName()));
		}
				
		Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
		if(parameter != null && parameter.getLongValue() != null) {
			data.setDebitCreditAttributeId(parameter.getLongValue());
		}
		parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
		if(parameter != null && parameter.getStringValue() != null) {
			data.setDebitValue(parameter.getStringValue());
		}
		else {
			data.setDebitValue("D");
		}
		parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
		if(parameter != null && parameter.getStringValue() != null) {
			data.setCreditValue(parameter.getStringValue());
		}
		else {
			data.setCreditValue("C");
		}	
		data.setMatGrids(materializedGridRepository.findAllAsNameables());
	}
	
	@Override
	public RightEditorData<ReconciliationUnionModel> getRightLowLevelEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		RightEditorData<ReconciliationUnionModel> data = super.getRightLowLevelEditorData(filter, session, locale);
		if(filter != null && StringUtils.hasText(filter.getSubjectType())) {
			data.setItems(reconciliationModelRepository.findGenericAllAsNameables());
		}
		return data;
	}

	@Override
	protected ReconciliationUnionModel getNewItem() {
		ReconciliationUnionModel reconciliationModel = new ReconciliationUnionModel();
		String baseName = "Reconciliation Union Model ";
		int i = 1;
		reconciliationModel.setName(baseName + i);
		while(getByName(reconciliationModel.getName()) != null) {
			i++;
			reconciliationModel.setName(baseName + i);
		}
		
		ReconciliationUnionModelGrid grid = new ReconciliationUnionModelGrid();
		grid.setType(JoinGridType.UNION_GRID);
		UnionGrid unionGrid = unionGridService.getNewUnionGrid("Left");
		grid.setGrid(unionGrid);
		// unionGrid.setActive(true);
		unionGrid.setGridType(GrilleType.RECONCILIATION);
		reconciliationModel.setLeftGrid(grid);
		
		grid = new ReconciliationUnionModelGrid();
		grid.setType(JoinGridType.UNION_GRID);
		unionGrid =  unionGridService.getNewUnionGrid("Rigth");
		grid.setGrid(unionGrid);
		// unionGrid.setActive(true);
		unionGrid.setGridType(GrilleType.RECONCILIATION);
		reconciliationModel.setRigthGrid(grid);
		
		return reconciliationModel;
	}
	
@Override
@Transactional
public ReconciliationUnionModel save(ReconciliationUnionModel reconciliationUnionModel, Locale locale) {
	log.debug("Try to Save reconciliationUnionModel : {}", reconciliationUnionModel);		
	try {	
		if(reconciliationUnionModel == null) {
			String message = getMessageSource().getMessage("unable.to.save.null.reconciliation.model", new Object[]{reconciliationUnionModel} , locale);
			throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
		}
		if(!StringUtils.hasLength(reconciliationUnionModel.getName())) {
			String message = getMessageSource().getMessage("unable.to.save.reconciliation.model.with.empty.name", new String[]{reconciliationUnionModel.getName()} , locale);
			throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
		}
		
		ListChangeHandler<ReconciliationUnionModelEnrichment> enrichments = reconciliationUnionModel.getEnrichmentListChangeHandler();
		ListChangeHandler<ReconciliationUnionModelButtonColumn> buttonColumnLists = reconciliationUnionModel.getButtonColumnListChangeHandler();
		
		reconciliationUnionModel.setModificationDate(new Timestamp(System.currentTimeMillis()));
		if(reconciliationUnionModel.getLeftGrid() != null) {
			reconciliationUnionModelGridService.save(reconciliationUnionModel.getLeftGrid(), locale);
		}
		if(reconciliationUnionModel.getRigthGrid() != null) {
			reconciliationUnionModelGridService.save(reconciliationUnionModel.getRigthGrid(), locale);
		}
		if(reconciliationUnionModel.getWriteOffModel() != null) {
			writeOffModelService.save(reconciliationUnionModel.getWriteOffModel(), locale);
		}
		
		validateBeforeSave(reconciliationUnionModel, locale);
		reconciliationUnionModel = getRepository().save(reconciliationUnionModel);
		ReconciliationUnionModel id = reconciliationUnionModel;
		
		if(buttonColumnLists.getNewItems() != null) {
			buttonColumnLists.getNewItems().forEach( item -> {
				log.trace("Try to save ReconciliationUnionModelButtonColumn : {}", item);
				item.setModel(id);
				reconciliationUnionModelButtonColumnRepository.save(item);
				log.trace("ReconciliationUnionModelButtonColumn saved : {}", item.getId());
			});
		}
		if(buttonColumnLists.getUpdatedItems() != null) {
			buttonColumnLists.getUpdatedItems().forEach( item -> {
				log.trace("Try to save ReconciliationUnionModelButtonColumn : {}", item);
				item.setModel(id);
				reconciliationUnionModelButtonColumnRepository.save(item);
				log.trace("ReconciliationUnionModelButtonColumn saved : {}", item.getId());
			});
		}
		if(buttonColumnLists.getDeletedItems() != null) {
			buttonColumnLists.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete ReconciliationUnionModelButtonColumn : {}", item);
					reconciliationUnionModelButtonColumnRepository.deleteById(item.getId());
					log.trace("ReconciliationUnionModelButtonColumn deleted : {}", item.getId());
				}
			});
		}
		if(enrichments.getNewItems() != null) {
			enrichments.getNewItems().forEach( item -> {
				log.trace("Try to save ReconciliationUnionModelEnrichment : {}", item);
				item.setModel(id);
				reconciliationUnionModelEnrichmentRepository.save(item);
				log.trace("ReconciliationUnionModelEnrichment saved : {}", item.getId());
			});
		}
		if(enrichments.getUpdatedItems() != null) {
			enrichments.getUpdatedItems().forEach( item -> {
				log.trace("Try to save ReconciliationUnionModelEnrichment : {}", item);
				item.setModel(id);
				reconciliationUnionModelEnrichmentRepository.save(item);
				log.trace("reconciliationUnionModelEnrichment saved : {}", item.getId());
			});
		}
		if(enrichments.getDeletedItems() != null) {
			enrichments.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete reconciliationUnionModelEnrichment : {}", item);
					reconciliationUnionModelEnrichmentRepository.deleteById(item.getId());
					log.trace("ReconciliationUnionModelEnrichment deleted : {}", item.getId());
				}
			});
		
		}		
		
		log.debug("ReconciliationUnionModel saved : {} ", reconciliationUnionModel.getId());
        return reconciliationUnionModel;	
	}
	catch (BcephalException e) {
		throw e;
	}
	catch (Exception e) {
		log.error("Unexpected error while save ReconciliationModel : {}", reconciliationUnionModel, e);
		String message = getMessageSource().getMessage("unable.to.save.reconciliation.model", new Object[]{reconciliationUnionModel} , locale);
		throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}
}


@Override
public void delete(ReconciliationUnionModel reconciliationUnionModel) {
	log.debug("Try to delete ReconciliationUnionModel : {}", reconciliationUnionModel);	
	if(reconciliationUnionModel == null || reconciliationUnionModel.getId() == null) {
		return;
	}
	if(reconciliationUnionModel.getLeftGrid() != null) {
		reconciliationUnionModelGridService.delete(reconciliationUnionModel.getLeftGrid());
	}
	if(reconciliationUnionModel.getRigthGrid() != null) {
		reconciliationUnionModelGridService.delete(reconciliationUnionModel.getRigthGrid());
	}
	ListChangeHandler<ReconciliationUnionModelEnrichment> enrichments = reconciliationUnionModel.getEnrichmentListChangeHandler();
	ListChangeHandler<ReconciliationUnionModelButtonColumn> buttonColumnLists = reconciliationUnionModel.getButtonColumnListChangeHandler();
	if(enrichments.getItems() != null) {
		enrichments.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete ReconciliationUnionModelEnrichment : {}", item);
				reconciliationUnionModelEnrichmentRepository.deleteById(item.getId());
				log.trace("ReconciliationUnionModelEnrichment deleted : {}", item.getId());
			}
		});
	}
	if(enrichments.getDeletedItems() != null) {
		enrichments.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete ReconciliationUnionModelEnrichment : {}", item);
				reconciliationUnionModelEnrichmentRepository.deleteById(item.getId());
				log.trace("ReconciliationUnionModelEnrichment deleted : {}", item.getId());
			}
		});
	}

	if(buttonColumnLists.getItems() != null) {
		buttonColumnLists.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete ReconciliationUnionModelButtonColumn : {}", item);
				reconciliationUnionModelButtonColumnRepository.deleteById(item.getId());
				log.trace("ReconciliationUnionModelButtonColumn deleted : {}", item.getId());
			}
		});
	}
	if(buttonColumnLists.getDeletedItems() != null) {
		buttonColumnLists.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete ReconciliationUnionModelButtonColumn : {}", item);
				reconciliationUnionModelButtonColumnRepository.deleteById(item.getId());
				log.trace("ReconciliationUnionModelButtonColumn deleted : {}", item.getId());
			}
		});
	}
	
	if(reconciliationUnionModel.getWriteOffModel() != null) {
		writeOffModelService.delete(reconciliationUnionModel.getWriteOffModel());
	}
	getRepository().deleteById(reconciliationUnionModel.getId());
	log.debug("ReconciliationUnionModel successfully to delete : {} ", reconciliationUnionModel);
    return;	
}

	@Override
	protected BrowserData getNewBrowserData(ReconciliationUnionModel item) {
		return new BrowserData(item);
	}

	@Override
	protected Specification<ReconciliationUnionModel> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<ReconciliationUnionModel> qBuilder = new RequestQueryBuilder<ReconciliationUnionModel>(root, query, cb);
		    qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), 
		    		root.get("creationDate"), root.get("modificationDate"));
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
	
	@Transactional
	public boolean reconciliate(ReconciliationUnionData data, String username, RunModes mode, boolean refreshPublicationAfterReco) throws Exception {

		try {
			loadSettings(data);			
			reconciliateWithoutCommint(data, username,  mode, refreshPublicationAfterReco);
			return true;
		} 
		catch (BcephalException e) {
			log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error during reconciliation",  e);
			if(e instanceof BcephalException) {
				throw e;
			}
			throw new BcephalException("Unexpected error during reconciliation.");
		}
	}
	
	protected UnionGrid getUnionGrid(JoinGridType type, Long id) {		
		UnionGrid result = unionGridService.getById(id);
		return result;
	}
	
	protected Grille getGrille(JoinGridType type, Long id) {		
		Optional<Grille> result = grilleRepository.findById(id);
		return result.isPresent() ? result.get() : null;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reconciliateAndCommit(List<ReconciliationUnionData> datas, String username, RunModes mode, boolean refreshPublicationAfterReco) throws Exception {
		for(ReconciliationUnionData data : datas) {
			//loadSettings(data);
			reconciliateWithoutCommint(data, username, mode, refreshPublicationAfterReco);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void reconciliateAndCommit2(List<ReconciliationUnionData> datas, String username, RunModes mode, boolean refreshPublicationAfterReco) throws Exception {
		reconciliateAndCommit(datas, username, mode, refreshPublicationAfterReco);
	}
	
	//@Transactional(propagation = Propagation.REQUIRED)
	public boolean reconciliateAndNotCommit(ReconciliationUnionData data, String username, RunModes mode, boolean refreshPublicationAfterReco) throws Exception {
		return reconciliateWithoutCommint(data, username, mode, refreshPublicationAfterReco);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void commit() {
		log.trace("Commit...");
	}
	
	public boolean reconciliateWithoutCommint(ReconciliationUnionData data, String username, RunModes mode, boolean refreshPublicationAfterReco) throws Exception {
		log.debug("Reconciliation model ID : {}", data.getReconciliationId());
		List<String> ids = new ArrayList<>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
		}
		if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
		}
				
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		int count = ids.size();
		log.debug("#Rows to reconcuiliate : {}", count);
		if (count <= 0) {
			log.debug("No rows to reconciliate!");
			throw new BcephalException("No rows to reconciliate!");
		}
		
		if(data.getLeftRecoTypeId() == null) {
			log.debug("Unable to reconciliate. The left reco type is NULL!");
			throw new BcephalException("Unable to reconciliate. The left reco type is NULL!");
		}
		if(data.getRightRecoTypeId() == null) {
			log.debug("Unable to reconciliate. The right reco type is NULL!");
			throw new BcephalException("Unable to reconciliate. The right reco type is NULL!");
		}
		if(data.getRecoSequenceId() == null) {
			log.debug("Unable to reconciliate. The reconciliation sequence is NULL!");
			throw new BcephalException("Unable to reconciliate. The reconciliation sequence is NULL!");
		}		
		if(!data.isPerformNeutralization() && data.isAllowPartialReco() && data.isPerformPartialReco() && data.getPartialRecoSequenceId() == null) {
			log.debug("Unable to reconciliate. The partial reconciliation sequence is NULL!");
			throw new BcephalException("Unable to reconciliate. The partial reconciliation sequence is NULL!");
		}
				
		ReconciliationUnionOperation operations = new ReconciliationUnionOperation(data);
		operations.setEntityManager(entityManager);
		operations.setAttributeRepository(attributeRepository);
		operations.setMeasureRepository(measureRepository);
		operations.setIncrementalNumberRepository(incrementalNumberRepository);		
		operations.build(data.getLeftids(), data.getRightids());
		
		List<String> leftSqls = data.buildReconciliationSql(true, username, mode);
		log.trace("Reco left query : {}", leftSqls);
		
		List<String> rightSqls = data.buildReconciliationSql(false, username, mode);		
		log.trace("Reco right query : {}", rightSqls);
		
		Date recoDate = new Date();
		for(String leftSql : leftSqls) {
			Query query = buildRecoQuery(data, operations, leftSql, recoDate, username, mode, true);
			if(query != null) {
				query.executeUpdate();
			}
		}
		for(String rightSql : rightSqls) {		
			Query query = buildRecoQuery(data, operations, rightSql, recoDate, username, mode, false);
			if(query != null) {
				query.executeUpdate();
			}	
		}
		
		performenrichment(data);
				
		if(!data.isPerformNeutralization()) {
			if(data.isAllowPartialReco() && data.isPerformPartialReco()) {
				if(mode == RunModes.M && data.getPartialRecoItems().size() > 0) {
					HashMap<Long,String> sqls = data.buildReconciliationPartialSqlFormManualMode(true, false);
					boolean isUnion = data.getLeftGridType().isUnionGrid();
					for(Long gridId : sqls.keySet()) {	
						String sql = sqls.get(gridId);
						for(PartialRecoItem item : data.getPartialRecoItems()) {
							boolean isOk = (isUnion && item.getGridId() != null && gridId.equals(item.getGridId())) || (!isUnion && item.getGridId() == null);
							if(item.isLeft() && isOk) {
								Query query = entityManager.createNativeQuery(sql);
								query.setParameter("reconciliatedAmount", item.getReconciliatedAmount());
								query.setParameter("remainingAmount", item.getRemainningAmount());
								query.setParameter("id", item.getId());
								query.executeUpdate();
							}
						}				
					}	
										
					sqls = data.buildReconciliationPartialSqlFormManualMode(false, false);
					isUnion = data.getRightGridType().isUnionGrid();
					for(Long gridId : sqls.keySet()) {	
						String sql = sqls.get(gridId);				
						for(PartialRecoItem item : data.getPartialRecoItems()) {
							boolean isOk = (isUnion && item.getGridId() != null && gridId.equals(item.getGridId())) || (!isUnion && item.getGridId() == null);
							if(!item.isLeft() && isOk) {
								Query query = entityManager.createNativeQuery(sql);
								query.setParameter("reconciliatedAmount", item.getReconciliatedAmount());
								query.setParameter("remainingAmount", item.getRemainningAmount());
								query.setParameter("id", item.getId());
								query.executeUpdate();
							}
						}				
					}	
				}
			}
			else {			
				log.debug("Write off amount : {}", data.getWriteOffAmount());
				if (data.getWriteOffAmount() != null && data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0) {
					log.debug("Try to create write off : {}", data.getWriteOffAmount());
					WriteOffUnionService service = new WriteOffUnionService(entityManager, username);
					service.writeOff(data, operations.getRecoNumber(), recoDate, username, mode);
					
					log.debug("Write off created!");
				}			
			}
		}
					
		log.debug("Try to save reco log");
		ReconciliationLog recoLog = new ReconciliationLog();
		recoLog.setCreationDate(new Timestamp(System.currentTimeMillis()));
		recoLog.setReconciliation(data.getReconciliationId());
		recoLog.setReconciliationNbr(operations.getRecoNumber());
		recoLog.setRecoType(mode.name());
		recoLog.setUsername(username);
		recoLog.setAction(ReconciliationActions.RECONCILIATION);
		recoLog.setLeftAmount(data.getLeftAmount());
		recoLog.setRigthAmount(data.getRigthAmount());
		recoLog.setBalanceAmount(data.getBalanceAmount());
		recoLog.setWriteoffAmount(data.getWriteOffAmount());	
		reconciliationLogRepository.save(recoLog);
		log.debug("Reco log saved!");
		
		if(data.isAllowReconciliatedAmountLog() && data.getReconciliatedAmountLogGridId() != null) {
			logReconciliatedAmount(data, recoLog, username, mode, operations);
		}
		
		if(refreshPublicationAfterReco) {
			if(data.getLeftGridType() == JoinGridType.GRID || data.getLeftGridType() == JoinGridType.REPORT_GRID) {
				grilleService.publish(data.getLeftGridId());
			}
			if (data.getRightGridType() == JoinGridType.GRID || data.getRightGridType() == JoinGridType.REPORT_GRID) {
				grilleService.publish(data.getRightGridId());
			}
		}
		
		return true;
	}
	
	public void refreshPublication(ReconciliationUnionData data) throws Exception {
		if(data.getLeftGridType() == JoinGridType.GRID || data.getLeftGridType() == JoinGridType.REPORT_GRID) {
			grilleService.publish(data.getLeftGridId());
		}
		if (data.getRightGridType() == JoinGridType.GRID || data.getRightGridType() == JoinGridType.REPORT_GRID) {
			grilleService.publish(data.getRightGridId());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean resetReconciliation(ReconciliationUnionData data, String username, boolean refreshPublicationAfterReset) throws Exception {
		log.debug("Reset reconciliation. Model ID : {}", data.getReconciliationId());
		loadSettings(data);
		List<String> ids = new ArrayList<>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
			data.setRightids(new ArrayList<>(0));
		}
		else if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
			data.setLeftids(new ArrayList<>(0));
		}
				
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		int count = ids.size();
		log.debug("#Rows to reset : {}", count);
		if (count <= 0) {
			log.debug("No rows to reset!");
			throw new BcephalException("No rows to reset!");
		}
		
		if(data.getLeftRecoTypeId() == null) {
			log.debug("Unable to reset reconciliation. The left reco type is NULL!");
			throw new BcephalException("Unable to reset reconciliation. The left reco type is NULL!");
		}
		if(data.getRightRecoTypeId() == null) {
			log.debug("Unable to reset reconciliation. The right reco type is NULL!");
			throw new BcephalException("Unable to reset reconciliation. The right reco type is NULL!");
		}
		
		data.setPerformNeutralization(data.isAllowNeutralization() && data.getLeftNeutralizationAttributeId() != null && data.getRightNeutralizationAttributeId() != null);
		
		
		List<Object[]> numbers = new ArrayList<>(0);
		List<Object[]> partialNumbers = new ArrayList<>(0);
		if(data.getLeftids().size() > 0) {
			String sql = data.buildGetColumnValuesSql(true, data.getLeftRecoTypeId());
			log.trace("Reset left numbers query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			List<Object[]> objs = query.getResultList();
			numbers.addAll(objs);
		}
		if(data.getRightids().size() > 0) {
			String sql = data.buildGetColumnValuesSql(false, data.getRightRecoTypeId());
			log.trace("Reset right numbers query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			List<Object[]> objs = query.getResultList();
			numbers.addAll(objs);
		}
		if(numbers.size() > 0) {
			if(data.isAllowPartialReco() && data.getLeftids().size() > 0) {
				String sql = data.buildGetColumnValuesSql(true, data.getLeftPartialRecoAttributeId());
				log.trace("Reset left partial numbers query : {}", sql);
				Query query = entityManager.createNativeQuery(sql);
				List<Object[]> objs = query.getResultList();
				partialNumbers.addAll(objs);
			}
			if(data.isAllowPartialReco() && data.getRightids().size() > 0) {
				String sql = data.buildGetColumnValuesSql(false, data.getRightPartialRecoAttributeId());
				log.trace("Reset right partial numbers query : {}", sql);
				Query query = entityManager.createNativeQuery(sql);
				List<Object[]> objs = query.getResultList();
				partialNumbers.addAll(objs);
			}
			
						
//			resetenrichment(data,recoAttribute,partialRecoAttribute,numbers,partialnumbers);
			
			String sql = data.buildResetWriteOffSql(numbers);
			if(StringUtils.hasText(sql)) {
				log.trace("Reset writeoff query : {}", sql);
				Query query = entityManager.createNativeQuery(sql);
				int n = 1;
				for(Object parameter : data.getParameters()){
					query.setParameter(n++, parameter);
				}
				query.executeUpdate();
			}
			
			
			List<String> leftSqls = data.buildResetRecoSql(true, numbers, partialNumbers);
			log.trace("Reset reco left query  : {}", leftSqls);
			
			List<String> rightSqls = data.buildResetRecoSql(false, numbers, partialNumbers);		
			log.trace("Reset reco right query : {}", rightSqls);
			
			for(String leftSql : leftSqls) {
				Query query = entityManager.createNativeQuery(leftSql);
				query.executeUpdate();
			}
			for(String rightSql : rightSqls) {		
				Query query = entityManager.createNativeQuery(rightSql);
				query.executeUpdate();
			}
							
						
			for (Object object : numbers) {	
				ReconciliationLog log = new ReconciliationLog();
				log.setCreationDate(new Timestamp(System.currentTimeMillis()));
				log.setReconciliation(data.getReconciliationId());
				log.setReconciliationNbr(object.toString());
				log.setRecoType(RunModes.M.name());
				log.setUsername(username);
				log.setAction(ReconciliationActions.RESET);
				log.setLeftAmount(BigDecimal.ZERO);
				log.setRigthAmount(BigDecimal.ZERO);
				log.setBalanceAmount(BigDecimal.ZERO);
				log.setWriteoffAmount(BigDecimal.ZERO);	
				reconciliationLogRepository.save(log);
			}	
			
			if(data.isAllowReconciliatedAmountLog() && data.getReconciliatedAmountLogGridId() != null) {
				resetLogReconciliatedAmount(data, numbers, partialNumbers);
			}
			
			if(refreshPublicationAfterReset) {
				if(data.getLeftGridType() == JoinGridType.GRID || data.getLeftGridType() == JoinGridType.REPORT_GRID) {
					grilleService.publish(data.getLeftGridId());
				}
				if (data.getRightGridType() == JoinGridType.GRID || data.getRightGridType() == JoinGridType.REPORT_GRID) {
					grilleService.publish(data.getRightGridId());
				}
			}
		}
		
		return true;
	}
	
	
	
	
	private Query buildRecoQuery(ReconciliationUnionData data, ReconciliationUnionOperation operations, String sql, Date recoDate, String username, RunModes mode, boolean forLeft) {
		if(StringUtils.hasText(sql)) {
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("recoNumber", operations.getRecoNumber());
			
			if(data.isPerformNeutralization()) {
				String value = StringUtils.hasText(data.getNeutralizationValue()) ? data.getNeutralizationValue() : operations.getNeutralizeNumber();
				query.setParameter("neutralizationNumber", value);
			}	
			if(operations.isAllowPartialReco()) {
				query.setParameter("partialRecoNumber", operations.getPartialRecoNumber());
			}
			if(data.isAddRecoDate() && (forLeft ? data.getLeftRecoDateId() : data.getRightRecoDateId()) != null) {
				query.setParameter("date", recoDate);
			}
			if(data.isAddNote() && (forLeft ? data.getLeftNoteAttributeId() : data.getRightNoteAttributeId()) != null && StringUtils.hasText(data.getNote())) {
				query.setParameter("note", data.getNote());
			}					

			if(data.isAddUser() && (forLeft ? data.getLeftUserColumnId() : data.getRightUserColumnId()) != null) {
				query.setParameter("username", username);
			}		
			if(data.isAddAutomaticManual() && (forLeft ? data.getLeftModeColumnId() : data.getLeftModeColumnId()) != null) {
				query.setParameter("mode", mode.name());
			}
			
			return query;
		}
		return null;
	}
	
		
	private void logReconciliatedAmount(ReconciliationUnionData data, ReconciliationLog log, String username, RunModes mode, ReconciliationUnionOperation operations) throws Exception {
		Optional<MaterializedGrid> result = materializedGridRepository.findById(data.getReconciliatedAmountLogGridId());
		if(result.isPresent()) {
			MaterializedGrid grid = result.get();
			if(!grid.isPublished()) {
				throw new BcephalException("Materialized grid : '" + grid.getName() + "' is not published!", "");
			}
			MaterializedGridColumn recoAction = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_ACTION, "Reco action", DimensionType.ATTRIBUTE, true);			
			MaterializedGridColumn recoCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_FILTER, "Reco filter", DimensionType.ATTRIBUTE, true);			
			MaterializedGridColumn recoTypeCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_TYPE, "Reco type", DimensionType.ATTRIBUTE, true);						
			MaterializedGridColumn recoNbrCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_NBR, "Reco nbr", DimensionType.ATTRIBUTE, true);	
			MaterializedGridColumn recoAmountCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_AMOUNT, "Reconciliated amount", DimensionType.MEASURE, true);	
			boolean allowPartialReco = data.isAllowPartialReco() && data.getLeftPartialRecoAttributeId() != null && data.getRightPartialRecoAttributeId() != null;
			boolean allowWriteOff = data.getWriteOffAmount() != null && data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0;
			MaterializedGridColumn recoPartialNbrCol = null;
			MaterializedGridColumn recoPartialTypeCol = null;
			if(allowPartialReco) {
				recoPartialNbrCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_PARTIAL_NBR, "Reco partial nbr", DimensionType.ATTRIBUTE, true);
				recoPartialTypeCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_PARTIAL_TYPE, "Reco partial type", DimensionType.ATTRIBUTE, false);
			}
			
			MaterializedGridColumn recoDateCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_DATE, "Reco date", DimensionType.PERIOD, false);			
			MaterializedGridColumn recoUserCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_USER, "Reco user", DimensionType.ATTRIBUTE, false);			
			MaterializedGridColumn recoAmCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_AM, "Reco A/M", DimensionType.ATTRIBUTE, false);			
			MaterializedGridColumn recoWriteOffCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_WRITEOFF, "Reco writeoff", DimensionType.MEASURE, false);
			MaterializedGridColumn recoLeftAmountCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_LEFT_AMOUNT, "Reco left amount", DimensionType.MEASURE, false);
			MaterializedGridColumn recoBalanceAmountCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_BALANCE_AMOUNT, "Reco left amount", DimensionType.MEASURE, false);
			MaterializedGridColumn recoRigthAmountCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_RIGTH_AMOUNT, "Reco rigth amount", DimensionType.MEASURE, false);
						
			String sql = "INSERT INTO " + grid.getMaterializationTableName() + " ( " 
					+ recoAction.getDbColumnName() + ", "
					+ recoCol.getDbColumnName() + ", " 
					+ recoTypeCol.getDbColumnName() + ", "
					+ recoNbrCol.getDbColumnName() + ", " 
					+ recoAmountCol.getDbColumnName();
			
			String values = "VALUES(:action, :reco, :recoType, :recoNbr, :recoAmount";
						
			if(allowPartialReco && recoPartialNbrCol != null) {
				sql += ", " + recoPartialNbrCol.getDbColumnName();
				values += ", :recoPartialNbr";
			}	
			if(allowPartialReco && recoPartialTypeCol != null) {
				sql += ", " + recoPartialTypeCol.getDbColumnName();
				values += ", :recoPartialType";
			}	
			if(data.isAddUser() && recoUserCol != null) {
				sql += ", " + recoUserCol.getDbColumnName();
				values += ", :user";
			}
			if(data.isAddRecoDate() && recoDateCol != null) {
				sql += ", " + recoDateCol.getDbColumnName();
				values += ", :date";
			}
			if(data.isAddAutomaticManual() && recoAmCol != null) {
				sql += ", " + recoAmCol.getDbColumnName();
				values += ", :autoManual";
			}
			if(recoLeftAmountCol != null) {
				sql += ", " + recoLeftAmountCol.getDbColumnName();
				values += ", :leftAmount";
			}
			if(recoRigthAmountCol != null) {
				sql += ", " + recoRigthAmountCol.getDbColumnName();
				values += ", :rigthAmount";
			}
			if(recoBalanceAmountCol != null) {
				sql += ", " + recoBalanceAmountCol.getDbColumnName();
				values += ", :balanceAmount";
			}
			if(allowWriteOff && recoWriteOffCol != null) {
				sql += ", " + recoWriteOffCol.getDbColumnName();
				values += ", :writeOff";
			}
							
			int i = 1;
			for(EnrichmentValue enrichmentItemData : data.getEnrichmentItemDatas()) {				
				if(ReconciliationModelSide.LOG_GRID.name().equalsIgnoreCase(enrichmentItemData.getSide())) {
					String col = new MaterializedGridColumn(enrichmentItemData.getDimensionId()).getDbColumnName();
					sql += ", " + col;
					if(enrichmentItemData.hasNullValue()) {
						values += ", null";
					}
					else {
						values += ", :enrichmentItemData" + i++;
					}
				}								
			}
						
			sql += ") " + values;
			
			sql += ")";
			
			ReconciliationUnionModel filter =  getById(data.getReconciliationId());
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("action", "RECO");
			query.setParameter("reco", filter != null ? filter.getName() : "");
			query.setParameter("recoType", "");
			query.setParameter("recoNbr", operations.getRecoNumber());
			query.setParameter("recoAmount", data.getReconciliatedAmount());			
			if(allowPartialReco && recoPartialNbrCol != null) {
				query.setParameter("recoPartialNbr", operations.getPartialRecoNumber());
			}	
			if(allowPartialReco && recoPartialTypeCol != null) {
				query.setParameter("recoPartialType", "");
			}
			if(data.isAddUser() && recoUserCol != null) {
				query.setParameter("user", username);
			}
			if(data.isAddRecoDate() && recoDateCol != null) {
				query.setParameter("date", log.getCreationDate());
			}
			if(data.isAddAutomaticManual() && recoAmCol != null) {
				query.setParameter("autoManual", mode.name());
			}			
			if(recoLeftAmountCol != null) {
				query.setParameter("leftAmount", data.getLeftAmount() != null ? data.getLeftAmount() : BigDecimal.ZERO);
			}
			if(recoRigthAmountCol != null) {
				query.setParameter("rigthAmount", data.getRigthAmount() != null ? data.getRigthAmount() : BigDecimal.ZERO);
			}	
			if(recoBalanceAmountCol != null) {
				query.setParameter("balanceAmount", data.getBalanceAmount() != null ? data.getBalanceAmount() : BigDecimal.ZERO);
			}
			if(allowWriteOff && recoWriteOffCol != null) {
				query.setParameter("writeOff", data.getWriteOffAmount() != null ? data.getWriteOffAmount() : BigDecimal.ZERO);
			}
			
			i = 1;	
			for(EnrichmentValue enrichmentItemData : data.getEnrichmentItemDatas()) {
				if(ReconciliationModelSide.LOG_GRID.name().equalsIgnoreCase(enrichmentItemData.getSide())) {
					if(!enrichmentItemData.hasNullValue()) {
						query.setParameter("enrichmentItemData" + i++, enrichmentItemData.getValue());
					}
					
				}
			}
			query.executeUpdate();
		}
	}
	
	private void resetLogReconciliatedAmount(ReconciliationUnionData data, List<Object[]> numbers, List<Object[]> partialnumbers) throws BcephalException {
		try {
			Optional<MaterializedGrid> result = materializedGridRepository.findById(data.getReconciliatedAmountLogGridId());
			if(result.isPresent()) {
				MaterializedGrid grid = result.get();
				if(!grid.isPublished()) {
					return;
				}
				MaterializedGridColumn recoAction = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_ACTION, "Reco action", DimensionType.ATTRIBUTE, true);	
				MaterializedGridColumn recoTypeCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_TYPE, "Reco type", DimensionType.ATTRIBUTE, true);						
				MaterializedGridColumn recoNbrCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_NBR, "Reco nbr", DimensionType.ATTRIBUTE, true);	
				boolean allowPartialReco = data.isAllowPartialReco() && data.getLeftPartialRecoAttributeId() != null && data.getRightPartialRecoAttributeId() != null;
				MaterializedGridColumn recoPartialNbrCol = null;
				if(allowPartialReco) {
					recoPartialNbrCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_PARTIAL_NBR, "Reco partial nbr", DimensionType.ATTRIBUTE, true);
				}
				
				String columns = "";
				String values = "";
				String coma = "";
				String recoActionCol = recoAction.getDbColumnName();
				for(MaterializedGridColumn column : grid.getColumns()) {
					String col = column.getDbColumnName();
					columns += coma + col;
					if(col.equalsIgnoreCase(recoActionCol)) {
						values += coma + "'RESET'";
					}else {
						values += coma + col;
					}
					coma = ",";
				}				
				@SuppressWarnings("unused")
				String sql = "INSERT INTO " + grid.getMaterializationTableName() 
					+ " (" + columns + ") SELECT " + values + " FROM " + grid.getMaterializationTableName() 
					+ " WHERE " + recoTypeCol.getDbColumnName() + " = :recoType"			
					+ " AND (" + recoNbrCol.getDbColumnName() + " IN (";
	
				coma = "";
				String inPart = "";
				for (Object object : numbers) {
					inPart += coma + "'" +  object + "'";
					coma = ", ";
					
				}				
				sql += inPart + ")";
				
				if(allowPartialReco && !partialnumbers.isEmpty()) {
					sql += " OR " + recoPartialNbrCol.getDbColumnName() + " IN (";	
					coma = "";
					for (Object object : partialnumbers) {
						sql += coma + "'" +  object + "'";
						coma = ", ";
					}
					sql += ")";
				}				
				sql += ")";
//				Optional<Attribute> att = attributeRepository.findById(data.getRecoTypeId());			
//				Query query = entityManager.createNativeQuery(sql);
//				query.setParameter("recoType", att.get().getName());
//				query.executeUpdate();
			}			
		}
		catch (Exception e) {
			log.error("Unable to reset log reconciliated amount", e);
			if(e instanceof BcephalException) {
				throw e;
			}
		}
	}
	
	private MaterializedGridColumn getLogRecoGridColumn(MaterializedGrid grid, GrilleColumnCategory role, String name, DimensionType type, boolean mandatory) throws BcephalException {
		MaterializedGridColumn column = grid.getColumnByRole(role);
		if(column == null) {
			if(mandatory) {
				throw new BcephalException("Mandatory colunm : '" + name + "' is not found in materialized grid : '" + grid.getName() + "'!", "");
			}
			return null;
		}
		if(column.getType() != type) {
			throw new BcephalException("Materialized grid : '" + grid.getName() + "' - Column : '" + column.getName() + "' type must be : " + type, "");
		}	
		if(!column.isPublished()) {
			throw new BcephalException("Materialized grid : '" + grid.getName() + "' - Column : '" + column.getName() + "' is not published!", "");
		}		
		return column;
	}
	
		
	
	
	
	private void performenrichment(ReconciliationUnionData data) throws Exception {
		int count = data.getEnrichmentItemDatas() != null ? data.getEnrichmentItemDatas().size() : 0;
		if(count > 0) {				
			List<String> leftSqls = data.buildEnrichmentSql(true, data);
			log.trace("Reco enrichment left query : {}", leftSqls);			
			List<String> rightSqls = data.buildEnrichmentSql(false, data);		
			log.trace("Reco enrichment query : {}", rightSqls);
			
			for(String leftSql : leftSqls) {
				Query query = buildEnrichmentQuery(data, ReconciliationModelSide.LEFT, leftSql);
				if(query != null) {
					query.executeUpdate();
				}
			}
			for(String rightSql : rightSqls) {		
				Query query = buildEnrichmentQuery(data, ReconciliationModelSide.RIGHT, rightSql);
				if(query != null) {
					query.executeUpdate();
				}	
			}			
		}
	}
		
	private Query buildEnrichmentQuery(ReconciliationUnionData data, ReconciliationModelSide side, String sql) throws Exception {
		Query query = entityManager.createNativeQuery(sql);
		int i = 1;	
		for(EnrichmentValue enrichmentValue : data.getEnrichmentItemDatas()) {
			if(enrichmentValue.getValue() != null) {					
				if(side.name().equalsIgnoreCase(enrichmentValue.getSide())) {
					query.setParameter("enrichmentItemData" + i++, enrichmentValue.getValue());
				}
			}
		}
		return query;
	}

	protected void resetenrichment(ReconciliationUnionData data, Attribute recoAttribute, Attribute partialRecoAttribute, List<Object[]> numbers, List<Object[]> partialnumbers) throws Exception {
		int count = data.getEnrichmentItemDatas() != null ? data.getEnrichmentItemDatas().size() : 0;
		if(count > 0) {
			List<String> leftSqls = data.buildResetEnrichmentSql(true, data, numbers, partialnumbers);
			log.trace("Reco reset enrichment left query : {}", leftSqls);			
			List<String> rightSqls = data.buildResetEnrichmentSql(false, data, numbers, partialnumbers);		
			log.trace("Reco reset enrichment query : {}", rightSqls);
			
			for(String leftSql : leftSqls) {
				Query query = entityManager.createNativeQuery(leftSql);
				if(query != null) {
					query.executeUpdate();
				}
			}
			for(String rightSql : rightSqls) {		
				Query query = entityManager.createNativeQuery(rightSql);
				if(query != null) {
					query.executeUpdate();
				}	
			}			
		}
	}
			
	protected void buildEnrichmentData(ReconciliationData data, ReconciliationModel model) throws Exception {
		data.getEnrichmentItemDatas().clear();
		for(ReconciliationModelEnrichment enrichment : model.getEnrichmentListChangeHandler().getItems()) {
			buildEnrichmentItemData(data, model, enrichment);
		}
	}

	private void buildEnrichmentItemData(ReconciliationData data, ReconciliationModel model, ReconciliationModelEnrichment enrichment) throws Exception {		
		
		GrilleColumn targetColumn = getColumn(enrichment.getTargetSide().isLeft() ? model.getLeftGrid() : model.getRigthGrid(), enrichment.getTargetColumnId());
        GrilleColumn sourceColumn = getColumn(enrichment.getSourceSide().isLeft() ? model.getLeftGrid() : model.getRigthGrid(), enrichment.getSourceColumnId());
		
        if (targetColumn != null) {
        	EnrichmentValue enrichmentValue = new EnrichmentValue();
        	enrichmentValue.setSide(enrichment.getTargetSide().name());
        	enrichmentValue.setDimensionType(targetColumn.getType());
        	enrichmentValue.setDimensionId(targetColumn.getDimensionId());
            
            if (enrichment.getSourceSide().isCustom()) {
            	enrichmentValue.setDecimalValue(enrichment.getDecimalValue());
            	enrichmentValue.setStringValue(enrichment.getStringValue());
            	enrichmentValue.setDateValue(enrichment.getDateValue());
                data.getEnrichmentItemDatas().add(enrichmentValue);
            }
            else if(sourceColumn != null) {
            	Object value = getGridItemValue(data, sourceColumn, enrichment.getSourceSide());
                if(value != null) {
                    if (targetColumn.isPeriod() && value instanceof Date) {
                    	enrichmentValue.setDateValue(new PeriodValue());
                    	enrichmentValue.getDateValue().setDateOperator(PeriodOperator.SPECIFIC);
                    	enrichmentValue.getDateValue().setDateValue((Date)value);
                    }
                    else if (targetColumn.isMeasure()) {
                        try {
                        	enrichmentValue.setDecimalValue(new BigDecimal(value.toString()));
                        }
                        catch (Exception e) {

                        }
                    }
                    else if (targetColumn.isAttribute()) {
                    	enrichmentValue.setStringValue(value.toString());
                    }
                }                        
                data.getEnrichmentItemDatas().add(enrichmentValue);
            }
            else {
                throw new BcephalException("Enrichment item at position " + enrichment.getPosition() + " is wrong!");
            }
        }
        else {
        	throw new BcephalException("Enrichment item at position " + enrichment.getPosition() + " is wrong!");
        }        
	}
	
	
	private Object getGridItemValue(ReconciliationData data, GrilleColumn sourceColumn, ReconciliationModelSide side) throws Exception {
		List<Long> ids = new ArrayList<>(0);
		if(side.isLeft()) {					
			ids.addAll(data.getLeftids());
		}
		else if(side.isRight()) {
			ids.addAll(data.getRightids());
		}
		if(ids.size() > 0) {
			if (sourceColumn.isPeriod()) {
				return getEnrichmentValue(new Period(sourceColumn.getDimensionId()), ids);
            }
            else if (sourceColumn.isMeasure()) {
            	return getEnrichmentValue(new Measure(sourceColumn.getDimensionId()), ids);
            }
            else if (sourceColumn.isAttribute()) {
            	return getEnrichmentValue(new Attribute(sourceColumn.getDimensionId()), ids);
            }
		}
		return null;
	}

	public String getEnrichmentValue(Attribute attribute, List<Long> ids) throws Exception {
		try {
			String col = attribute.getUniverseTableColumnName();
			String sql ="SELECT DISTINCT " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " WHERE " + col + " IS NOT NULL AND " + col + " <> '' AND id ";	
			if(ids.size() == 1) {
				sql += " = " + ids.get(0);
			}
			else {
				sql += " IN ( ";
				String coma = "";
				for(Long oid : ids) {
					sql += coma + oid;
					coma = ", ";
				}
				sql += " )";
			}
			Query query = entityManager.createNativeQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(1);			
			String value = (String) query.getSingleResult();
			return value;
		} catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		} catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
			throw new BcephalException("Unable to retrive enrichment value");
		}		
		return null;
	}

	public Date getEnrichmentValue(Period period, List<Long> oids) throws Exception {
		try {
			String col = period.getUniverseTableColumnName();
			String sql ="SELECT DISTINCT " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " WHERE " + col + " IS NOT NULL AND id ";	
			if(oids.size() == 1) {
				sql += " = " + oids.get(0);
			}
			else {
				sql += " IN ( ";
				String coma = "";
				for(Long oid : oids) {
					sql += coma + oid;
					coma = ", ";
				}
				sql += " )";
			}
			Query query = entityManager.createNativeQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(1);			
			Date value = (Date) query.getSingleResult();
			return value;
		} 
		catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		} catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
			throw new BcephalException("Unable to retrive enrichment value");
		}		
		return null;
	}
		

	public BigDecimal getEnrichmentValue(Measure measure, List<Long> oids) throws Exception {
		try {
			String col = measure.getUniverseTableColumnName();
			String sql ="SELECT DISTINCT " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " WHERE " + col + " IS NOT NULL AND id ";	
			if(oids.size() == 1) {
				sql += " = " + oids.get(0);
			}
			else {
				sql += " IN ( ";
				String coma = "";
				for(Long oid : oids) {
					sql += coma + oid;
					coma = ", ";
				}
				sql += " )";
			}
			Query query = entityManager.createNativeQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(1);			
			BigDecimal value = (BigDecimal) query.getSingleResult();
			return value;
		} 
		catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		}	catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
			throw new BcephalException("Unable to retrive enrichment value");
		}		
		return null;
	}


	private GrilleColumn getColumn(Grille grid, Long columnId) {
        if (columnId != null) {
            for (GrilleColumn column : grid.getColumnListChangeHandler().getItems())
            {
                if (columnId.equals(column.getId())) return column;
            }
        }
        return null;
    }
	
	
	
	
	
	public boolean containsReconciliatedItems(Long recoTypeOid, List<Long> ids) {
		if (ids == null || ids.isEmpty() || recoTypeOid == null) {
			return false;
		}
		Attribute recoAttribute = new Attribute(recoTypeOid);
		try {			
			String sql = "SELECT COUNT(1) FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " WHERE (" + recoAttribute.getUniverseTableColumnName() + " IS NOT NULL"
					+ " AND " + recoAttribute.getUniverseTableColumnName() + " != '') "
				    + " AND " + UniverseParameters.ID + " IN (";
			String coma = "";
			for (Long oid : ids) {
				sql += coma + oid;
				coma = ", ";
			}
			sql += ")";			
			
			Query query = entityManager.createNativeQuery(sql);
			Number count = (Number) query.getSingleResult();
			return count.longValue() > 0;
		} 
		catch (Exception ex) {
			log.error("Unable to check reconciliated items",  ex);
			throw new BcephalException("Unable to check reconciliated items.");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean unfreeze(ReconciliationUnionData data, String username) throws Exception {
		log.debug("Reconciliation model ID : {}", data.getReconciliationId());
		loadSettings(data);
		List<String> ids = new ArrayList<>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
		}
		if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
		}
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		int count = ids.size();
		log.debug("#Rows to unfreeze : {}", count);
		if (count <= 0) {
			log.debug("No rows to unfreeze!");
			throw new BcephalException("No rows to unfreeze!");
		}
				
		if(data.getLeftFreezeAttributeId() == null) {
			log.debug("Unable to freeze. The left freeze type is NULL!");
			throw new BcephalException("Unable to freeze. The left freeze type is NULL!");
		}
		if(data.getRightFreezeAttributeId() == null) {
			log.debug("Unable to freeze. The right freeze type is NULL!");
			throw new BcephalException("Unable to freeze. The right freeze type is NULL!");
		}
		
		
		List<Object[]> numbers = new ArrayList<>(0);
		if(data.getLeftids().size() > 0) {
			String sql = data.buildGetColumnValuesSql(true, data.getLeftFreezeAttributeId());
			log.trace("Reset left numbers query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			List<Object[]> objs = query.getResultList();
			numbers.addAll(objs);
		}
		if(data.getRightids().size() > 0) {
			String sql = data.buildGetColumnValuesSql(false, data.getRightFreezeAttributeId());
			log.trace("Reset right numbers query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			List<Object[]> objs = query.getResultList();
			numbers.addAll(objs);
		}
		if(numbers.size() > 0) {			
			List<String> leftSqls = data.buildUnfreezeSql(true, numbers);
			log.trace("Unfreeze left query  : {}", leftSqls);
			
			List<String> rightSqls = data.buildUnfreezeSql(false, numbers);		
			log.trace("Unfreeze right query : {}", rightSqls);
			
			for(String leftSql : leftSqls) {
				Query query = entityManager.createNativeQuery(leftSql);
				query.executeUpdate();
			}
			for(String rightSql : rightSqls) {		
				Query query = entityManager.createNativeQuery(rightSql);
				query.executeUpdate();
			}
							
						
			for (Object object : numbers) {	
				ReconciliationLog log = new ReconciliationLog();
				log.setCreationDate(new Timestamp(System.currentTimeMillis()));
				log.setReconciliation(data.getReconciliationId());
				log.setReconciliationNbr(object.toString());
				log.setRecoType(RunModes.M.name());
				log.setUsername(username);
				log.setAction(ReconciliationActions.UNFREEZE);
				log.setLeftAmount(BigDecimal.ZERO);
				log.setRigthAmount(BigDecimal.ZERO);
				log.setBalanceAmount(BigDecimal.ZERO);
				log.setWriteoffAmount(BigDecimal.ZERO);	
				reconciliationLogRepository.save(log);
			}	
			
		}
		
		
		
//		Attribute freezeAttribute = new Attribute(data.getFreezeAttributeId());
//
//		loadSettings(data);				
//		
//		List<Object[]> numbers = new ArrayList<>(0);
//		try {
//			String sql = "SELECT DISTINCT " + freezeAttribute.getUniverseTableColumnName()
//						+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//						+ " WHERE " + freezeAttribute.getUniverseTableColumnName() + " IS NOT NULL AND id IN (";			
//			String coma = "";
//			for (Number oid : ids) {
//				sql += coma + oid;
//				coma = ", ";
//			}
//			sql += ")";
//			Query query = entityManager.createNativeQuery(sql);
//			numbers = query.getResultList();					
//		} catch(Exception ex) {			
//		
//		}
//		
//		if(!numbers.isEmpty()){
//			try {	
//				String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//						+ " SET " + freezeAttribute.getUniverseTableColumnName() + " = NULL";
//				if(data.isAddUser() && data.getUserAttribute() != null) {
//					sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = NULL";
//				}
//				if(data.isAddRecoDate() && data.getRecoDateId() != null) {
//					sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = NULL";
//				}
//				if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
//					sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = NULL";
//				}
//				sql += " WHERE " + freezeAttribute.getUniverseTableColumnName() + " IN (";
//					
//				String sql2 = "DELETE FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//						+ " WHERE " + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.WRITEOFF + "'"
//						+ " AND " + freezeAttribute.getUniverseTableColumnName() + " IN (";
//	
//				String coma = "";
//				String inPart = "";
//				for (Object object : numbers) {					
//					inPart += coma + "'" +  object + "'";
//					coma = ", ";
//					
//					ReconciliationLog log = new ReconciliationLog();
//					log.setCreationDate(new Timestamp(System.currentTimeMillis()));
//					log.setReconciliation(data.getReconciliationId());
//					log.setReconciliationNbr(object.toString());
//					log.setRecoType(RunModes.M.name());
//					log.setUsername(username);
//					log.setAction(ReconciliationActions.UNFREEZE);
//					log.setLeftAmount(BigDecimal.ZERO);
//					log.setRigthAmount(BigDecimal.ZERO);
//					log.setBalanceAmount(BigDecimal.ZERO);
//					log.setWriteoffAmount(BigDecimal.ZERO);	
//					reconciliationLogRepository.save(log);
//					
//				}				
//				sql += inPart + ")";
//				sql2 += inPart + ")";
//				Query query = entityManager.createNativeQuery(sql2);
//				query.executeUpdate();
//	
//				query = entityManager.createNativeQuery(sql);
//				query.executeUpdate();
//				
//				return true;
//			} 
//			catch (BcephalException e) {
//				log.debug(e.getMessage());
//				throw e;
//			} catch (Exception e) {
//				log.error("Unable to reset freeze",  e);
//				throw new BcephalException("Unable to reset freeze.");
//			}
//			finally {
//
//			}
//		}
		return true;
	}
			
	@Transactional
	public boolean freeze(ReconciliationUnionData data, String username, RunModes mode) throws Exception {
		loadSettings(data);
		try {
			freezeWithoutCommint(data, username,  mode);
			return true;
		} 
		catch (BcephalException e) {
			log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error during freezing",  e);
			if(e instanceof BcephalException) {
				throw e;
			}
			throw new BcephalException("Unexpected error during freezing.");
		}
	}
	
	public boolean freezeWithoutCommint(ReconciliationUnionData data, String username, RunModes mode) throws Exception {
		log.debug("Reconciliation model ID : {}", data.getReconciliationId());
		List<String> ids = new ArrayList<>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
		}
		if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
		}
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		int count = ids.size();
		log.debug("#Rows to freeze : {}", count);
		if (count <= 0) {
			log.debug("No rows to freeze!");
			throw new BcephalException("No rows to freeze!");
		}
				
		if(data.getLeftFreezeAttributeId() == null) {
			log.debug("Unable to freeze. The left freeze type is NULL!");
			throw new BcephalException("Unable to freeze. The left freeze type is NULL!");
		}
		if(data.getRightFreezeAttributeId() == null) {
			log.debug("Unable to freeze. The right freeze type is NULL!");
			throw new BcephalException("Unable to freeze. The right freeze type is NULL!");
		}
		if(data.getFreezeSequenceId() == null) {
			log.debug("Unable to freeze. The freeze sequence is NULL!");
			throw new BcephalException("Unable to freeze. The freeze sequence is NULL!");
		}
		
		IncrementalNumber freezeSequence = null;
		String freezeNumber = null;
		Optional<IncrementalNumber> resp = incrementalNumberRepository.findById(data.getFreezeSequenceId());
		if(!resp.isEmpty()) {
			freezeSequence = resp.get();
			log.debug("Sequence found : {}", freezeSequence.getName());
		}
		else {
			log.debug("Sequence not found! : {}", data.getFreezeSequenceId());
		}
		if(freezeSequence != null) {
			log.debug("Try to build sequence next number : {}", freezeSequence.getName());
			freezeNumber =  freezeSequence.buildNextValue();
		}
		
		log.debug("Freeze number : {}", freezeNumber);
		
		data.setAllowPartialReco(false);
		
		List<String> leftSqls = data.buildFreezeSql(true, username, mode);
		log.trace("Freeze left query : {}", leftSqls);
		
		List<String> rightSqls = data.buildFreezeSql(false, username, mode);		
		log.trace("Freeze right query : {}", rightSqls);
		
		Date recoDate = new Date();
		for(String leftSql : leftSqls) {
			Query query = buildFreezeQuery(data, freezeNumber, leftSql, recoDate, username, mode, false);
			if(query != null) {
				query.executeUpdate();
			}	
		}
		for(String rightSql : rightSqls) {		
			Query query = buildFreezeQuery(data, freezeNumber, rightSql, recoDate, username, mode, false);
			if(query != null) {
				query.executeUpdate();
			}	
		}
		
		if(freezeSequence != null) {
			incrementalNumberRepository.save(freezeSequence);
		}
		
		log.debug("Try to save reco log");
		ReconciliationLog recoLog = new ReconciliationLog();
		recoLog.setCreationDate(new Timestamp(System.currentTimeMillis()));
		recoLog.setReconciliation(data.getReconciliationId());
		recoLog.setReconciliationNbr(freezeNumber);
		recoLog.setRecoType(mode.name());
		recoLog.setUsername(username);
		recoLog.setAction(ReconciliationActions.FREEZE);
		recoLog.setLeftAmount(data.getLeftAmount());
		recoLog.setRigthAmount(data.getRigthAmount());
		recoLog.setBalanceAmount(data.getBalanceAmount());
		recoLog.setWriteoffAmount(data.getWriteOffAmount());	
		reconciliationLogRepository.save(recoLog);
		log.debug("Reco log saved!");
		
		return true;
	}
	
	private Query buildFreezeQuery(ReconciliationUnionData data, String freezeNumber, String sql, Date recoDate, String username, RunModes mode, boolean forLeft) {
		if(StringUtils.hasText(sql)) {
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("freezeNumber", freezeNumber);
			if(data.isAddUser() && (forLeft ? data.getLeftUserColumnId() : data.getRightUserColumnId()) != null) {
				query.setParameter("username", username);
			}		
			if(data.isAddAutomaticManual() && (forLeft ? data.getLeftModeColumnId() : data.getLeftModeColumnId()) != null) {
				query.setParameter("mode", mode.name());
			}
			if(data.isAddRecoDate() && (forLeft ? data.getLeftRecoDateId() : data.getRightRecoDateId()) != null) {
				query.setParameter("date", recoDate);
			}
			if(data.isAddNote() && (forLeft ? data.getLeftNoteAttributeId() : data.getRightNoteAttributeId()) != null && StringUtils.hasText(data.getNote())) {
				query.setParameter("note", data.getNote());
			}
			return query;
		}
		return null;
	}
	
	
	@Transactional
	public boolean neutralize(ReconciliationUnionData data, String username, RunModes mode, boolean refreshPublicationAfterReco) throws Exception {
		log.debug("Neutralization - Reconciliation model ID : {}", data.getReconciliationId());
		if(data.getLeftRecoTypeId() == null) {
			log.debug("Unable to neutralize. The left reco type is NULL!");
			throw new BcephalException("Unable to neutralize. The left reco type is NULL!");
		}	
		if(data.getRightRecoTypeId() == null) {
			log.debug("Unable to neutralize. The right reco type is NULL!");
			throw new BcephalException("Unable to neutralize. The right reco type is NULL!");
		}	
		if(!data.isAllowNeutralization()) {
			log.debug("Unable to neutralize. The neutralization operation is not aalowed!");
			throw new BcephalException("Neutralization not allowed.");
		}        
		if(data.getLeftNeutralizationAttributeId() == null) {
			log.debug("Unable to neutralize. The left neutralization attribute is NULL!");
			throw new BcephalException("Unable to neutralize. The left neutralization attribute is NULL!");
		}
		if(data.getRightNeutralizationAttributeId() == null) {
			log.debug("Unable to neutralize. The right neutralization attribute is NULL!");
			throw new BcephalException("Unable to neutralize. The right neutralization attribute is NULL!");
		}
		
		data.setPerformNeutralization(true);
		data.setPerformPartialReco(data.isAllowPartialReco() && data.getLeftReconciliatedMeasureId() != null && data.getLeftRemainningMeasureId() != null
				&& data.getRightReconciliatedMeasureId() != null && data.getRightRemainningMeasureId() != null);
		return reconciliate(data, username, mode, refreshPublicationAfterReco);		
	}
	
	public boolean neutralizeWithoutCommint(ReconciliationUnionData data, String username, RunModes mode, boolean refreshPublicationAfterReco) throws Exception {
		log.debug("Neutralization - Reconciliation model ID : {}", data.getReconciliationId());
		if(data.getLeftRecoTypeId() == null) {
			log.debug("Unable to neutralize. The left reco type is NULL!");
			throw new BcephalException("Unable to neutralize. The left reco type is NULL!");
		}	
		if(data.getRightRecoTypeId() == null) {
			log.debug("Unable to neutralize. The right reco type is NULL!");
			throw new BcephalException("Unable to neutralize. The right reco type is NULL!");
		}	
		if(!data.isAllowNeutralization()) {
			log.debug("Unable to neutralize. The neutralization operation is not aalowed!");
			throw new BcephalException("Neutralization not allowed.");
		}        
		if(data.getLeftNeutralizationAttributeId() == null) {
			log.debug("Unable to neutralize. The left neutralization attribute is NULL!");
			throw new BcephalException("Unable to neutralize. The left neutralization attribute is NULL!");
		}
		if(data.getRightNeutralizationAttributeId() == null) {
			log.debug("Unable to neutralize. The right neutralization attribute is NULL!");
			throw new BcephalException("Unable to neutralize. The right neutralization attribute is NULL!");
		}
		
		data.setPerformNeutralization(true);
		data.setPerformPartialReco(data.isAllowPartialReco() && data.getLeftReconciliatedMeasureId() != null && data.getLeftRemainningMeasureId() != null
				&& data.getRightReconciliatedMeasureId() != null && data.getRightRemainningMeasureId() != null);
		loadSettings(data);
		return reconciliateWithoutCommint(data, username, mode, refreshPublicationAfterReco);
	}
        
	@Transactional
  	public boolean unneutralize(ReconciliationUnionData data, String username) throws Exception {
		List<String> ids = new ArrayList<>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
		}
		if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
		}
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		if (data.getReconciliationId() == null || ids.isEmpty() || data.getLeftRecoTypeId() == null || data.getRightRecoTypeId() == null) {
			return false;
		}
		return false;
  	}

	@Transactional
	public BrowserDataPage<GridItem> refreshReconciliatedAmounts2(UnionGridFilter filter, java.util.Locale locale) throws Exception {
		return refreshReconciliatedAmounts2(filter, locale, true);
	}
	
	@Transactional
	public BrowserDataPage<GridItem> refreshReconciliatedAmounts2(UnionGridFilter filter, java.util.Locale locale, boolean loadClosures) throws Exception {
		GrilleRowType type = filter.getRowType();
		if(filter.getRecoData() != null && filter.getRecoData().isAllowPartialReco() && filter.getRecoData().getRecoAttributeId() != null && filter.getRecoData().getPartialRecoAttributeId() != null 
				&& filter.getRecoData().getRemainningMeasureId() != null && filter.getRecoData().getReconciliatedMeasureId() != null && filter.getRecoData().getAmountMeasureId() != null) {
			
			Long gridId = filter.getRecoData().getMainGridId();			
			UnionGrid grid = filter.getUnionGrid() != null ? filter.getUnionGrid() : getUnionGrid(JoinGridType.UNION_GRID, gridId);
			if(loadClosures) {
				unionGridService.loadFilterClosures(grid, true);
			}
			
			RefreshUnionRecoAmountQueryBuilder builder = new RefreshUnionRecoAmountQueryBuilder(filter, grid);
			List<String> sqls = builder.buildQueries();
			for(String sql : sqls) {			
				log.debug("Refresh reco amount query : {}", sql);
				if(StringUtils.hasText(sql)) {
					Query query = this.entityManager.createNativeQuery(sql);
					query.executeUpdate();		
				}
			}
			filter.setRowType(GrilleRowType.NOT_RECONCILIATED);
		}
		filter.setRowType(type);
		return unionGridService.searchRows(filter, locale);
	}

	@Transactional
	public EditorData<ReconciliationModel> publish(Long id, Locale locale) {
		try {
			//ReconciliationModel m = getById(id);
			return new EditorData<ReconciliationModel>();
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to publish the grid", ex);
			String message = getMessageSource().getMessage("unable.to.publish.grid", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public EditorData<ReconciliationModel> resetPublication(Long id, Locale locale) {
		try {
			//ReconciliationModel m = getById(id);
			return new EditorData<ReconciliationModel>();
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to reset the grid", ex);
			String message = getMessageSource().getMessage("unable.to.reset.grid", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}

	@Transactional
	public boolean refreshPublication(Long id, Locale locale) {
		try {
			//ReconciliationModel m = getById(id);
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to refresh the grid", ex);
			String message = getMessageSource().getMessage("unable.to.refresh.grid", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public void loadSettings(ReconciliationUnionData data) {
		if(data.isAddAutomaticManual()) {
			Attribute autoManualAttribute = getAttribute(ReconciliationParameterCodes.RECONCILIATION_AUTO_MANUAL_ATTRIBUTE);
			AttributeValue automaticValue = getAttributeValue(ReconciliationParameterCodes.RECONCILIATION_AUTOMATIC_VALUE);
			AttributeValue manualValue = getAttributeValue(ReconciliationParameterCodes.RECONCILIATION_MANUAL_VALUE);
			data.setAutoManualAttribute(autoManualAttribute);
			data.setAutomaticValue(automaticValue);
			data.setManualValue(manualValue);
		}
		if(data.isAddUser()) {
			Attribute userAttribute = getAttribute(ReconciliationParameterCodes.RECONCILIATION_USER_ATTRIBUTE);
			data.setUserAttribute(userAttribute);
		}
//		if(data.isAddNote()) {
//			Attribute noteAttribute = getAttribute(ReconciliationParameterCodes.RECONCILIATION_NOTE_ATTRIBUTE);
//			data.setNoteAttribute(noteAttribute);
//		}
		if(data.getLeftGridType() == JoinGridType.UNION_GRID) {
			data.setLeftGrid(getUnionGrid(data.getLeftGridType(), data.getLeftGridId()));
			unionGridService.loadFilterClosures(data.getLeftGrid(), true);
			if(data.getLeftBrowserDataFilter() != null) {
				data.getLeftBrowserDataFilter().setUnionGrid(data.getLeftGrid());
			}
		}
		else {
			data.setLeftGrille(getGrille(data.getLeftGridType(), data.getLeftGridId()));
		}
		
		if(data.getRightGridType() == JoinGridType.UNION_GRID) {
			data.setRightGrid(getUnionGrid(data.getRightGridType(), data.getRightGridId()));
			unionGridService.loadFilterClosures(data.getRightGrid(), true);
			if(data.getRightBrowserDataFilter() != null) {
				data.getRightBrowserDataFilter().setUnionGrid(data.getRightGrid());
			}
		}
		else {
			data.setRightGrille(getGrille(data.getRightGridType(), data.getRightGridId()));
		}
		
	}
	
	/**
	 * @param parameterCode
	 * @return
	 */
	protected Attribute getAttribute(String parameterCode){
		Parameter parameter = parameterRepository.findByCodeAndParameterType(parameterCode, ParameterType.ATTRIBUTE);
		if(parameter != null && parameter.getLongValue() != null) {
			Optional<Attribute> result = attributeRepository.findById(parameter.getLongValue());
			if(result.isPresent()) {
				return result.get();
			}
		}		
		return null;
	}
	
	/**
	 * @param parameterCode
	 * @return
	 */
	public AttributeValue getAttributeValue(String parameterCode){
		Parameter parameter = parameterRepository.findByCodeAndParameterType(parameterCode, ParameterType.ATTRIBUTE_VALUE);
		if(parameter != null) {
			if(parameter.getLongValue() != null) {
				Optional<AttributeValue> result = attributeValueRepository.findById(parameter.getLongValue());
				if(result.isPresent()) {
					return result.get();
				}
			}	
			if(parameter.getStringValue() != null) {
				AttributeValue value = new AttributeValue();
				value.setName(parameter.getStringValue());
				return value;
			}	
		}		
		return null;
	}
	
		
	public Object getFieldValue(ReconciliationUnionModelGrid grid, Long dimensionId, DimensionType dimensionType, List<String> ids) {
		if(grid.isUnion()) {
			return getFieldValueForUnion(grid, dimensionId, dimensionType, ids);
		}
		else {
			return getFieldValueForUniverse(grid, dimensionId, dimensionType, ids);
		}	
	}
	
	private Object getFieldValueForUniverse(ReconciliationUnionModelGrid grid, Long dimensionId, DimensionType dimensionType, List<String> ids) {
		try {
			String col = null;
			String stringPart = "";
			if (dimensionType.isPeriod()) {
				col = new Period(dimensionId).getUniverseTableColumnName();
            }
            else if (dimensionType.isMeasure()) {
            	col = new Measure(dimensionId).getUniverseTableColumnName();
            }
            else if (dimensionType.isAttribute()) {
            	col = new Attribute(dimensionId).getUniverseTableColumnName();
            	stringPart = "AND " + col + " <> ''";
            }			
			String sql ="SELECT DISTINCT " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " WHERE " + col + " IS NOT NULL " + stringPart + " AND id ";	
			if(ids.size() == 1) {
				sql += " = " + ids.get(0);
			}
			else {
				sql += " IN ( ";
				String coma = "";
				for(String oid : ids) {
					sql += coma + oid;
					coma = ", ";
				}
				sql += " )";
			}
			Query query = entityManager.createNativeQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(1);			
			Object value = query.getSingleResult();
			return value;			
		} 
		catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		}	
		catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
		}		
		return null;
	}
	
	private Object getFieldValueForUnion(ReconciliationUnionModelGrid grid, Long dimensionId, DimensionType dimensionType, List<String> ids) {
		try {
			UnionGridColumn column = grid.getGrid().getColumnById(dimensionId);
			String sql = "";
			String coma = "";	
			int count = 0;
			for (UnionGridItem gridItem : grid.getGrid().getItems()) {				
				SmartMaterializedGridColumn c = column.getColumnByGridId(gridItem.getGrid().getId());
				String col = c != null ? c.getDbColumnName() : "null";
				if (!StringUtils.hasText(col)) {
					continue;
				}
				
				String stringPart = dimensionType.isAttribute() ? "AND " + col + " <> ''" : "";
				String itemsql = "SELECT " + col + " FROM " + gridItem.getGrid().getDbTableName()
						+ " WHERE " + col + " IS NOT NULL " + stringPart;	
				
				List<Number> result = new ReconciliationUnionData().buildIds(gridItem.getGrid().getId(), ids);
				if(result.isEmpty()) {
					itemsql = null;
				}
				else {
					String or = " AND ( ";
					for (Number id : result) {
						itemsql += or + " ID = " + id;
						or = " OR ";
					}	
					itemsql += ") LIMIT 1";
				}
								
				if (StringUtils.hasText(itemsql)) {
					sql += coma + itemsql;
					coma = " UNION ";
					count++;
				}
			}		
			
			Query query = entityManager.createNativeQuery(sql);
			if(count > 1) {
				query.setFirstResult(0);
				query.setMaxResults(1);	
			}
			Object value = query.getSingleResult();
			return value;			
		} 
		catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		}	
		catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
		}		
		return null;
	}
	
	
	public String getWriteOffFieldAttributeValue(Attribute attribute, List<Long> ids) {
		try {
			String col = attribute.getUniverseTableColumnName();
			String sql ="SELECT DISTINCT " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " WHERE " + col + " IS NOT NULL AND " + col + " <> '' AND id ";	
			if(ids.size() == 1) {
				sql += " = " + ids.get(0);
			}
			else {
				sql += " IN ( ";
				String coma = "";
				for(Long oid : ids) {
					sql += coma + oid;
					coma = ", ";
				}
				sql += " )";
			}
			Query query = entityManager.createNativeQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(1);			
			String value = (String) query.getSingleResult();
			return value;
		} 
		catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		}
		catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
		}		
		return null;
	}

	public Date getWriteOffFieldPeriodValue(Period period, List<Long> ids) {
		try {
			Query query = getWriteOffFieldValueQuery(period, ids);
			Date value = (Date) query.getSingleResult();
			return value;
		} 
		catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		}	
		catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
		}		
		return null;
	}
		

	public BigDecimal getWriteOffFieldMeasureValue(Measure measure, List<Long> ids) {
		try {			
			Query query = getWriteOffFieldValueQuery(measure, ids);
			BigDecimal value = (BigDecimal) query.getSingleResult();
			return value;
		} 
		catch (NoResultException ex) {
			log.trace("Threre is no result.");			
		}	catch (Exception ex) {
			log.error("Unable to retrieve field value ", ex);
		}		
		return null;
	}
	
	private Query getWriteOffFieldValueQuery(Dimension dimension, List<Long> ids) {
		String col = dimension.getUniverseTableColumnName();
		String sql ="SELECT DISTINCT " + col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME 
				+ " WHERE " + col + " IS NOT NULL AND id ";	
		if(ids.size() == 1) {
			sql += " = " + ids.get(0);
		}
		else {
			sql += " IN ( ";
			String coma = "";
			for(Long oid : ids) {
				sql += coma + oid;
				coma = ", ";
			}
			sql += " )";
		}
		Query query = entityManager.createNativeQuery(sql);
		query.setFirstResult(0);
		query.setMaxResults(1);	
		return query;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> getSelectAllRowsIds(UnionGridFilter filter) throws Exception {	
		List<String> ids = new ArrayList<>();	
		if(filter.getUnionGrid() != null) {
			UnionSelectAllRowsQueryBuilder builder = new UnionSelectAllRowsQueryBuilder(filter);
			builder.setParameterRepository(parameterRepository);			
			String sql = builder.buildIdsQuery();
			log.trace("Select IDs query : {}", sql);
			Query query = this.entityManager.createNativeQuery(sql);
			for(var key : builder.getParameters().keySet()){
				query.setParameter(key, builder.getParameters().get(key));
			}
			ids = (List<String>)query.getResultList();
		}
		else if(filter.getGrid() != null) {
			filter.loadFilterRecoDataForGrid();
			grilleService.loadFilterClosures(filter);	
			SelectAllRowsQueryBuilder builder = new SelectAllRowsQueryBuilder(filter);
			((SelectAllRowsQueryBuilder)builder).setParameterRepository(parameterRepository);			
			String sql = builder.buildIdsQuery();
			log.trace("Select IDs query : {}", sql);
			Query query = this.entityManager.createNativeQuery(sql);
			List<Long> longids = (List<Long>)query.getResultList();
			for(Long id : longids) {
				ids.add("" + id);
			}
		}				
		return ids;
	}

	
	public BrowserDataPage<GridItem> selectAllRows(UnionGridFilter filter, java.util.Locale locale) throws Exception {		
		BrowserDataPage<GridItem> page = new BrowserDataPage<GridItem>();
		GridItem item = new GridItem(new Object[] {BigDecimal.ZERO, BigDecimal.ZERO, -100});
		page.getItems().add(item);	
		page.setPageSize(filter.getPageSize());
		if(filter.getUnionGrid() != null) {
			unionGridService.loadFilterClosures(filter, true);
			UnionSelectAllRowsQueryBuilder builder = new UnionSelectAllRowsQueryBuilder(filter);
			builder.setParameterRepository(parameterRepository);
						
//			if(filter.getDebitCreditAttributeId() == null) {
//				Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
//				if(parameter != null && parameter.getStringValue() != null) {
//					filter.setDebitValue(parameter.getStringValue());
//				}
//				else {
//					filter.setDebitValue("D");
//				}
//				parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
//				if(parameter != null && parameter.getStringValue() != null) {
//					filter.setCreditValue(parameter.getStringValue());
//				}
//				else {
//					filter.setCreditValue("C");
//				}
//			}
						
			String sql = builder.buildCountQuery();
			log.trace("Count query : {}", sql);
			Query query = this.entityManager.createNativeQuery(sql);
			for(var key : builder.getParameters().keySet()){
				query.setParameter(key, builder.getParameters().get(key));
			}
			Number number = (Number)query.getSingleResult();
			Integer count = number.intValue();
			if(count == 0) {
				return page;
			}
			
			page.setTotalItemCount(count);
			page.setPageCount(1);
			page.setCurrentPage(1);
			page.setPageFirstItem(1);
						
			BigDecimal amount1 =  BigDecimal.ZERO;
			BigDecimal amount2 =  BigDecimal.ZERO;
			
			int pageSize = 50000;
			int currentPage = 1;
			int firstItem = ((currentPage - 1) * pageSize) + 1;
			sql = builder.buildQuery();
			log.trace("Search query : {}", sql);
			while(firstItem <= count) {				
				query = this.entityManager.createNativeQuery(sql);
				for(var key : builder.getParameters().keySet()){
					query.setParameter(key, builder.getParameters().get(key));
				}
				query.setFirstResult(firstItem - 1);
				query.setMaxResults(pageSize);
				@SuppressWarnings("unchecked")
				List<Object[]> rows = query.getResultList();
						
				BigDecimal[] amounts = buildAmounts(filter, rows);
				amount1 = amount1.add(amounts[0]);
				amount2 = amount2.add(amounts[1]);
				
				currentPage++;
				firstItem = ((currentPage - 1) * pageSize) + 1;
			}	
			
			item.datas[0] = amount1;
			item.datas[1] = amount2;
			
			log.debug("Row found : {}", count);
		}		
		return page;
	}

	protected BigDecimal[] buildAmounts(GrilleDataFilter filter, List<Object[]> rows) {
		BigDecimal[] amounts = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};		
        boolean useCreditDebit = filter.getRecoData().isUseDebitCredit();	
		for (Object[] row : rows) {
			BigDecimal amount = row[0] != null ? new BigDecimal(row[0].toString()) : BigDecimal.ZERO;
			String cdValue = getDebitCreditValue(filter, row);
			boolean isDebitRow = StringUtils.hasText(cdValue) && cdValue.equals(filter.getDebitValue());	
			if((useCreditDebit && isDebitRow) || (!useCreditDebit && amount.compareTo(BigDecimal.ZERO) < 0)) {
        		amounts[1] = amounts[1].add(amount);
        	}
        	else {
        		amounts[0] = amounts[0].add(amount);
        	}
		}
		return amounts;
	}

	private String getDebitCreditValue(GrilleDataFilter filter, Object[] row) {
		String value = null;
		int position = 1;
		if(row.length > position && row[position] instanceof String) {
			value = (String)row[position];
		}
		return value;
	}

	@Transactional
	public ReconciliationUnionModel createRecoColumns(UnionGridCreateColumnRequest request, Locale locale) throws Exception {
		if(request == null) {
			throw new BcephalException("Union grid creation request is NULL!");
		}
		if(request.getModelId()== null) {
			throw new BcephalException("Union grid reco model ID is NULL!");
		}
		ReconciliationUnionModel model = getById(request.getModelId());
		if(model == null) {
			throw new BcephalException("Union grid reco model not found!");
		}
		
		if(model.getLeftGrid() != null) {
			createRecoColumns(request, model, ReconciliationModelSide.LEFT, locale);
		}
		if(model.getRigthGrid() != null) {
			createRecoColumns(request, model, ReconciliationModelSide.RIGHT, locale);
		}
		model = save(model, locale);
		return model;
	}
	
	private void createRecoColumns(UnionGridCreateColumnRequest request, ReconciliationUnionModel model,
			ReconciliationModelSide side, Locale locale) throws Exception {
		ReconciliationUnionModelGrid modelGrid = side.isLeft() ? model.getLeftGrid() : model.getRigthGrid();
		
		if(modelGrid != null && modelGrid.isUnion() && modelGrid.getGrid() != null) {
			createRecoUnionColumns(modelGrid, request, model, side, locale);
		}
		else if(modelGrid != null && modelGrid.isUniverse() && modelGrid.getGrille() != null) {
			createRecoUniverseColumns(modelGrid, request, model, side, locale);
		}
	}
	
	private void createRecoUniverseColumns(ReconciliationUnionModelGrid modelGrid, UnionGridCreateColumnRequest request, ReconciliationUnionModel model, ReconciliationModelSide side, Locale locale) throws Exception {
		
		if(modelGrid.getGrille() != null) {
			UnionGridCreateColumnRequestData data = side.isLeft() ? request.getLeftData() : request.getRightData();
			
			GrilleColumn column = StringUtils.hasText(data.getRecoName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getRecoName(), locale) : null;
			if(column != null) {
				modelGrid.setRecoTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getPartialRecoName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getPartialRecoName(), locale) : null;
			if(column != null) {
				modelGrid.setPartialRecoTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getNoteName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getNoteName(), locale) : null;
			if(column != null) {
				modelGrid.setNoteTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getNeutralizationName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getNeutralizationName(), locale) : null;
			if(column != null) {
				modelGrid.setNeutralizationTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getFreezeName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getFreezeName(), locale) : null;
			if(column != null) {
				modelGrid.setFreezeTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getUserName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getUserName(), locale) : null;
			if(column != null) {
				modelGrid.setUserColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getAutomaticName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getAutomaticName(), locale) : null;
			if(column != null) {
				modelGrid.setModeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getDebitCreditName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.ATTRIBUTE, data.getDebitCreditName(), locale) : null;
			if(column != null) {
				modelGrid.setDebitCreditColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getMeasureName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.MEASURE, data.getMeasureName(), locale) : null;
			if(column != null) {
				modelGrid.setMeasureColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getReconciliatedAmountName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.MEASURE, data.getReconciliatedAmountName(), locale) : null;
			if(column != null) {
				modelGrid.setReconciliatedMeasureColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getRemainingAmountName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.MEASURE, data.getRemainingAmountName(), locale) : null;
			if(column != null) {
				modelGrid.setRemainningMeasureColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getDeltaMeasureName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.MEASURE, data.getDeltaMeasureName(), locale) : null;
			if(column != null && model.getWriteOffModel() != null) {
				model.getWriteOffModel().setWriteOffMeasureId(column.getId());
			}
			
			column = StringUtils.hasText(data.getRecoDateName()) 
					? grilleService.createColumn(modelGrid.getGrille(), DimensionType.PERIOD, data.getRecoDateName(), locale) : null;
			if(column != null) {
				modelGrid.setRecoDateColumnId(column.getId());
			}				
		}
	}
	

	private void createRecoUnionColumns(ReconciliationUnionModelGrid modelGrid, UnionGridCreateColumnRequest request, ReconciliationUnionModel model, ReconciliationModelSide side, Locale locale) {
		
		if(modelGrid.getGrid() != null) {
			UnionGridCreateColumnRequestData data = side.isLeft() ? request.getLeftData() : request.getRightData();
			
			UnionGridColumn column = StringUtils.hasText(data.getRecoName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getRecoName(), locale) : null;
			if(column != null) {
				modelGrid.setRecoTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getPartialRecoName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getPartialRecoName(), locale) : null;
			if(column != null) {
				modelGrid.setPartialRecoTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getNoteName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getNoteName(), locale) : null;
			if(column != null) {
				modelGrid.setNoteTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getNeutralizationName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getNeutralizationName(), locale) : null;
			if(column != null) {
				modelGrid.setNeutralizationTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getFreezeName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getFreezeName(), locale) : null;
			if(column != null) {
				modelGrid.setFreezeTypeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getUserName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getUserName(), locale) : null;
			if(column != null) {
				modelGrid.setUserColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getAutomaticName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getAutomaticName(), locale) : null;
			if(column != null) {
				modelGrid.setModeColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getDebitCreditName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.ATTRIBUTE, data.getDebitCreditName(), locale) : null;
			if(column != null) {
				modelGrid.setDebitCreditColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getMeasureName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.MEASURE, data.getMeasureName(), locale) : null;
			if(column != null) {
				modelGrid.setMeasureColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getReconciliatedAmountName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.MEASURE, data.getReconciliatedAmountName(), locale) : null;
			if(column != null) {
				modelGrid.setReconciliatedMeasureColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getRemainingAmountName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.MEASURE, data.getRemainingAmountName(), locale) : null;
			if(column != null) {
				modelGrid.setRemainningMeasureColumnId(column.getId());
			}
			
			column = StringUtils.hasText(data.getDeltaMeasureName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.MEASURE, data.getDeltaMeasureName(), locale) : null;
			if(column != null && model.getWriteOffModel() != null) {
				model.getWriteOffModel().setWriteOffMeasureId(column.getId());
			}
			
			column = StringUtils.hasText(data.getRecoDateName()) 
					? unionGridService.createColumn(modelGrid.getGrid(), DimensionType.PERIOD, data.getRecoDateName(), locale) : null;
			if(column != null) {
				modelGrid.setRecoDateColumnId(column.getId());
			}				
		}
	}
	
	
	
	public BrowserDataPage<Object> searchDimensionValues(DimensionDataFilter filter, Locale locale) throws Exception {
		if(filter.getDataSourceType() == DataSourceType.UNION_GRID) {
			return unionGridService.searchDimensionValues(filter, locale);
		}
		else {
			if(filter.getDataSourceType() != DataSourceType.MATERIALIZED_GRID) {
				filter.loadFilterRecoDataForGrid();
			}
			return grilleService.searchAttributeValues(filter, locale);
		}
	}
	
	@Override
	public Long copy(Long id, String newName, Locale locale) {
		log.debug("Try to copy entity : {} as {}", id, newName);
		List<ReconciliationUnionModel> objects = getAllByName(newName);
		if(objects.size() > 0) {
			throw new BcephalException("Duplicate name : " + newName);
		}
		ReconciliationUnionModel item = getById(id);
		if(item != null) {
			ReconciliationUnionModel copy = getCopy(item);
			copy.setName(newName);
			ReconciliationUnionModel result = save(copy, locale);
			result.getLeftGrid().RefreshColumnIdAfterCopy(item.getLeftGrid());
			result.getRigthGrid().RefreshColumnIdAfterCopy(item.getRigthGrid());
			if(result.isAllowWriteOff()) {
				if(result.getWriteOffModel() != null && result.getWriteOffModel().getWriteOffSide() != null && result.getWriteOffModel().getWriteOffSide().isLeft()) {
					result.getWriteOffModel().RefreshColumnIdAfterCopy(item.getWriteOffModel(), item.getLeftGrid(), result.getLeftGrid());
				}else {
					result.getWriteOffModel().RefreshColumnIdAfterCopy(item.getWriteOffModel(), item.getRigthGrid(), result.getRigthGrid());
				}
			}
			result = save(result, locale);
			return result.getId();
		}
		return null;
	}

}
