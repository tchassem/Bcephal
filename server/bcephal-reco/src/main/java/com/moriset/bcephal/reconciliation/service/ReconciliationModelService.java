/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.RefreshRecoAmountQueryBuilder;
import com.moriset.bcephal.reconciliation.domain.PartialRecoItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationActions;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationLog;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModel;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelEditorData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelEnrichment;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.repository.ReconciliationConditionRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationLogRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationModelEnrichmentRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationModelRepository;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class ReconciliationModelService extends MainObjectService<ReconciliationModel, BrowserData> {

	@Autowired
	ReconciliationModelRepository reconciliationModelRepository;
	
	@Autowired
	ReconciliationModelEnrichmentRepository reconciliationModelEnrichmentRepository;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	ReconciliationConditionRepository reconciliationConditionRepository;
	
	@Autowired
	EntityRepository entityRepository;
	
	@Autowired
	GrilleService grilleService;
	
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
	WriteOffModelService writeOffModelService;
	
	@Autowired
	MaterializedGridRepository materializedGridRepository;
	
	
	
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
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
	
	
	@Override
	public ReconciliationModelRepository getRepository() {
		return reconciliationModelRepository;
	}
	
	@Override
	public ReconciliationModelEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		ReconciliationModelEditorData data = new ReconciliationModelEditorData();
		if(filter.isNewData()) {
			data.setItem(getNewItem());
		}
		else {
			data.setItem(getById(filter.getId()));
		}
		initEditorData(data, session, locale);
		if(data.getItem() != null && data.getItem().getGroup() == null) {
			data.getItem().setGroup(getDefaultGroup());
		}	
		return data;
	}
	
	@Override
	public ReconciliationModel getById(Long id) {
		ReconciliationModel item = super.getById(id);
		if(item != null) {
			if(item.getLeftGrid() != null) {
				item.getLeftGrid().setRowType(GrilleRowType.NOT_RECONCILIATED);
			}
			if(item.getRigthGrid() != null) {
				item.getRigthGrid().setRowType(GrilleRowType.NOT_RECONCILIATED);
			}
		}
		return item;
	}
	
	@Override
	protected void initEditorData(EditorData<ReconciliationModel> data_, HttpSession session, Locale locale) throws Exception {
		ReconciliationModelEditorData data = (ReconciliationModelEditorData)data_;
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
	public RightEditorData<ReconciliationModel> getRightLowLevelEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		RightEditorData<ReconciliationModel> data = super.getRightLowLevelEditorData(filter, session, locale);
		if(filter != null && StringUtils.hasText(filter.getSubjectType())) {
			data.setItems(reconciliationModelRepository.findGenericAllAsNameables());
		}
		return data;
	}
	
	@Override
	protected ReconciliationModel getNewItem() {
		ReconciliationModel reconciliationModel = new ReconciliationModel();
		String baseName = "Reconciliation Model ";
		int i = 1;
		reconciliationModel.setName(baseName + i);
		while(getByName(reconciliationModel.getName()) != null) {
			i++;
			reconciliationModel.setName(baseName + i);
		}
		
		Grille grid = new Grille();
		grid.setName("Left");
		grid.setType(GrilleType.RECONCILIATION);
		grid.setRowType(GrilleRowType.NOT_RECONCILIATED);
		reconciliationModel.setLeftGrid(grid);
		
		grid = new Grille();
		grid.setName("Rigth");
		grid.setType(GrilleType.RECONCILIATION);
		grid.setRowType(GrilleRowType.NOT_RECONCILIATED);
		reconciliationModel.setRigthGrid(grid);
		
		grid = new Grille();
		grid.setName("Bottom");
		grid.setType(GrilleType.RECONCILIATION);
		reconciliationModel.setBottomGrid(grid);;
		
		return reconciliationModel;
	}
	
		
	@Override
	@Transactional
	public ReconciliationModel save(ReconciliationModel reconciliationModel, Locale locale) {
		log.debug("Try to  Save ReconciliationModel : {}", reconciliationModel);		
		try {	
			if(reconciliationModel == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.reconciliation.model", new Object[]{reconciliationModel} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if(!StringUtils.hasLength(reconciliationModel.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.reconciliation.model.with.empty.name", new String[]{reconciliationModel.getName()} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			
			
			ListChangeHandler<ReconciliationModelEnrichment> enrichments = reconciliationModel.getEnrichmentListChangeHandler();
			ListChangeHandler<ReconciliationCondition> conditions = reconciliationModel.getConditionListChangeHandler();
			
			reconciliationModel.setModificationDate(new Timestamp(System.currentTimeMillis()));
			if(reconciliationModel.getLeftGrid() != null) {
				//reconciliationModel.getLeftGrid().setRowType(GrilleRowType.NOT_RECONCILIATED);
				grilleService.save(reconciliationModel.getLeftGrid(), locale);
			}
			if(reconciliationModel.getRigthGrid() != null) {
				//reconciliationModel.getRigthGrid().setRowType(GrilleRowType.NOT_RECONCILIATED);
				grilleService.save(reconciliationModel.getRigthGrid(), locale);
			}
			if(reconciliationModel.getBottomGrid() != null) {
				grilleService.save(reconciliationModel.getBottomGrid(), locale);
			}
			if(reconciliationModel.getWriteOffModel() != null) {
				writeOffModelService.save(reconciliationModel.getWriteOffModel(), locale);
			}
			validateBeforeSave(reconciliationModel, locale);
			reconciliationModel = getRepository().save(reconciliationModel);
			ReconciliationModel id = reconciliationModel;
			
			enrichments.getNewItems().forEach( item -> {
				log.trace("Try to save ReconciliationModelEnrichment : {}", item);
				item.setModel(id);
				reconciliationModelEnrichmentRepository.save(item);
				log.trace("ReconciliationModelEnrichment saved : {}", item.getId());
			});
			enrichments.getUpdatedItems().forEach( item -> {
				log.trace("Try to save ReconciliationModelEnrichment : {}", item);
				item.setModel(id);
				reconciliationModelEnrichmentRepository.save(item);
				log.trace("ReconciliationModelEnrichment saved : {}", item.getId());
			});
			enrichments.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete ReconciliationModelEnrichment : {}", item);
					reconciliationModelEnrichmentRepository.deleteById(item.getId());
					log.trace("ReconciliationModelEnrichment deleted : {}", item.getId());
				}
			});
			
			conditions.getNewItems().forEach( item -> {
				log.trace("Try to save Reconciliation Condition : {}", item);
				item.setRecoModelId(id);
				reconciliationConditionRepository.save(item);
				log.trace("Reconciliation Condition saved : {}", item.getId());
			});
			conditions.getUpdatedItems().forEach( item -> {
				log.trace("Try to save Reconciliation Condition : {}", item);
				item.setRecoModelId(id);
				reconciliationConditionRepository.save(item);
				log.trace("Reconciliation Condition saved : {}", item.getId());
			});
			conditions.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete Reconciliation Condition : {}", item);
					reconciliationConditionRepository.deleteById(item.getId());
					log.trace("Reconciliation Condition deleted : {}", item.getId());
				}
			});
			
			log.debug("ReconciliationModel saved : {} ", reconciliationModel.getId());
	        return reconciliationModel;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save ReconciliationModel : {}", reconciliationModel, e);
			String message = getMessageSource().getMessage("unable.to.save.reconciliation.model", new Object[]{reconciliationModel} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public void delete(ReconciliationModel reconciliationModel) {
		log.debug("Try to delete ReconciliationModel : {}", reconciliationModel);	
		if(reconciliationModel == null || reconciliationModel.getId() == null) {
			return;
		}
		
		ListChangeHandler<ReconciliationModelEnrichment> enrichments = reconciliationModel.getEnrichmentListChangeHandler();
		ListChangeHandler<ReconciliationCondition> conditions = reconciliationModel.getConditionListChangeHandler();
		enrichments.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete AutoRecoCommonDimension : {}", item);
				reconciliationModelEnrichmentRepository.deleteById(item.getId());
				log.trace("AutoRecoCommonDimension deleted : {}", item.getId());
			}
		});
		enrichments.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete AutoRecoCommonDimension : {}", item);
				reconciliationModelEnrichmentRepository.deleteById(item.getId());
				log.trace("AutoRecoCommonDimension deleted : {}", item.getId());
			}
		});
		
		conditions.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Reconciliation Condition : {}", item);
				reconciliationConditionRepository.deleteById(item.getId());
				log.trace("Reconciliation Condition deleted : {}", item.getId());
			}
		});
		conditions.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Reconciliation Condition : {}", item);
				reconciliationConditionRepository.deleteById(item.getId());
				log.trace("Reconciliation Condition deleted : {}", item.getId());
			}
		});
		if(reconciliationModel.getWriteOffModel() != null) {
			writeOffModelService.delete(reconciliationModel.getWriteOffModel());
		}
		getRepository().deleteById(reconciliationModel.getId());
		log.debug("ReconciliationModel successfully to delete : {} ", reconciliationModel);
	    return;	
	}

	@Override
	protected BrowserData getNewBrowserData(ReconciliationModel item) {
		return new BrowserData(item);

	}

	@Override
	protected Specification<ReconciliationModel> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<ReconciliationModel> qBuilder = new RequestQueryBuilder<ReconciliationModel>(root, query, cb);
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
	public boolean reconciliate(ReconciliationData data, String username, RunModes mode) throws Exception {
		loadSettings(data);
		try {
			reconciliateWithoutCommint(data, username,  mode);
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
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reconciliateAndCommit(List<ReconciliationData> datas, String username, RunModes mode) throws Exception {
		for(ReconciliationData data : datas) {
			reconciliateWithoutCommint(data, username, mode);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void reconciliateAndCommit2(List<ReconciliationData> datas, String username, RunModes mode) throws Exception {
		reconciliateAndCommit(datas, username, mode);
	}
	
	//@Transactional(propagation = Propagation.REQUIRED)
	public boolean reconciliateAndNotCommit(ReconciliationData data, String username, RunModes mode) throws Exception {
		return reconciliateWithoutCommint(data, username, mode);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void commit() {
		log.trace("Commit...");
	}
	
	public boolean reconciliateWithoutCommint(ReconciliationData data, String username, RunModes mode) throws Exception {
		log.debug("Reconciliation model ID : {}", data.getReconciliationId());
		List<Long> ids = new ArrayList<Long>(0);
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
			return false;
		}
		
		if(data.getRecoTypeId() == null) {
			log.debug("Unable to reconciliate. The reco type is NULL!");
			throw new BcephalException("Undefined reco type.");
		}
		
		
		ReconciliationOperation operations = new ReconciliationOperation(data);
		operations.setEntityManager(entityManager);
		operations.setAttributeRepository(attributeRepository);
		operations.setMeasureRepository(measureRepository);
		operations.setIncrementalNumberRepository(incrementalNumberRepository);		
		operations.build(ids);

		String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
				+ operations.getRecoAttribute().getUniverseTableColumnName() + " = :recoNumber";
		
		if(data.isPerformNeutralization()) {
			sql += ", " + operations.getNeutralizeAttribute().getUniverseTableColumnName() + " = :neutralizationNumber";
		}		
		if(operations.isAllowPartialReco()) {
			sql += ", " + operations.getPartialRecoAttribute().getUniverseTableColumnName() + " = :partialRecoNumber";
		}		
		if(data.isAddUser() && data.getUserAttribute() != null) {
			sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = :user";
		}
		if(data.isAddRecoDate() && data.getRecoDateId() != null) {
			sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = :date";
		}
		if(data.isAddAutomaticManual()  && data.getAutoManualAttribute() != null) {
			sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = :autoManual";
		}
		if(data.isAddNote() && data.getNoteAttributeId() != null && StringUtils.hasText(data.getNote())) {
			sql += ", " + new Attribute(data.getNoteAttributeId()).getUniverseTableColumnName() + " = :note";
		}
		
		String or = " WHERE ";
		for (Number oid : ids) {
			sql += or + " ID = " + oid;
			or = " OR ";
		}				
		
		log.trace("Reco query : {}", sql);
		
		Date recoDate = new Date();
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("recoNumber", operations.getRecoNumber());
		
		if(data.isPerformNeutralization()) {
			query.setParameter("neutralizationNumber", operations.getNeutralizeNumber());
		}	
		if(operations.isAllowPartialReco()) {
			query.setParameter("partialRecoNumber", operations.getPartialRecoNumber());
		}
		
		if(data.isAddUser() && data.getUserAttribute() != null) {
			query.setParameter("user", username);
		}
		if(data.isAddRecoDate() && data.getRecoDateId() != null) {
			query.setParameter("date", Calendar.getInstance().getTime());
		}
		if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
			if(mode == RunModes.A) {
				if(data.getAutomaticValue() != null) {
					query.setParameter("autoManual", data.getAutomaticValue().getName());
				}
				else {
					query.setParameter("autoManual", RunModes.A.name());
				}
			}
			else {
				if(data.getManualValue() != null) {
					query.setParameter("autoManual", data.getManualValue().getName());
				}
				else {
					query.setParameter("autoManual", RunModes.M.name());
				}
			}
		}
		if(data.isAddNote() && data.getNoteAttributeId() != null && StringUtils.hasText(data.getNote())) {
			query.setParameter("note", data.getNote());
		}
		
		query.executeUpdate();
		
		performenrichment(data);
		
		
		if(data.isAllowPartialReco() && data.isPerformPartialReco()) {
			if(mode == RunModes.A) {					
				or = "";
				String leftSql = "";
				for (Number oid : data.getLeftids()) {
					leftSql += or + " id = " + oid;
					or = " OR ";
				}
				
				or = "";
				String rigthSql = "";
				for (Number oid : data.getRightids()) {
					rigthSql += or + " id = " + oid;
					or = " OR ";
				}
				if(StringUtils.hasText(leftSql)) {
					sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
							+ operations.getReconciliatedMeasure().getUniverseTableColumnName() + " = " + operations.getLeftMeasure().getUniverseTableColumnName() + ", "
							+ operations.getRemainningMeasure().getUniverseTableColumnName() + " = 0 "
									+ "WHERE ";
					query = entityManager.createNativeQuery(sql + leftSql);
					query.executeUpdate();
				}
				if(StringUtils.hasText(rigthSql)) {
					sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
							+ operations.getReconciliatedMeasure().getUniverseTableColumnName() + " = " + operations.getRigthMeasure().getUniverseTableColumnName() + ", "
							+ operations.getRemainningMeasure().getUniverseTableColumnName() + " = 0 "
									+ "WHERE ";
					query = entityManager.createNativeQuery(sql + rigthSql);
					query.executeUpdate();
				}					
			}
			else {
				sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
						+ operations.getReconciliatedMeasure().getUniverseTableColumnName() + " = :reconciliatedAmount, "
								+ operations.getRemainningMeasure().getUniverseTableColumnName() + " = :remainingAmount "
										+ "WHERE id = :id";
				for(PartialRecoItem item : data.getPartialRecoItems()) {
					query = entityManager.createNativeQuery(sql);
					query.setParameter("id", item.getId());
					if(data.isAllowNeutralization() && data.isPerformNeutralization()) {
						query.setParameter("reconciliatedAmount", item.getAmount());
						query.setParameter("remainingAmount", BigDecimal.ZERO);
					}
					else {
						query.setParameter("reconciliatedAmount", item.getReconciliatedAmount());
						query.setParameter("remainingAmount", item.getRemainningAmount());
					}
					query.executeUpdate();
				}
			}
			
		}
		else {
			
			log.debug("Write off amount : {}", data.getWriteOffAmount());
			if (data.getWriteOffAmount() != null && data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0) {
				log.debug("Try to create write off : {}", data.getWriteOffAmount());
				WriteOffService service = new WriteOffService(entityManager, username);
				service.writeOff(data, operations.getRecoAttribute(), operations.getRecoNumber(), recoDate, mode);
				if(operations.getReconciliatedMeasure() != null && operations.getRemainningMeasure() != null) {
					sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
							+ operations.getReconciliatedMeasure().getUniverseTableColumnName() + " = :reconciliatedAmount, "
									+ operations.getRemainningMeasure().getUniverseTableColumnName() + " = :remainingAmount "
											+ "WHERE id = :id";
					for(PartialRecoItem item : data.getPartialRecoItems()) {						
						query = entityManager.createNativeQuery(sql);
						query.setParameter("reconciliatedAmount", item.getAmount());
						query.setParameter("remainingAmount", BigDecimal.ZERO);
						query.setParameter("id", item.getId());
						query.executeUpdate();
					}
				}
				log.debug("Write off created!");
			}
			
		}
		
		log.debug("Try to save reco type attribute : {}", operations.getRecoAttribute().getName());
		if(operations.getRecoAttribute() != null) {
			attributeRepository.save(operations.getRecoAttribute());
		}
		if(operations.getPartialRecoAttribute() != null) {
			attributeRepository.save(operations.getPartialRecoAttribute());
		}
		if(operations.getRecoSequence() != null) {
			incrementalNumberRepository.save(operations.getRecoSequence());
		}
		if(operations.getPartialRecoSequence() != null) {
			incrementalNumberRepository.save(operations.getPartialRecoSequence());
		}
		if(data.isPerformNeutralization()) {
			if(operations.getNeutralizeAttribute() != null) {
				attributeRepository.save(operations.getNeutralizeAttribute());
			}
			if(operations.getNeutralizeSequence() != null) {
				incrementalNumberRepository.save(operations.getNeutralizeSequence());
			}
		}
		log.debug("Reco type attribute saved : {}", operations.getRecoAttribute().getName());
					
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
		
		return true;
	}
		
	private void logReconciliatedAmount(ReconciliationData data, ReconciliationLog log, String username, RunModes mode, ReconciliationOperation operations) throws Exception {
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
			boolean allowPartialReco = data.isAllowPartialReco() && data.getPartialRecoAttributeId() != null;
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
			
			ReconciliationModel filter =  getById(data.getReconciliationId());
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("action", "RECO");
			query.setParameter("reco", filter != null ? filter.getName() : "");
			query.setParameter("recoType", "" + operations.getRecoAttribute().getName());
			query.setParameter("recoNbr", operations.getRecoNumber());
			query.setParameter("recoAmount", data.getReconciliatedAmount());			
			if(allowPartialReco && recoPartialNbrCol != null) {
				query.setParameter("recoPartialNbr", operations.getPartialRecoNumber());
			}	
			if(allowPartialReco && recoPartialTypeCol != null) {
				query.setParameter("recoPartialType", operations.getPartialRecoAttribute().getName());
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
	
	private void resetLogReconciliatedAmount(ReconciliationData data, List<Object[]> numbers, List<Object[]> partialnumbers) throws BcephalException {
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
				boolean allowPartialReco = data.isAllowPartialReco() && data.getPartialRecoAttributeId() != null;
				MaterializedGridColumn recoPartialNbrCol = null;
				if(allowPartialReco) {
					recoPartialNbrCol = getLogRecoGridColumn(grid, GrilleColumnCategory.RECO_PARTIAL_NBR, "Reco partial nbr", DimensionType.ATTRIBUTE, true);
				}
				
//				String sql = "DELETE FROM " + grid.getMaterializationTableName() 
//						+ " WHERE " + recoTypeCol.getDbColumnName() + " = :recoType"			
//						+ " AND (" + recoNbrCol.getDbColumnName() + " IN (";
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
				Optional<Attribute> att = attributeRepository.findById(data.getRecoTypeId());			
				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("recoType", att.get().getName());
				query.executeUpdate();
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
	
		
	
	
	
	private void performenrichment(ReconciliationData data) throws Exception {
		int count = data.getEnrichmentItemDatas() != null ? data.getEnrichmentItemDatas().size() : 0;
		if(count > 0) {			
			String leftsql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET ";
			String rightsql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET ";
			int i = 1;	
			String leftcoma = "";
			String rigthcoma = "";
			boolean hasLeftSide = false;
			boolean hasRightSide = false;
			int position = 0;
			for(EnrichmentValue enrichmentValue : data.getEnrichmentItemDatas()) {
				if(!enrichmentValue.isValid()) {
					throw new BcephalException("Invalid enrichement at : " + position);
				}
				position++;
				
				String col = enrichmentValue.getUniverseTableColumnName();
				if(ReconciliationModelSide.LEFT.name().equalsIgnoreCase(enrichmentValue.getSide())) {
					if(enrichmentValue.getValue() != null) {
						leftsql += leftcoma + col  + " = :enrichmentItemData" + i++;
					}
					else {
						leftsql += leftcoma + col  + " = NULL";
					}
					leftcoma = ",";
					hasLeftSide = true;
				}
				else if(ReconciliationModelSide.RIGHT.name().equalsIgnoreCase(enrichmentValue.getSide())) {
					if(enrichmentValue.getValue() != null) {
						rightsql += rigthcoma + col  + " = :enrichmentItemData" + i++;
					}
					else {
						rightsql += rigthcoma + col  + " = NULL";
					}					
					rigthcoma = ",";
					hasRightSide = true;
				}
			}
			String or = " WHERE ";
			for (Number oid : data.getLeftids()) {
				leftsql += or + " ID = " + oid;
				or = " OR ";
			}
			or = " WHERE ";
			for (Number oid : data.getRightids()) {
				rightsql += or + " ID = " + oid;
				or = " OR ";
			}
						
			
			
			Query leftquery = entityManager.createNativeQuery(leftsql);
			Query rightquery = entityManager.createNativeQuery(rightsql);
			i = 1;	
			for(EnrichmentValue enrichmentValue : data.getEnrichmentItemDatas()) {
				if(enrichmentValue.getValue() != null) {					
					if(ReconciliationModelSide.LEFT.name().equalsIgnoreCase(enrichmentValue.getSide())) {
						leftquery.setParameter("enrichmentItemData" + i++, enrichmentValue.getValue());
					}
					else if(ReconciliationModelSide.RIGHT.name().equalsIgnoreCase(enrichmentValue.getSide())) {
						rightquery.setParameter("enrichmentItemData" + i++, enrichmentValue.getValue());
					}
				}
			}
			if(hasLeftSide) {
				leftquery.executeUpdate();
			}
			if(hasRightSide) {
				rightquery.executeUpdate();
			}
		}
	}
	
	
	private void resetenrichment(ReconciliationData data, Attribute recoAttribute, Attribute partialRecoAttribute,List<Object[]> numbers,List<Object[]> partialnumbers) throws Exception {
		int count = data.getEnrichmentItemDatas() != null ? data.getEnrichmentItemDatas().size() : 0;
		if(count > 0) {
			String leftsql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET ";
			String rightsql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET ";
			String leftcoma = "";
			String rigthcoma = "";
			boolean hasLeftSide = false;
			boolean hasRightSide = false;
			int position = 0;
			for(EnrichmentValue enrichmentValue : data.getEnrichmentItemDatas()) {
				if(!enrichmentValue.isValid()) {
					if(!ReconciliationModelSide.LOG_GRID.name().equalsIgnoreCase(enrichmentValue.getSide())) {
						throw new BcephalException("Invalid enrichement at : " + position);
					}
				}
				position++;
				
				String col = enrichmentValue.getUniverseTableColumnName();
				if(ReconciliationModelSide.LEFT.name().equalsIgnoreCase(enrichmentValue.getSide())) {
					leftsql += leftcoma + col  + " = NULL";
					leftcoma = ",";
					hasLeftSide = true;
				}
				else if(ReconciliationModelSide.RIGHT.name().equalsIgnoreCase(enrichmentValue.getSide())) {
					rightsql += rigthcoma + col  + " = NULL";				
					rigthcoma = ",";
					hasRightSide = true;
				}					
			}
			
			String coma = "";
			String inPart = " WHERE (" + recoAttribute.getUniverseTableColumnName() + " IN (";
			for (Object object : numbers) {					
				inPart += coma + "'" +  object + "'";
				coma = ", ";				
			}				
			leftsql += inPart + ")";
			rightsql += inPart + ")";
			
			inPart = "";
			if(data.isAllowPartialReco() && !partialnumbers.isEmpty()) {
				inPart += " OR " + partialRecoAttribute.getUniverseTableColumnName() + " IN (";
				coma = "";
				for (Object object : numbers) {		
					inPart += coma + "'" + object + "'";
					coma = ", ";
				}
				inPart += ")";
				leftsql += inPart;
				rightsql += inPart;
			}
			leftsql +=  ")";
			rightsql +=  ")";
			
			
//			String or = " WHERE ";
//			for (Number oid : data.getLeftids()) {
//				leftsql += or + " ID = " + oid;
//				or = " OR ";
//			}
//			or = " WHERE ";
//			for (Number oid : data.getRightids()) {
//				rightsql += or + " ID = " + oid;
//				or = " OR ";
//			}
			Query leftquery = entityManager.createNativeQuery(leftsql);
			Query rightquery = entityManager.createNativeQuery(rightsql);
			if(hasLeftSide) {
				leftquery.executeUpdate();
			}
			if(hasRightSide) {
				rightquery.executeUpdate();
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
	

	@SuppressWarnings("unchecked")
	@Transactional
	public boolean resetReconciliation(ReconciliationData data, String username) {
		List<Long> ids = new ArrayList<Long>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
			data.setRightids(new ArrayList<Long>(0));
		}
		else if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
			data.setLeftids(new ArrayList<Long>(0));
		}
		
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		if (data.getReconciliationId() == null || ids.isEmpty() || data.getRecoTypeId() == null) {
			return false;
		}
		Attribute recoAttribute = new Attribute(data.getRecoTypeId());
		Attribute partialRecoAttribute = new Attribute(data.getPartialRecoAttributeId());
		Attribute neutralizationAttribute = data.getNeutralizationAttributeId() != null ? new Attribute(data.getNeutralizationAttributeId()) : null;
		
		Measure reconciliatedMeasure = new Measure(data.getReconciliatedMeasureId());
		Measure remainningMeasure = new Measure(data.getRemainningMeasureId());

		loadSettings(data);				
		
		List<Object[]> numbers = new ArrayList<>(0);
		List<Object[]> partialnumbers = new ArrayList<>(0);
		try {			
			String sql = "SELECT DISTINCT " + recoAttribute.getUniverseTableColumnName()
				+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
				+ " WHERE " + recoAttribute.getUniverseTableColumnName() + " IS NOT NULL AND (";			
			String coma = "";
			String idCol = "id";
			for (Number oid : ids) {
				sql += coma + idCol + "=" + oid;
				coma = " OR ";
			}
			sql += ")";
			
			log.debug("Search reco nbrs...");
			log.trace("Search reco nbrs query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			numbers = query.getResultList();
			log.debug("Reco nbrs count : {}", numbers.size());
						
			if(data.isAllowPartialReco()) {				
				sql = "SELECT DISTINCT " + partialRecoAttribute.getUniverseTableColumnName()
					+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " WHERE " + partialRecoAttribute.getUniverseTableColumnName() + " IS NOT NULL AND (";			
				coma = "";
				for (Number oid : ids) {
					sql += coma + idCol + "=" + oid;
					coma = " OR ";
				}
				sql += ")";
				
				log.debug("Search partial reco nbrs...");
				log.trace("Search partial reco nbrs query : {}", sql);
				query = entityManager.createNativeQuery(sql);
				partialnumbers = query.getResultList();
				log.debug("Reco partial nbrs count : {}", partialnumbers.size());
			}
			
			if(data.isAllowNeutralization() && neutralizationAttribute != null) {
//				sql = "SELECT DISTINCT " + neutralizationAttribute.getUniverseTableColumnName()
//				+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//				+ " WHERE " + neutralizationAttribute.getUniverseTableColumnName() + " IS NOT NULL AND id IN (";			
//				coma = "";
//				for (Long oid : ids) {
//					sql += coma + oid;
//					coma = ", ";
//				}
//				sql += ")";
//				query = entityManager.createNativeQuery(sql);
//				neutralizationnumbers = query.getResultList();
			}
			
		} catch(Exception ex) {			
		
		}
		
		int count = numbers.size();
		int chunk = 50;
		int page = 0;
		if(count > 0){
			try {	
				log.debug("Try to update rows...");
				String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
						+ " SET " + recoAttribute.getUniverseTableColumnName() + " = NULL";
				
				if(data.isAllowPartialReco()) {
					sql += ", " + reconciliatedMeasure.getUniverseTableColumnName() + " = NULL";
					sql += ", " + remainningMeasure.getUniverseTableColumnName() + " = NULL";
					sql += ", " + partialRecoAttribute.getUniverseTableColumnName() + " = NULL";
				}
				
				if(data.isAllowNeutralization() && neutralizationAttribute != null) {
					sql += ", " + neutralizationAttribute.getUniverseTableColumnName() + " = NULL";
				}
				
				if(data.isAddUser() && data.getUserAttribute() != null) {
					sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = NULL";
				}
				if(data.isAddRecoDate() && data.getRecoDateId() != null) {
					sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = NULL";
				}
				if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
					sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = NULL";
				}
				String recoCol = recoAttribute.getUniverseTableColumnName();
				
				sql += " WHERE ((";
					
				String sql2 = "DELETE FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
						+ " WHERE " + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.WRITEOFF + "'"
						+ " AND (";
	
				int index = 0;
				int pndex = 0;
				while(count > page*chunk) {
					int c = chunk;
					index = page*chunk + chunk - 1;
					if(index >= count) {
						index = count - 1;
					}
					if((page*chunk) + c > count) {
						c = count - (page*chunk);
					}
					List<Object[]> ns = numbers.subList(page*chunk, (page*chunk) + c);				
					
					String coma = "";
					String inPart = "";
					String s1 = sql;
					String s2 = sql2;
					for (Object object : ns) {					
						inPart += coma + recoCol + "=" + "'" +  object + "'";
						coma = " OR ";
						
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
					s1 += inPart + ")";
					s2 += inPart + ")";
					List<Object[]> pns = new ArrayList<>();
					int pcount = partialnumbers.size();
					if(data.isAllowPartialReco() && !partialnumbers.isEmpty()) {						
						if(page*chunk < pcount) {
							pndex = page*chunk + chunk - 1;
							if(pndex >= pcount) {
								pndex = pcount - 1;
							}
							c = chunk;
							if((page*chunk) + c > pcount) {
								c = pcount - (page*chunk);
							}
							
							pns = partialnumbers.subList(page*chunk, (page*chunk) + c);
							String partialRecoCol = partialRecoAttribute.getUniverseTableColumnName();
							s1 += " OR (";
							coma = "";
							for (Object object : pns) {		
								s1 += coma + partialRecoCol + "='" + object + "'";
								coma = " OR ";
							}
							s1 += ")";
						}						
					}
					s1 += ")";
					
					log.debug("Reset query : {}", s1);
					
					resetenrichment(data,recoAttribute,partialRecoAttribute,ns,pns);
					
					Query query = entityManager.createNativeQuery(s2);
					query.executeUpdate();
		
					query = entityManager.createNativeQuery(s1);
					query.executeUpdate();
					page++;
					
					if(data.isAllowReconciliatedAmountLog() && data.getReconciliatedAmountLogGridId() != null) {
						resetLogReconciliatedAmount(data, ns, pns);
					}
				}
				
				
				
				return true;
			} 
			catch (BcephalException e) {
				log.debug(e.getMessage());
				throw e;
			} catch (Exception e) {
				log.error("Unable to reset reconciliate",  e);
				throw new BcephalException("Unable to reset reconciliate.");
			}
			finally {

			}
		}
		return true;
	}
	
	
//	
//	@SuppressWarnings("unchecked")
//	@Transactional
//	public boolean resetReconciliation(ReconciliationData data, String username) {
//		List<Long> ids = new ArrayList<Long>(0);
//		if(data.isSelectAllRowsInLeftGrid()) {
//			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
//			data.setRightids(new ArrayList<Long>(0));
//		}
//		else if(data.isSelectAllRowsInRightGrid()) {
//			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
//			data.setLeftids(new ArrayList<Long>(0));
//		}
//		
//		ids.addAll(data.getLeftids());
//		ids.addAll(data.getRightids());
//		if (data.getReconciliationId() == null || ids.isEmpty() || data.getRecoTypeId() == null) {
//			return false;
//		}
//		Attribute recoAttribute = new Attribute(data.getRecoTypeId());
//		Attribute partialRecoAttribute = new Attribute(data.getPartialRecoAttributeId());
//		Attribute neutralizationAttribute = data.getNeutralizationAttributeId() != null ? new Attribute(data.getNeutralizationAttributeId()) : null;
//		
//		Measure reconciliatedMeasure = new Measure(data.getReconciliatedMeasureId());
//		Measure remainningMeasure = new Measure(data.getRemainningMeasureId());
//
//		loadSettings(data);				
//		
//		List<Object[]> numbers = new ArrayList<>(0);
//		List<Object[]> partialnumbers = new ArrayList<>(0);
//		try {
//			
////			String sql = "SELECT DISTINCT " + recoAttribute.getUniverseTableColumnName()
////						+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
////						+ " WHERE " + recoAttribute.getUniverseTableColumnName() + " IS NOT NULL AND id IN (";			
////			String coma = "";
////			for (Number oid : ids) {
////				sql += coma + oid;
////				coma = ", ";
////			}
////			sql += ")";
//			
//			String sql = "SELECT DISTINCT " + recoAttribute.getUniverseTableColumnName()
//				+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//				+ " WHERE " + recoAttribute.getUniverseTableColumnName() + " IS NOT NULL AND (";			
//			String coma = "";
//			String idCol = "id";
//			for (Number oid : ids) {
//				sql += coma + idCol + "=" + oid;
//				coma = " OR ";
//			}
//			sql += ")";
//			
//			log.debug("Search reco nbrs...");
//			log.trace("Search reco nbrs query : {}", sql);
//			Query query = entityManager.createNativeQuery(sql);
//			numbers = query.getResultList();
//			log.debug("Reco nbrs count : {}", numbers.size());
//						
//			if(data.isAllowPartialReco()) {
////				sql = "SELECT DISTINCT " + partialRecoAttribute.getUniverseTableColumnName()
////					+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
////					+ " WHERE " + partialRecoAttribute.getUniverseTableColumnName() + " IS NOT NULL AND id IN (";			
////				coma = "";
////				for (Number oid : ids) {
////					sql += coma + oid;
////					coma = ", ";
////				}
////				sql += ")";
//				
//				sql = "SELECT DISTINCT " + partialRecoAttribute.getUniverseTableColumnName()
//					+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//					+ " WHERE " + partialRecoAttribute.getUniverseTableColumnName() + " IS NOT NULL AND (";			
//				coma = "";
//				for (Number oid : ids) {
//					sql += coma + idCol + "=" + oid;
//					coma = " OR ";
//				}
//				sql += ")";
//				
//				log.debug("Search partial reco nbrs...");
//				log.trace("Search partial reco nbrs query : {}", sql);
//				query = entityManager.createNativeQuery(sql);
//				partialnumbers = query.getResultList();
//				log.debug("Reco partial nbrs count : {}", partialnumbers.size());
//			}
//			
//			if(data.isAllowNeutralization() && neutralizationAttribute != null) {
////				sql = "SELECT DISTINCT " + neutralizationAttribute.getUniverseTableColumnName()
////				+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
////				+ " WHERE " + neutralizationAttribute.getUniverseTableColumnName() + " IS NOT NULL AND id IN (";			
////				coma = "";
////				for (Long oid : ids) {
////					sql += coma + oid;
////					coma = ", ";
////				}
////				sql += ")";
////				query = entityManager.createNativeQuery(sql);
////				neutralizationnumbers = query.getResultList();
//			}
//			
//		} catch(Exception ex) {			
//		
//		}
//		
//		int count = numbers.size();
//		int chunk = 50;
//		int page = 0;
//		if(count > 0){
//			try {	
//				log.debug("Try to update rows...");
//				String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//						+ " SET " + recoAttribute.getUniverseTableColumnName() + " = NULL";
//				
//				if(data.isAllowPartialReco()) {
//					sql += ", " + reconciliatedMeasure.getUniverseTableColumnName() + " = NULL";
//					sql += ", " + remainningMeasure.getUniverseTableColumnName() + " = NULL";
//					sql += ", " + partialRecoAttribute.getUniverseTableColumnName() + " = NULL";
//				}
//				
//				if(data.isAllowNeutralization() && neutralizationAttribute != null) {
//					sql += ", " + neutralizationAttribute.getUniverseTableColumnName() + " = NULL";
//				}
//				
//				if(data.isAddUser() && data.getUserAttribute() != null) {
//					sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = NULL";
//				}
//				if(data.isAddRecoDate() && data.getRecoDateId() != null) {
//					sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = NULL";
//				}
//				if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
//					sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = NULL";
//				}
//				sql += " WHERE (" + recoAttribute.getUniverseTableColumnName() + " IN (";
//					
//				String sql2 = "DELETE FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
//						+ " WHERE " + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.WRITEOFF + "'"
//						+ " AND " + recoAttribute.getUniverseTableColumnName() + " IN (";
//	
//				int index = 0;
//				int pndex = 0;
//				while(count > page*chunk) {
//					int c = chunk;
//					index = page*chunk + chunk - 1;
//					if(index >= count) {
//						index = count - 1;
//					}
//					if((page*chunk) + c > count) {
//						c = count - (page*chunk);
//					}
//					List<Object[]> ns = numbers.subList(page*chunk, (page*chunk) + c);				
//					
//					String coma = "";
//					String inPart = "";
//					String s1 = sql;
//					String s2 = sql2;
//					for (Object object : ns) {					
//						inPart += coma + "'" +  object + "'";
//						coma = ", ";
//						
//						ReconciliationLog log = new ReconciliationLog();
//						log.setCreationDate(new Timestamp(System.currentTimeMillis()));
//						log.setReconciliation(data.getReconciliationId());
//						log.setReconciliationNbr(object.toString());
//						log.setRecoType(RunModes.M.name());
//						log.setUsername(username);
//						log.setAction(ReconciliationActions.RESET);
//						log.setLeftAmount(BigDecimal.ZERO);
//						log.setRigthAmount(BigDecimal.ZERO);
//						log.setBalanceAmount(BigDecimal.ZERO);
//						log.setWriteoffAmount(BigDecimal.ZERO);	
//						reconciliationLogRepository.save(log);
//						
//					}				
//					s1 += inPart + ")";
//					s2 += inPart + ")";
//					List<Object[]> pns = new ArrayList<>();
//					int pcount = partialnumbers.size();
//					if(data.isAllowPartialReco() && !partialnumbers.isEmpty()) {						
//						if(page*chunk < pcount) {
//							pndex = page*chunk + chunk - 1;
//							if(pndex >= pcount) {
//								pndex = pcount - 1;
//							}
//							c = chunk;
//							if((page*chunk) + c > pcount) {
//								c = pcount - (page*chunk);
//							}
//							
//							pns = partialnumbers.subList(page*chunk, (page*chunk) + c);
//							s1 += " OR " + partialRecoAttribute.getUniverseTableColumnName() + " IN (";
//							coma = "";
//							for (Object object : pns) {		
//								s1 += coma + "'" + object + "'";
//								coma = ", ";
//							}
//							s1 += ")";
//						}						
//					}
//					s1 += ")";
//					
//					log.debug("Reset query : {}", s1);
//					
//					resetenrichment(data,recoAttribute,partialRecoAttribute,ns,pns);
//					
//					Query query = entityManager.createNativeQuery(s2);
//					query.executeUpdate();
//		
//					query = entityManager.createNativeQuery(s1);
//					query.executeUpdate();
//					page++;
//					
//					if(data.isAllowReconciliatedAmountLog() && data.getReconciliatedAmountLogGridId() != null) {
//						resetLogReconciliatedAmount(data, ns, pns);
//					}
//				}
//				
//				
//				
//				return true;
//			} 
//			catch (BcephalException e) {
//				log.debug(e.getMessage());
//				throw e;
//			} catch (Exception e) {
//				log.error("Unable to reset reconciliate",  e);
//				throw new BcephalException("Unable to reset reconciliate.");
//			}
//			finally {
//
//			}
//		}
//		return true;
//	}
//	
	
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
	public boolean unfreeze(ReconciliationData data, String username) {
		List<Long> ids = new ArrayList<Long>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
		}
		if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
		}
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		if (data.getReconciliationId() == null || ids.isEmpty() || data.getFreezeAttributeId() == null) {
			return false;
		}
		Attribute freezeAttribute = new Attribute(data.getFreezeAttributeId());

		loadSettings(data);				
		
		List<Object[]> numbers = new ArrayList<>(0);
		try {
			String sql = "SELECT DISTINCT " + freezeAttribute.getUniverseTableColumnName()
						+ " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
						+ " WHERE " + freezeAttribute.getUniverseTableColumnName() + " IS NOT NULL AND id IN (";			
			String coma = "";
			for (Number oid : ids) {
				sql += coma + oid;
				coma = ", ";
			}
			sql += ")";
			Query query = entityManager.createNativeQuery(sql);
			numbers = query.getResultList();					
		} catch(Exception ex) {			
		
		}
		
		if(!numbers.isEmpty()){
			try {	
				String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
						+ " SET " + freezeAttribute.getUniverseTableColumnName() + " = NULL";
				if(data.isAddUser() && data.getUserAttribute() != null) {
					sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = NULL";
				}
				if(data.isAddRecoDate() && data.getRecoDateId() != null) {
					sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = NULL";
				}
				if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
					sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = NULL";
				}
				sql += " WHERE " + freezeAttribute.getUniverseTableColumnName() + " IN (";
					
				String sql2 = "DELETE FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
						+ " WHERE " + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.WRITEOFF + "'"
						+ " AND " + freezeAttribute.getUniverseTableColumnName() + " IN (";
	
				String coma = "";
				String inPart = "";
				for (Object object : numbers) {					
					inPart += coma + "'" +  object + "'";
					coma = ", ";
					
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
				sql += inPart + ")";
				sql2 += inPart + ")";
				Query query = entityManager.createNativeQuery(sql2);
				query.executeUpdate();
	
				query = entityManager.createNativeQuery(sql);
				query.executeUpdate();
				
				return true;
			} 
			catch (BcephalException e) {
				log.debug(e.getMessage());
				throw e;
			} catch (Exception e) {
				log.error("Unable to reset freeze",  e);
				throw new BcephalException("Unable to reset freeze.");
			}
			finally {

			}
		}
		return true;
	}
	
	@Transactional
	public boolean editNote(ReconciliationData data, String username, RunModes mode) throws Exception {
		loadSettings(data);
		try {
			log.debug("Reconciliation model ID : {}", data.getReconciliationId());
			List<Long> ids = new ArrayList<Long>(0);
			if(data.isSelectAllRowsInLeftGrid()) {
				data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
			}
			if(data.isSelectAllRowsInRightGrid()) {
				data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
			}
			ids.addAll(data.getLeftids());
			ids.addAll(data.getRightids());
			int count = ids.size();
			log.debug("#Rows to edit : {}", count);
			if (count <= 0) {
				log.debug("No rows to edit!");
				return false;
			}
			
			if(data.getNoteAttributeId() == null) {
				log.debug("Unable to edit note. The note type is NULL!");
				throw new BcephalException("Undefined note type.");
			}
					
			Optional<Attribute> response = attributeRepository.findById(data.getNoteAttributeId());
			if(response.isEmpty()) {
				log.debug("Unable to edit note. The note attribute is unknown!. Not found attribute with ID : {}", data.getNoteAttributeId());
				throw new BcephalException("Unknown note type : " + data.getNoteAttributeId());
			}
			Attribute noteAttribute = response.get();		
			String note = data.getNote();			
			if(!StringUtils.hasText(note)) {
				note = "";
			}				
			log.debug("Note : {}", note);

			String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
					+ noteAttribute.getUniverseTableColumnName() + " = :note";
			
//			if(data.isAddUser() && data.getUserAttribute() != null) {
//				sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = :user";
//			}
//			if(data.isAddRecoDate() && data.getRecoDateId() != null) {
//				//sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = :date";
//			}
//			if(data.isAddAutomaticManual()  && data.getAutoManualAttribute() != null) {
//				sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = :autoManual";
//			}
//			if(data.isAddNote() && data.getNoteAttributeId() != null && StringUtils.hasText(data.getNote())) {
//				sql += ", " + new Attribute(data.getNoteAttributeId()).getUniverseTableColumnName() + " = :note";
//			}
			
			String or = " WHERE ";
			for (Number oid : ids) {
				sql += or + " ID = " + oid;
				or = " OR ";
			}				
			
			log.trace("Freeze query : {}", sql);
			
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("note", note);
			
//			if(data.isAddUser() && data.getUserAttribute() != null) {
//				query.setParameter("user", username);
//			}
//			if(data.isAddRecoDate() && data.getRecoDateId() != null) {
//				//query.setParameter("date", Calendar.getInstance().getTime());
//			}
//			if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
//				if(mode == RunModes.A) {
//					if(data.getAutomaticValue() != null) {
//						query.setParameter("autoManual", data.getAutomaticValue().getName());
//					}
//					else {
//						query.setParameter("autoManual", RunModes.A.name());
//					}
//				}
//				else {
//					if(data.getManualValue() != null) {
//						query.setParameter("autoManual", data.getManualValue().getName());
//					}
//					else {
//						query.setParameter("autoManual", RunModes.M.name());
//					}
//				}
//			}
//			if(data.isAddNote() && data.getNoteAttributeId() != null && StringUtils.hasText(data.getNote())) {
//				query.setParameter("note", data.getNote());
//			}
			
			query.executeUpdate();
			
									
			log.debug("Try to save reco log");
			ReconciliationLog recoLog = new ReconciliationLog();
			recoLog.setCreationDate(new Timestamp(System.currentTimeMillis()));
			recoLog.setReconciliation(data.getReconciliationId());
//			recoLog.setReconciliationNbr(recoNumber);
			recoLog.setRecoType(mode.name());
			recoLog.setUsername(username);
			recoLog.setAction(ReconciliationActions.NOTE);
			recoLog.setLeftAmount(data.getLeftAmount());
			recoLog.setRigthAmount(data.getRigthAmount());
			recoLog.setBalanceAmount(data.getBalanceAmount());
			recoLog.setWriteoffAmount(BigDecimal.ZERO);	
			reconciliationLogRepository.save(recoLog);
			log.debug("Reco log saved!");
			
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
	
			
	@Transactional
	public boolean freeze(ReconciliationData data, String username, RunModes mode) throws Exception {
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
	
	public boolean freezeWithoutCommint(ReconciliationData data, String username, RunModes mode) throws Exception {
		log.debug("Reconciliation model ID : {}", data.getReconciliationId());
		List<Long> ids = new ArrayList<Long>(0);
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
			return false;
		}
		
		if(data.getFreezeAttributeId() == null) {
			log.debug("Unable to freeze. The freeze type is NULL!");
			throw new BcephalException("Undefined freeze type.");
		}
				
		Optional<Attribute> response = attributeRepository.findById(data.getFreezeAttributeId());
		if(response.isEmpty()) {
			log.debug("Unable to freeze. The reco freeze is unknown!. Not found attribute with : {}", data.getFreezeAttributeId());
			throw new BcephalException("Unknown freeze type : " + data.getRecoTypeId());
		}
		Attribute recoAttribute = response.get();		
		String recoNumber = null;
		IncrementalNumber recoSequence = null;		
		if(data.getFreezeSequenceId() != null) {
			Optional<IncrementalNumber> resp = incrementalNumberRepository.findById(data.getFreezeSequenceId());
			if(!resp.isEmpty()) {
				recoSequence = resp.get();
				log.debug("Sequence found : {}", recoSequence.getName());
			}
			else {
				log.debug("Sequence not found! : {}", data.getFreezeSequenceId());
			}
			if(recoSequence != null) {
				log.debug("Try to build sequence next number : {}", recoSequence.getName());
				recoNumber =  recoSequence.buildNextValue();
			}
		}		
		if(!StringUtils.hasText(recoNumber)) {
			if( recoAttribute.getIncremantalValue() == null) {
				recoAttribute.setIncremantalValue((long) 0); 
			}
			Long number =  recoAttribute.getIncremantalValue() + 1;
			recoAttribute.setIncremantalValue(number);
			recoNumber = "" + number;
		}				
		log.debug("Freeze number : {}", recoNumber);

		String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
				+ recoAttribute.getUniverseTableColumnName() + " = :recoNbr";
		
		if(data.isAddUser() && data.getUserAttribute() != null) {
			sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = :user";
		}
		if(data.isAddRecoDate() && data.getRecoDateId() != null) {
			//sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = :date";
		}
		if(data.isAddAutomaticManual()  && data.getAutoManualAttribute() != null) {
			sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = :autoManual";
		}
		if(data.isAddNote() && data.getNoteAttributeId() != null && StringUtils.hasText(data.getNote())) {
			sql += ", " + new Attribute(data.getNoteAttributeId()).getUniverseTableColumnName() + " = :note";
		}
		
		String or = " WHERE ";
		for (Number oid : ids) {
			sql += or + " ID = " + oid;
			or = " OR ";
		}				
		
		log.trace("Freeze query : {}", sql);
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("recoNbr", recoNumber);
		
		if(data.isAddUser() && data.getUserAttribute() != null) {
			query.setParameter("user", username);
		}
		if(data.isAddRecoDate() && data.getRecoDateId() != null) {
			//query.setParameter("date", Calendar.getInstance().getTime());
		}
		if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
			if(mode == RunModes.A) {
				if(data.getAutomaticValue() != null) {
					query.setParameter("autoManual", data.getAutomaticValue().getName());
				}
				else {
					query.setParameter("autoManual", RunModes.A.name());
				}
			}
			else {
				if(data.getManualValue() != null) {
					query.setParameter("autoManual", data.getManualValue().getName());
				}
				else {
					query.setParameter("autoManual", RunModes.M.name());
				}
			}
		}
		if(data.isAddNote() && data.getNoteAttributeId() != null && StringUtils.hasText(data.getNote())) {
			query.setParameter("note", data.getNote());
		}
		
		query.executeUpdate();
		
		log.debug("Try to save reco type attribute : {}", recoAttribute.getName());
		attributeRepository.save(recoAttribute);
		if(recoSequence != null) {
			incrementalNumberRepository.save(recoSequence);
		}
		log.debug("Reco type attribute saved : {}", recoAttribute.getName());
					
		log.debug("Try to save reco log");
		ReconciliationLog recoLog = new ReconciliationLog();
		recoLog.setCreationDate(new Timestamp(System.currentTimeMillis()));
		recoLog.setReconciliation(data.getReconciliationId());
		recoLog.setReconciliationNbr(recoNumber);
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
	
	
	@Transactional
	public boolean neutralize(ReconciliationData data, String username, RunModes mode) throws Exception {
		log.debug("Neutralization - Reconciliation model ID : {}", data.getReconciliationId());
		if(data.getRecoTypeId() == null) {
			log.debug("Unable to neutralize. The reco type is NULL!");
			throw new BcephalException("Undefined reco type.");
		}		
		if(!data.isAllowNeutralization()) {
			log.debug("Unable to neutralize. The neutralization operation is not aalowed!");
			throw new BcephalException("Neutralization not allowed.");
		}        
		if(data.getNeutralizationAttributeId() == null) {
			log.debug("Unable to neutralize. The neutralization attribute is NULL!");
			throw new BcephalException("Undefined neutralization attribute.");
		}
		
		data.setPerformNeutralization(true);
		data.setPerformPartialReco(data.isAllowPartialReco() && data.getReconciliatedMeasureId() != null && data.getRemainningMeasureId() != null);
		return reconciliate(data, username, mode);		
	}
	
	public boolean neutralizeWithoutCommint(ReconciliationData data, String username, RunModes mode) throws Exception {
		log.debug("Neutralization - Reconciliation model ID : {}", data.getReconciliationId());
		if(data.getRecoTypeId() == null) {
			log.debug("Unable to neutralize. The reco type is NULL!");
			throw new BcephalException("Undefined reco type.");
		}		
		if(!data.isAllowNeutralization()) {
			log.debug("Unable to neutralize. The neutralization operation is not aalowed!");
			throw new BcephalException("Neutralization not allowed.");
		}        
		if(data.getNeutralizationAttributeId() == null) {
			log.debug("Unable to neutralize. The neutralization attribute is NULL!");
			throw new BcephalException("Undefined neutralization attribute.");
		}
		
		data.setPerformNeutralization(true);
		data.setPerformPartialReco(data.isAllowPartialReco() && data.getReconciliatedMeasureId() != null && data.getRemainningMeasureId() != null);
		loadSettings(data);
		return reconciliateWithoutCommint(data, username, mode);
	}
        
	@Transactional
  	public boolean unneutralize(ReconciliationData data, String username) {
		List<Long> ids = new ArrayList<Long>(0);
		if(data.isSelectAllRowsInLeftGrid()) {
			data.setLeftids(getSelectAllRowsIds(data.getLeftBrowserDataFilter()));
		}
		if(data.isSelectAllRowsInRightGrid()) {
			data.setRightids(getSelectAllRowsIds(data.getRightBrowserDataFilter()));
		}
		ids.addAll(data.getLeftids());
		ids.addAll(data.getRightids());
		if (data.getReconciliationId() == null || ids.isEmpty() || data.getRecoTypeId() == null) {
			return false;
		}
		Attribute recoAttribute = new Attribute(data.getRecoTypeId());
		Attribute partialRecoAttribute = new Attribute(data.getPartialRecoAttributeId());
		Attribute neutralizeAttribute = new Attribute(data.getNeutralizationAttributeId());
		
		Measure reconciliatedMeasure = new Measure(data.getReconciliatedMeasureId());
		Measure remainningMeasure = new Measure(data.getRemainningMeasureId());

		loadSettings(data);				
		
		try {	
			String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " SET " 
					+ recoAttribute.getUniverseTableColumnName() + " = NULL, "
					+ neutralizeAttribute.getUniverseTableColumnName() + " = NULL";
			
			if(data.isAllowPartialReco()) {
				sql += ", " + reconciliatedMeasure.getUniverseTableColumnName() + " = NULL";
				sql += ", " + remainningMeasure.getUniverseTableColumnName() + " = NULL";
				sql += ", " + partialRecoAttribute.getUniverseTableColumnName() + " = NULL";
			}
			
			if(data.isAddUser() && data.getUserAttribute() != null) {
				sql += ", " + data.getUserAttribute().getUniverseTableColumnName() + " = NULL";
			}
			if(data.isAddRecoDate() && data.getRecoDateId() != null) {
				sql += ", " + new Period(data.getRecoDateId()).getUniverseTableColumnName() + " = NULL";
			}
			if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
				sql += ", " + data.getAutoManualAttribute().getUniverseTableColumnName() + " = NULL";
			}
			sql += " WHERE id IN (";
			
			String coma = "";
			for (Number oid : ids) {
				sql += coma + oid;
				coma = ", ";
			}
			sql += ")";						
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();				
			return true;
		} 
		catch (BcephalException e) {
			log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Unable to reset neutralization",  e);
			throw new BcephalException("Unable to reset neutralization.");
		}
		finally {

		}  
  	}

	
	@Transactional
	public BrowserDataPage<Object[]> refreshReconciliatedAmounts(GrilleDataFilter filter, java.util.Locale locale) {
		if(filter.getRecoData() != null && filter.getRecoData().isAllowPartialReco() && filter.getRecoData().getRecoAttributeId() != null && filter.getRecoData().getPartialRecoAttributeId() != null 
				&& filter.getRecoData().getRemainningMeasureId() != null && filter.getRecoData().getReconciliatedMeasureId() != null && filter.getRecoData().getAmountMeasureId() != null) {
			RefreshRecoAmountQueryBuilder builder = new RefreshRecoAmountQueryBuilder(filter);
			builder.setParameterRepository(parameterRepository);
			String sql = builder.buildQuery();
			Query query = this.entityManager.createNativeQuery(sql);
			query.executeUpdate();	
		}
		return grilleService.searchRows(filter, locale);
	}
	
	@Transactional
	public BrowserDataPage<GridItem> refreshReconciliatedAmounts2(GrilleDataFilter filter, java.util.Locale locale) {
		GrilleRowType type = filter.getRowType();
		if(filter.getRecoData() != null && filter.getRecoData().isAllowPartialReco() && filter.getRecoData().getRecoAttributeId() != null && filter.getRecoData().getPartialRecoAttributeId() != null 
				&& filter.getRecoData().getRemainningMeasureId() != null && filter.getRecoData().getReconciliatedMeasureId() != null && filter.getRecoData().getAmountMeasureId() != null) {
			filter.setRowType(GrilleRowType.NOT_RECONCILIATED);
			RefreshRecoAmountQueryBuilder builder = new RefreshRecoAmountQueryBuilder(filter);
			builder.setParameterRepository(parameterRepository);
			String sql = builder.buildQuery();
			log.debug("Refresh reco amount query : {}", sql);
			Query query = this.entityManager.createNativeQuery(sql);
			query.executeUpdate();	
		}
		filter.setRowType(type);
		return grilleService.searchRows2(filter, locale);
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
	
	
	
	
	
	
	
	
	
	
	
	public void loadSettings(ReconciliationData data) {
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
	public List<Long> getSelectAllRowsIds(GrilleDataFilter filter) {		
		List<Long> ids = new ArrayList<>();		
		grilleService.loadFilterClosures(filter);	
		SelectAllRowsQueryBuilder builder = new SelectAllRowsQueryBuilder(filter);
		((SelectAllRowsQueryBuilder)builder).setParameterRepository(parameterRepository);
		
		String sql = builder.buildIdsQuery();
		log.trace("Select IDs query : {}", sql);
		Query query = this.entityManager.createNativeQuery(sql);
		ids = (List<Long>)query.getResultList();
		return ids;
	}

	
	public BrowserDataPage<GridItem> selectAllRows(GrilleDataFilter filter, java.util.Locale locale) {		
		BrowserDataPage<GridItem> page = new BrowserDataPage<GridItem>();
		GridItem item = new GridItem(new Object[] {BigDecimal.ZERO, BigDecimal.ZERO, -100});
		page.getItems().add(item);	
		page.setPageSize(filter.getPageSize());
		if(filter.getGrid() != null) {
			grilleService.loadFilterClosures(filter);	
			SelectAllRowsQueryBuilder builder = new SelectAllRowsQueryBuilder(filter);
			((SelectAllRowsQueryBuilder)builder).setParameterRepository(parameterRepository);
			//((SelectAllRowsQueryBuilder)builder).setLinkRepository(grilleService.linkRepository);
						
			if(filter.getDebitCreditAttribute() == null) {
				Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
				if(parameter != null && parameter.getLongValue() != null) {
					filter.setDebitCreditAttribute(new Attribute(parameter.getLongValue()));
				}
				parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
				if(parameter != null && parameter.getStringValue() != null) {
					filter.setDebitValue(parameter.getStringValue());
				}
				else {
					filter.setDebitValue("D");
				}
				parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
				if(parameter != null && parameter.getStringValue() != null) {
					filter.setCreditValue(parameter.getStringValue());
				}
				else {
					filter.setCreditValue("C");
				}
			}
						
			String sql = builder.buildCountQuery();
			log.trace("Count query : {}", sql);
			Query query = this.entityManager.createNativeQuery(sql);
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

	private BigDecimal[] buildAmounts(GrilleDataFilter filter, List<Object[]> rows) {
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
	
	
}
