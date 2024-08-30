/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.JoinKey;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingParameterEventBuilder extends BillingParameterBuilder {

	private String EVENT_ENTITY_NAME = "Billing event";
	private String BILLING_EVENT_REPOSITOPRY_GRID_NAME = "Billing event repository";
	private String BILLING_EVENT_GRID_NAME = "Billing events";
	private String BILLING_JOIN_NAME = "Billing join";

	private String PARENT_MENU_NAME = "Billing";
	
	@Autowired
	protected JoinService joinService;

	public BillingParameterEventBuilder() {
		super();
		entityName = EVENT_ENTITY_NAME;
	}

	@Transactional
	public void buildParameters(HttpSession session, Locale locale) {
		HashMap<Parameter, IPersistent> parameters = new HashMap<Parameter, IPersistent>(0);
		try {
			this.billingModel = getOrBuildModel(BILLING_MODEL_NAME, session, locale);
			buildEventParameters(parameters, session, locale);
			saveParameters(parameters, locale);

			Grille repositoryGrid = null;
			Parameter parameter = parameterRepository.findByCodeAndParameterType(
					BillingParameterCodes.BILLING_EVENT_REPOSITORY_GRID, ParameterType.GRID);
			if (parameter != null && parameter.getLongValue() != null) {
				Optional<Grille> result = grilleService.getRepository().findById(parameter.getLongValue());
				if (result.isPresent()) {
					repositoryGrid = result.get();
				}
			}
			if (repositoryGrid == null) {
				repositoryGrid = buildRepositoryGrid();
				if (repositoryGrid != null) {
					grilleService.save(repositoryGrid, Locale.ENGLISH);
					if (parameter == null) {
						parameter = new Parameter(BillingParameterCodes.BILLING_EVENT_REPOSITORY_GRID,
								ParameterType.GRID);
					}
					parameter.setLongValue(repositoryGrid.getId());
					parameterRepository.save(parameter);
				}
			}
			
			
			Join join = null;
			parameter = parameterRepository.findByCodeAndParameterType(
					BillingParameterCodes.BILLING_JOIN, ParameterType.JOIN);
			if (parameter != null && parameter.getLongValue() != null) {
				Optional<Join> result = joinService.getRepository().findById(parameter.getLongValue());
				if (result.isPresent()) {
					join = result.get();
				}
			}
			if (join == null) {
				join = buildBillingJoin();
				if (join != null) {
					joinService.save(join, Locale.ENGLISH);
					if (parameter == null) {
						parameter = new Parameter(BillingParameterCodes.BILLING_JOIN,
								ParameterType.JOIN);
					}
					parameter.setLongValue(join.getId());
					parameterRepository.save(parameter);
				}
			}

		} catch (Exception ex) {
			log.error("Unable to save billing role parameters ", ex);
		}
	}

	@Transactional
	public void rebuildBillingEventRepositoryGrid() {
		Grille repositoryGrid = null;
		Parameter parameter = parameterRepository
				.findByCodeAndParameterType(BillingParameterCodes.BILLING_EVENT_REPOSITORY_GRID, ParameterType.GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<Grille> result = grilleService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				repositoryGrid = result.get();
			}
			try {
				if (repositoryGrid != null) {
					grilleService.delete(repositoryGrid);
				}
				repositoryGrid = buildRepositoryGrid();
				if (repositoryGrid != null) {
					grilleService.save(repositoryGrid, Locale.ENGLISH);
					parameter.setLongValue(repositoryGrid.getId());
					parameterRepository.save(parameter);
				}
			} catch (Exception ex) {
				log.error("Unable to save billing repositpory ", ex);
			}
		}
	}

	private void buildEventParameters(HashMap<Parameter, IPersistent> parametersMap, HttpSession session, Locale locale) throws Exception {

		Measure billingMeasure = buildBillingMeasure();		
		Period billingPeriod = buildBillingPeriod();		
		
		buildParameterIfNotExist("Billing event description", BillingParameterCodes.BILLING_EVENT_DESCRIPTION_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, EVENT_ENTITY_NAME, this.billingModel, session, locale);

		Attribute attribute = (Attribute) buildParameterIfNotExist("Billing event status",
				BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null,
				EVENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Billed", BillingParameterCodes.BILLING_EVENT_STATUS_BILLED_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, EVENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Draft", BillingParameterCodes.BILLING_EVENT_STATUS_DRAFT_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, EVENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Frozen", BillingParameterCodes.BILLING_EVENT_STATUS_FROZEN_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, EVENT_ENTITY_NAME, this.billingModel, session, locale);
		if (attribute.getId() == null) {
			attribute.setDeclared(true);
		}

		attribute = (Attribute) buildParameterIfNotExist("Billing event type",
				BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null,
				EVENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Invoice", BillingParameterCodes.BILLING_EVENT_TYPE_INVOICE_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, EVENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Credit note", BillingParameterCodes.BILLING_EVENT_TYPE_CREDIT_NOTE_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, EVENT_ENTITY_NAME, this.billingModel, session, locale);
		if (attribute.getId() == null) {
			attribute.setDeclared(true);
		}

		attribute = (Attribute) buildParameterIfNotExist("Billing event A/M",
				BillingParameterCodes.BILLING_EVENT_AM_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null,
				EVENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Automatic", BillingParameterCodes.BILLING_EVENT_AM_AUTO_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, EVENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Manual", BillingParameterCodes.BILLING_EVENT_AM_MANUAL_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, EVENT_ENTITY_NAME, this.billingModel, session, locale);
		if (attribute.getId() == null) {
			attribute.setDeclared(true);
		}

		buildParameterIfNotExist("Billing run", BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, EVENT_ENTITY_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Billing event date", BillingParameterCodes.BILLING_EVENT_DATE_PERIOD,
				ParameterType.PERIOD, parametersMap, billingPeriod, BILLING_PERIOD_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Billing run date", BillingParameterCodes.BILLING_RUN_DATE_PERIOD,
				ParameterType.PERIOD, parametersMap, billingPeriod, BILLING_PERIOD_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Billing driver", BillingParameterCodes.BILLING_EVENT_BILLING_DRIVER_MEASURE,
				ParameterType.MEASURE, parametersMap, billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);
		
		buildParameterIfNotExist("Unit cost", BillingParameterCodes.BILLING_EVENT_UNIT_COST_MEASURE,
				ParameterType.MEASURE, parametersMap, billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Billing amount", BillingParameterCodes.BILLING_EVENT_BILLING_AMOUNT_MEASURE,
				ParameterType.MEASURE, parametersMap, billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("VAT rate", BillingParameterCodes.BILLING_EVENT_VAT_RATE_MEASURE,
				ParameterType.MEASURE, parametersMap, billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);

	}

	private Grille buildRepositoryGrid() {
		Grille grid = new Grille();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName(BILLING_EVENT_REPOSITOPRY_GRID_NAME);
		grid.setType(GrilleType.BILLING_EVENT_REPOSITORY);
		//grid.setUseLink(true);

//		GroupService groupService = new GroupService();
//		groupService.setUserSession(getUserSession());
//		grid.setGroup(groupService.getDefaultGroupBySubject(SubjectType.REPORT_GRID.getLabel()));

		addColum(grid, "Billing event type", BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);
		addColum(grid, "Client ID", BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.SYSTEM, true);
		addColum(grid, "Billing event date", BillingParameterCodes.BILLING_EVENT_DATE_PERIOD, ParameterType.PERIOD,
				GrilleColumnCategory.SYSTEM, true);
		addColum(grid, "Billing event description", BillingParameterCodes.BILLING_EVENT_DESCRIPTION_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, false);

		addColum(grid, "Billing driver", BillingParameterCodes.BILLING_EVENT_BILLING_DRIVER_MEASURE,
				ParameterType.MEASURE, GrilleColumnCategory.SYSTEM, false);
		
		addColum(grid, "Unit cost", BillingParameterCodes.BILLING_EVENT_UNIT_COST_MEASURE, ParameterType.MEASURE,
				GrilleColumnCategory.SYSTEM, false);
		addColum(grid, "VAT rate", BillingParameterCodes.BILLING_EVENT_VAT_RATE_MEASURE, ParameterType.MEASURE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Billing amount", BillingParameterCodes.BILLING_EVENT_BILLING_AMOUNT_MEASURE,
				ParameterType.MEASURE, GrilleColumnCategory.SYSTEM, false);
		addColum(grid, "Billing event status", BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);

		addColum(grid, "Billing run", BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Invoice number", BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
		addColum(grid, "Invoice description", BillingParameterCodes.BILLING_INVOICE_DESCRIPTION_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);

		Attribute attribute = parameterService.getAttribute(BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE);
		if (attribute != null) {
			AttributeFilterItem item = new AttributeFilterItem();
			item.setDimensionType(DimensionType.ATTRIBUTE);
			item.setOperator(AttributeOperator.NOT_NULL);
			item.setDimensionName(attribute.getName());
			item.setDimensionId(attribute.getId());
			item.setFilterVerb(FilterVerb.AND);
			item.setUseLink(true);
			item.setPosition(0);

			grid.setAdminFilter(new UniverseFilter());
			grid.getAdminFilter().setAttributeFilter(new AttributeFilter());
			grid.getAdminFilter().getAttributeFilter().getItemListChangeHandler().addNew(item);
		}

		return grid;
	}
	
	
	private Join buildBillingJoin() {
		
		
		log.debug("Try to read billing event repository ");
		Grille eventRepo = getGrid(BillingParameterCodes.BILLING_EVENT_REPOSITORY_GRID);
		if(eventRepo == null) {
			throw new BcephalException("Unable to build billing join. Billing event repository is not found!");
		}
		log.debug("Billing event repository read!");
		
		log.debug("Try to read client repository ");
		Grille clientRepo = getGrid(BillingParameterCodes.BILLING_CLIENT_REPOSITORY_GRID);
		if(clientRepo == null) {
			throw new BcephalException("Unable to build billing join. Client repository is not found!");
		}
		log.debug("Client repository read!");
		
		Join join = new Join();
		join.setName(BILLING_JOIN_NAME);
		join.setConsolidated(false);
		
//		GroupService groupService = new GroupService();
//		groupService.setUserSession(getUserSession());
//		join.setGroup(groupService.getDefaultGroupBySubject(SubjectType.JOIN_GRID.getLabel()));
		
		JoinGrid grid1 = new JoinGrid();
		grid1.setGridId(eventRepo.getId());
		grid1.setName(eventRepo.getName());
		grid1.setGridType(JoinGridType.REPORT_GRID);
		grid1.setPosition(0);
		grid1.setMainGrid(true);
		join.getGridListChangeHandler().addNew(grid1);
		
		JoinGrid grid2 = new JoinGrid();
		grid2.setGridId(clientRepo.getId());
		grid2.setName(clientRepo.getName());
		grid2.setPosition(1);
		grid2.setMainGrid(false);
		grid2.setGridType(JoinGridType.REPORT_GRID);
		join.getGridListChangeHandler().addNew(grid2);
		
		int position = 0;
		for(GrilleColumn gColumn : clientRepo.getColumnListChangeHandler().getItems()) {
			JoinColumn column = new JoinColumn();
			column.setGridId(clientRepo.getId());
			column.setGridType(grid1.getGridType());
			column.setColumnId(gColumn.getId());
			column.setDimensionId(gColumn.getDimensionId());
			column.setDimensionName(gColumn.getDimensionName());
			column.setType(gColumn.getType());
			column.setName(gColumn.getName());
			column.setPublicationDimensionId(gColumn.getDimensionId());
			column.setPublicationDimensionName(gColumn.getDimensionName());
			column.setPosition(position++);
			join.getColumnListChangeHandler().addNew(column);
		}		
		for(GrilleColumn gColumn : eventRepo.getColumnListChangeHandler().getItems()) {
			JoinColumn column = getColumn(join.getColumnListChangeHandler().getItems(), gColumn.getDimensionId(), gColumn.getType());
			if(column == null) {
				column = new JoinColumn();
				column.setGridId(eventRepo.getId());
				column.setGridType(grid2.getGridType());
				column.setColumnId(gColumn.getId());
				column.setDimensionId(gColumn.getDimensionId());
				column.setDimensionName(gColumn.getDimensionName());
				column.setType(gColumn.getType());
				column.setName(gColumn.getName());	
				column.setPublicationDimensionId(gColumn.getDimensionId());
				column.setPublicationDimensionName(gColumn.getDimensionName());
				column.setPosition(position++);
				join.getColumnListChangeHandler().addNew(column);
			}
		}
				
		Attribute clientIdAttribute = parameterService.getAttribute(BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE);
		if(clientIdAttribute != null) {
			JoinKey key = new JoinKey();			
			key.setDimensionType(DimensionType.ATTRIBUTE);
			key.setGridId1(eventRepo.getId());
			List<GrilleColumn> cols = eventRepo.getColumns(DimensionType.ATTRIBUTE, clientIdAttribute.getId());
			GrilleColumn col = cols.size() > 0 ? cols.get(0) : null;
			key.setGridType1(grid1.getGridType());
			key.setColumnId1(col != null ? col.getId() : null);
			key.setValueId1(clientIdAttribute.getId());		
			
			key.setGridId2(clientRepo.getId());
			cols = clientRepo.getColumns(DimensionType.ATTRIBUTE, clientIdAttribute.getId());
			col = cols.size() > 0 ? cols.get(0) : null;
			key.setColumnId2(col != null ? col.getId() : null);
			key.setGridType2(grid2.getGridType());
			key.setValueId2(clientIdAttribute.getId());			
			join.getKeyListChangeHandler().addNew(key);
		}
				
		return join;
	}
	
	private JoinColumn getColumn(List<JoinColumn> columns, Long dimensionId, DimensionType type) {
		for(JoinColumn column : columns) {
			if(column.getDimensionId().equals(dimensionId) && column.getType() == type) {
				return column;
			}
		}
		return null;
	}
	

}
