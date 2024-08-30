package com.moriset.bcephal.billing.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.billing.domain.BillingDescription;
import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.billing.domain.BillingModelDriverGroup;
import com.moriset.bcephal.billing.domain.BillingModelEditorData;
import com.moriset.bcephal.billing.domain.BillingModelEnrichmentItem;
import com.moriset.bcephal.billing.domain.BillingModelGroupingItem;
import com.moriset.bcephal.billing.domain.BillingModelItem;
import com.moriset.bcephal.billing.domain.BillingModelParameter;
import com.moriset.bcephal.billing.domain.BillingModelPivot;
import com.moriset.bcephal.billing.domain.BillingModelTemplate;
import com.moriset.bcephal.billing.domain.CalculateBillingItem;
import com.moriset.bcephal.billing.domain.InvoiceVariables;
import com.moriset.bcephal.billing.repository.BillingDescriptionRepository;
import com.moriset.bcephal.billing.repository.BillingModelEnrichmentItemRepository;
import com.moriset.bcephal.billing.repository.BillingModelGroupingItemRepository;
import com.moriset.bcephal.billing.repository.BillingModelItemRepository;
import com.moriset.bcephal.billing.repository.BillingModelParameterRepository;
import com.moriset.bcephal.billing.repository.BillingModelPivotRepository;
import com.moriset.bcephal.billing.repository.BillingModelRepository;
import com.moriset.bcephal.billing.repository.BillingModelTemplateRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillingModelService extends MainObjectService<BillingModel, BrowserData> {
	
	@Autowired
	BillingModelRepository billingModelRepository;
	
	@Autowired
	BillingModelDriverGroupService billingModelDriverGroupService;
	
	@Autowired
	BillingModelEnrichmentItemRepository billingModelEnrichmentItemRepository;
	
	@Autowired
	CalculateBillingItemService calculateBillingItemService;
	
	@Autowired
	BillingModelGroupingItemRepository billingModelGroupingItemRepository;
	
	@Autowired
	BillingModelItemRepository billingModelItemRepository;
	
	@Autowired
	BillingModelParameterRepository billingModelParameterRepository;
	
	@Autowired
	BillingModelPivotRepository billingModelPivotRepository;
	
	@Autowired
	BillingDescriptionRepository billingDescriptionRepository;
	
	@Autowired
	BillingJoinService billingJoinService;
	
	@Autowired
	UniverseFilterService universeFilterService;
	
	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;
	
	@Autowired
	BillingModelTemplateRepository billingModelTemplateRepository;
	
	@Value("${bcephal.languages}")
	List<String> locales;
	
	@Autowired
	BillTemplateService billTemplateService;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	BillingCompanyRepositoryService billingCompanyRepositoryService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.BILLING_MODEL;
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
	public MainObjectRepository<BillingModel> getRepository() {
		return (MainObjectRepository<BillingModel>) billingModelRepository;
	}
	
	@Override
	public BillingModelEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<BillingModel> base = super.getEditorData(filter, session, locale);
		BillingModelEditorData data = new BillingModelEditorData(base);
		data.sequences = incrementalNumberRepository.getAllIncrementalNumbers();
		data.variables = new InvoiceVariables().getAll();
		data.locales = locales;
		data.companies = billingCompanyRepositoryService.getCompanies();
		initEditorDataForJoin(data, session, locale);
		
		return data;
	}
		
	@Override
	protected void initEditorData(EditorData<BillingModel> data, HttpSession session, Locale locale) throws Exception {
		
	}
	
	protected void initEditorDataForJoin(BillingModelEditorData data, HttpSession session, Locale locale) throws Exception {		
		List<Model> models = new ArrayList<>(0);
		List<Measure> measures = new ArrayList<>(0);
		List<Period> periods = new ArrayList<>(0);
		Join grid = billingJoinService.getBillingJoin();
		if(grid != null) {
			Model model = new Model();
			models.add(model);
			model.setName(grid.getName());
			Entity entity = new Entity();
			entity.setName("Columns");
			model.getEntities().add(entity);

			for (JoinColumn column : grid.getColumns()) {
				if (column.getType() == DimensionType.MEASURE) {
					measures.add(new Measure(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));						
				} else if (column.getType() == DimensionType.ATTRIBUTE) {						
					entity.getAttributes().add(new Attribute(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));
				} else if (column.getType() == DimensionType.PERIOD) {
					periods.add(new Period(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));
				}
			}

			Comparator<Dimension> comparator = new Comparator<Dimension>() {
				@Override
				public int compare(Dimension data1, Dimension data2) {
					return data1.getName().compareTo(data2.getName());
				}
			};

			if (models.get(0).getEntities().size() > 0) {
				Collections.sort(models.get(0).getEntities().get(0).getAttributes(), comparator);
			}
			Collections.sort(measures, comparator);
			Collections.sort(periods, comparator);
		}
		data.setModels(models);
		data.setMeasures(measures);
		data.setPeriods(periods);
	}
	

	@Override
	protected BillingModel getNewItem() {
		BillingModel billingModel = new BillingModel();
		billingModel.setCurrency("€");
		String baseName = "BillingModel ";
		int i = 1;
		billingModel.setName(baseName + i);
		while (getByName(billingModel.getName()) != null) {
			i++;
			billingModel.setName(baseName + i);
		}
		return billingModel;
	}

	@Override
	@Transactional
	public BillingModel save(BillingModel billingModel, Locale locale) {
		log.debug("Try to  Save entity : {}", billingModel);
		if (getRepository() == null) {
			return billingModel;
		}
		try {
			if (billingModel == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.object",
						new Object[] { billingModel }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			ListChangeHandler<BillingModelDriverGroup> driverGroupList = billingModel.getDriverGroupListChangeHandler();
			ListChangeHandler<BillingModelEnrichmentItem> enrichmentItems = billingModel.getEnrichmentItemListChangeHandler();
			ListChangeHandler<BillingModelGroupingItem> groupingItem = billingModel.getGroupingItemListChangeHandler();
			ListChangeHandler<BillingModelItem> items = billingModel.getItemListChangeHandler();
			ListChangeHandler<BillingModelParameter> parameterList = billingModel.getParameterListChangeHandler();
			ListChangeHandler<BillingModelPivot> pivotList = billingModel.getPivotListChangeHandler();
			ListChangeHandler<BillingDescription> billingDescriptions = billingModel.getBillingDescriptionsListChangeHandler();
			ListChangeHandler<BillingModelTemplate> billingModelTemplates = billingModel.getBillingModelTemplatesListChangeHandler();
			ListChangeHandler<CalculateBillingItem> calculateBillingItems = billingModel.getCalculateBillingItemListChangeHandler();
			billingModel.setModificationDate(new Timestamp(System.currentTimeMillis()));
			
			if(billingModel.getFilter() != null) {
				billingModel.setFilter(universeFilterService.save(billingModel.getFilter()));
			}
			
			billingModel = getRepository().save(billingModel);
			BillingModel id = billingModel;
			saveBillingModelDriverGroup(driverGroupList, id, locale);
			saveBillingModelEnrichmentItem(enrichmentItems, id);
			saveBillingModelGroupingItem(groupingItem, id);
			saveBillingModelItem(items, id);
			saveBillingModelParameter(parameterList, id);
			saveBillingModelPivot(pivotList, id);
			saveBillingDescription(billingDescriptions, id);
			saveBillingModelTemplate(billingModelTemplates, id);
			saveCalculateBillingItem(calculateBillingItems, id, locale);
			log.debug("Grid saved : {} ", billingModel.getId());

			log.debug("entity successfully saved : {} ", billingModel);
			return billingModel;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", billingModel, e);
			String message = getMessageSource().getMessage("unable.to.save.model", new Object[] { billingModel },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	private void saveBillingDescription(ListChangeHandler<BillingDescription> billingDescriptions, BillingModel id) {
		billingDescriptions.getNewItems().forEach(item -> {
			log.trace("Try to save billing description : {}", item);
			item.setBilling(id);
			billingDescriptionRepository.save(item);
			log.trace("Billing description saved : {}", item.getId());
		});
		billingDescriptions.getUpdatedItems().forEach(item -> {
			log.trace("Try to save billing description : {}", item);
			item.setBilling(id);
			billingDescriptionRepository.save(item);
			log.trace("Billing description saved : {}", item.getId());
		});
		billingDescriptions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete billing description : {}", item);
				billingDescriptionRepository.deleteById(item.getId());
				log.trace("Billing description deleted : {}", item.getId());
			}
		});
		
	}

	private void saveBillingModelDriverGroup(ListChangeHandler<BillingModelDriverGroup> driverGroupList, BillingModel id, Locale locale) {
		driverGroupList.getNewItems().forEach(item -> {
			log.trace("Try to save BillingModelDriverGroup : {}", item);
			item.setBilling(id);
			billingModelDriverGroupService.save(item, locale);
			log.trace("BillingModelDriverGroup saved : {}", item.getId());
		});
		driverGroupList.getUpdatedItems().forEach(item -> {
			log.trace("Try to save BillingModelDriverGroup : {}", item);
			item.setBilling(id);
			billingModelDriverGroupService.save(item, locale);
			log.trace("BillingModelDriverGroup saved : {}", item.getId());
		});
		driverGroupList.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete BillingModelDriverGroup : {}", item);
				billingModelDriverGroupService.deleteById(item.getId());
				log.trace("BillingModelDriverGroup deleted : {}", item.getId());
			}
		});
	}
	
	private void saveBillingModelEnrichmentItem(ListChangeHandler<BillingModelEnrichmentItem> enrichmentItems, BillingModel id) {
		enrichmentItems.getNewItems().forEach(item -> {
			log.trace("Try to save BillingModelEnrichmentItem : {}", item);
			item.setBilling(id);
			billingModelEnrichmentItemRepository.save(item);
			log.trace("BillingModelEnrichmentItem saved : {}", item.getId());
		});
		enrichmentItems.getUpdatedItems().forEach(item -> {
			log.trace("Try to save BillingModelEnrichmentItem : {}", item);
			item.setBilling(id);
			billingModelEnrichmentItemRepository.save(item);
			log.trace("BillingModelEnrichmentItem saved : {}", item.getId());
		});
		enrichmentItems.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete BillingModelEnrichmentItem : {}", item);
				billingModelEnrichmentItemRepository.deleteById(item.getId());
				log.trace("BillingModelEnrichmentItem deleted : {}", item.getId());
			}
		});
	}
	
	private void saveBillingModelGroupingItem(ListChangeHandler<BillingModelGroupingItem> groupingItem, BillingModel id) {
		groupingItem.getNewItems().forEach(item -> {
			log.trace("Try to save BillingModelGroupingItem : {}", item);
			item.setBilling(id);
			billingModelGroupingItemRepository.save(item);
			log.trace("BillingModelGroupingItem saved : {}", item.getId());
		});
		groupingItem.getUpdatedItems().forEach(item -> {
			log.trace("Try to save BillingModelGroupingItem : {}", item);
			item.setBilling(id);
			billingModelGroupingItemRepository.save(item);
			log.trace("BillingModelGroupingItem saved : {}", item.getId());
		});
		groupingItem.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete BillingModelGroupingItem : {}", item);
				billingModelGroupingItemRepository.deleteById(item.getId());
				log.trace("BillingModelGroupingItem deleted : {}", item.getId());
			}
		});
	}
	
	private void saveBillingModelItem(ListChangeHandler<BillingModelItem> items, BillingModel id) {
		items.getNewItems().forEach(item -> {
			log.trace("Try to save BillingModelItem : {}", item);
			item.setBilling(id);
			billingModelItemRepository.save(item);
			log.trace("BillingModelItem saved : {}", item.getId());
		});
		items.getUpdatedItems().forEach(item -> {
			log.trace("Try to save BillingModelItem : {}", item);
			item.setBilling(id);
			billingModelItemRepository.save(item);
			log.trace("BillingModelItem saved : {}", item.getId());
		});
		items.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete BillingModelItem : {}", item);
				billingModelItemRepository.deleteById(item.getId());
				log.trace("BillingModelItem deleted : {}", item.getId());
			}
		});
	}
	
	private void saveBillingModelParameter(ListChangeHandler<BillingModelParameter> parameterList, BillingModel id) {
		parameterList.getNewItems().forEach(item -> {
			log.trace("Try to save BillingModelParameter : {}", item);
			item.setBilling(id);
			billingModelParameterRepository.save(item);
			log.trace("BillingModelParameter saved : {}", item.getId());
		});
		parameterList.getUpdatedItems().forEach(item -> {
			log.trace("Try to save BillingModelParameter : {}", item);
			item.setBilling(id);
			billingModelParameterRepository.save(item);
			log.trace("BillingModelParameter saved : {}", item.getId());
		});
		parameterList.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete BillingModelParameter : {}", item);
				billingModelParameterRepository.deleteById(item.getId());
				log.trace("BillingModelParameter deleted : {}", item.getId());
			}
		});
	}
	
	private void saveBillingModelPivot(ListChangeHandler<BillingModelPivot> pivotList, BillingModel id) {
		pivotList.getNewItems().forEach(item -> {
			log.trace("Try to save BillingModelPivot : {}", item);
			item.setBilling(id);
			billingModelPivotRepository.save(item);
			log.trace("BillingModelPivot saved : {}", item.getId());
		});
		pivotList.getUpdatedItems().forEach(item -> {
			log.trace("Try to save BillingModelPivot : {}", item);
			item.setBilling(id);
			billingModelPivotRepository.save(item);
			log.trace("BillingModelPivot saved : {}", item.getId());
		});
		pivotList.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete BillingModelPivot : {}", item);
				billingModelPivotRepository.deleteById(item.getId());
				log.trace("BillingModelPivot deleted : {}", item.getId());
			}
		});
	}
	
	private void saveBillingModelTemplate(ListChangeHandler<BillingModelTemplate> billingModelTemplateList, BillingModel id) {
		billingModelTemplateList.getNewItems().forEach(item -> {
			log.trace("Try to save BillingModelTemplate : {}", item);
			item.setModelId(id);
			if (item.getFilter() != null) {
				item.setFilter(universeFilterService.save(item.getFilter()));
			}
			billingModelTemplateRepository.save(item);
			log.trace("BillingModelTemplate saved : {}", item.getId());
		});
		billingModelTemplateList.getUpdatedItems().forEach(item -> {
			log.trace("Try to save BillingModelTemplate : {}", item);
			item.setModelId(id);
			if (item.getFilter() != null) {
				item.setFilter(universeFilterService.save(item.getFilter()));
			}
			billingModelTemplateRepository.save(item);
			log.trace("BillingModelTemplate saved : {}", item.getId());
		});
		billingModelTemplateList.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete BillingModelTemplate : {}", item);
				if (item.getFilter() != null) {
					universeFilterService.delete(item.getFilter());
				}
				billingModelTemplateRepository.deleteById(item.getId());
				log.trace("BillingModelTemplate deleted : {}", item.getId());
			}
		});
	}
	
	private void saveCalculateBillingItem(ListChangeHandler<CalculateBillingItem> items, BillingModel id, Locale locale) {		
		items.getNewItems().forEach(item -> {
			log.trace("Try to save CalculateBillingItem : {}", item);
			item.setModelId(id);
			calculateBillingItemService.save(item, locale);
			log.trace("CalculateBillingItem saved : {}", item.getId());
		});
		items.getUpdatedItems().forEach(item -> {
			log.trace("Try to update CalculateBillingItem : {}", item);
			item.setModelId(id);
			calculateBillingItemService.save(item, locale);
			log.trace("CalculateBillingItem updated : {}", item.getId());
		});
		items.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete CalculateBillingItem : {}", item);
				calculateBillingItemService.deleteById(item.getId());
				log.trace("CalculateBillingItem deleted : {}", item.getId());
			}
		});
	}
	
	
	@Override
	public void delete(BillingModel billingModel) {
		log.debug("Try to delete BillingModel : {}", billingModel);	
		if(billingModel == null || billingModel.getId() == null) {
			return;
		}
		
		ListChangeHandler<BillingModelDriverGroup> driverGroupList = billingModel.getDriverGroupListChangeHandler();
		ListChangeHandler<BillingModelEnrichmentItem> enrichmentItems = billingModel.getEnrichmentItemListChangeHandler();
		ListChangeHandler<BillingModelGroupingItem> groupingItem = billingModel.getGroupingItemListChangeHandler();
		ListChangeHandler<BillingModelItem> items = billingModel.getItemListChangeHandler();
		ListChangeHandler<BillingModelParameter> parameterList = billingModel.getParameterListChangeHandler();
		ListChangeHandler<BillingModelPivot> pivotList = billingModel.getPivotListChangeHandler();
		ListChangeHandler<BillingDescription> billingDescriptions = billingModel.getBillingDescriptionsListChangeHandler();
		ListChangeHandler<BillingModelTemplate> billingModelTemplates = billingModel.getBillingModelTemplatesListChangeHandler();
		ListChangeHandler<CalculateBillingItem> calculateBillingItems = billingModel.getCalculateBillingItemListChangeHandler();
		
		driverGroupList.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingModelDriverGroup : {}", item);
				billingModelDriverGroupService.deleteById(item.getId());
				log.trace("BillingModelDriverGroup deleted : {}", item.getId());
			}
		});
		enrichmentItems.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingModelEnrichmentItem : {}", item);
				billingModelEnrichmentItemRepository.deleteById(item.getId());
				log.trace("BillingModelEnrichmentItem deleted : {}", item.getId());
			}
		});
		
		groupingItem.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingModelGroupingItem : {}", item);
				billingModelGroupingItemRepository.deleteById(item.getId());
				log.trace("BillingModelGroupingItem deleted : {}", item.getId());
			}
		});
		
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingModelItem : {}", item);
				billingModelItemRepository.deleteById(item.getId());
				log.trace("BillingModelItem deleted : {}", item.getId());
			}
		});
		
		parameterList.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingModelParameter : {}", item);
				billingModelParameterRepository.deleteById(item.getId());
				log.trace("BillingModelParameter deleted : {}", item.getId());
			}
		});
		
		pivotList.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingModelPivot : {}", item);
				billingModelPivotRepository.deleteById(item.getId());
				log.trace("BillingModelPivot deleted : {}", item.getId());
			}
		});

		billingDescriptions.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingDescription : {}", item);
				billingDescriptionRepository.deleteById(item.getId());
				log.trace("BillingDescription deleted : {}", item.getId());
			}
		});

		billingModelTemplates.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete BillingModelTemplate : {}", item);
				if (item.getFilter() != null) {
					universeFilterService.delete(item.getFilter());
				}
				billingModelTemplateRepository.deleteById(item.getId());
				log.trace("BillingModelTemplate deleted : {}", item.getId());
			}
		});

		calculateBillingItems.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete CalculateBillingItem : {}", item);
				calculateBillingItemService.delete(item);
				log.trace("CalculateBillingItem deleted : {}", item.getId());
			}
		});
		
		if(billingModel.getFilter() != null) {
			universeFilterService.delete(billingModel.getFilter());
		}
			
		getRepository().deleteById(billingModel.getId());
		
		log.debug("Grid successfully to delete : {} ", billingModel);
	    return;	
	}
	

	@Override
	protected BrowserData getNewBrowserData(BillingModel item) {

		return new BrowserData(item);
	}

	@Override
	protected Specification<BillingModel> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<BillingModel> qBuilder = new RequestQueryBuilder<BillingModel>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));			
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

}
