/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleType;

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
public class BillingParameterInvoiceBuilder extends BillingParameterBuilder {

	private String INVOICE_ENTITY_NAME = "Invoice";
	private String BILLING_INVOICE_REPOSITOPRY_GRID_NAME = "Invoice repository";
	private String BILLING_CREDIT_NOTE_REPOSITOPRY_GRID_NAME = "Credit note repository";

	public BillingParameterInvoiceBuilder() {
		super();
		entityName = INVOICE_ENTITY_NAME;
	}

	@Transactional
	public void buildParameters(HttpSession session, Locale locale) {
		HashMap<Parameter, IPersistent> parameters = new HashMap<Parameter, IPersistent>(0);
		try {
			this.billingModel = getOrBuildModel(BILLING_MODEL_NAME, session, locale);
			buildInvoiceParameters(parameters, session, locale);
			saveParameters(parameters, locale);
			buildIncrementalNumber("Invoice Number Generator", BillingParameterCodes.BILLING_INVOICE_NUMBER_SEQUENCE,
					ParameterType.INCREMENTAL_NUMBER);
			buildIncrementalNumber("Credit Note Number Generator",
					BillingParameterCodes.BILLING_CREDIT_NOTE_NUMBER_SEQUENCE, ParameterType.INCREMENTAL_NUMBER);
			
			
			Grille repositoryGrid = null;
			Parameter parameter = parameterRepository.findByCodeAndParameterType(
					BillingParameterCodes.BILLING_INVOICE_REPOSITORY_GRID, ParameterType.GRID);
			if (parameter != null && parameter.getLongValue() != null) {
				Optional<Grille> result = grilleService.getRepository().findById(parameter.getLongValue());
				if (result.isPresent()) {
					repositoryGrid = result.get();
				}
			}
			if (repositoryGrid == null) {
				repositoryGrid = buildRepositoryGrid(false);
				if (repositoryGrid != null) {
					grilleService.save(repositoryGrid, Locale.ENGLISH);
					if (parameter == null) {
						parameter = new Parameter(BillingParameterCodes.BILLING_INVOICE_REPOSITORY_GRID,
								ParameterType.GRID);
					}
					parameter.setLongValue(repositoryGrid.getId());
					parameterRepository.save(parameter);
				}
			}
			
			repositoryGrid = null;
			parameter = null;
			parameter = parameterRepository.findByCodeAndParameterType(
					BillingParameterCodes.BILLING_CREDIT_NOTE_REPOSITORY_GRID, ParameterType.GRID);
			if (parameter != null && parameter.getLongValue() != null) {
				Optional<Grille> result = grilleService.getRepository().findById(parameter.getLongValue());
				if (result.isPresent()) {
					repositoryGrid = result.get();
				}
			}
			if (repositoryGrid == null) {
				repositoryGrid = buildRepositoryGrid(true);
				if (repositoryGrid != null) {
					grilleService.save(repositoryGrid, Locale.ENGLISH);
					if (parameter == null) {
						parameter = new Parameter(BillingParameterCodes.BILLING_CREDIT_NOTE_REPOSITORY_GRID,
								ParameterType.GRID);
					}
					parameter.setLongValue(repositoryGrid.getId());
					parameterRepository.save(parameter);
				}
			}
			
			buildBillTemplate();
			buildVariables();
		} catch (Exception ex) {
			log.error("Unable to save billing role parameters ", ex);
		}
	}
	
	private Grille buildRepositoryGrid(boolean isCreditNote) {
		Grille grid = new Grille();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName(isCreditNote ? BILLING_CREDIT_NOTE_REPOSITOPRY_GRID_NAME : BILLING_INVOICE_REPOSITOPRY_GRID_NAME);
		grid.setType(isCreditNote ? GrilleType.CREDIT_NOTE_REPOSITORY : GrilleType.INVOICE_REPOSITORY);
		//grid.setUseLink(false);

//		GroupService groupService = new GroupService();
//		groupService.setUserSession(getUserSession());
//		grid.setGroup(groupService.getDefaultGroupBySubject(SubjectType.REPORT_GRID.getLabel()));

		addColum(grid, "Type", BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);
		addColum(grid, "Reference", BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);
		addColum(grid, "Client ID", BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Client name", BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);		
		addColum(grid, "Amount without VAT", BillingParameterCodes.BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE, ParameterType.MEASURE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "VAT amount", BillingParameterCodes.BILLING_INVOICE_VAT_AMOUNT_MEASURE, ParameterType.MEASURE,
				GrilleColumnCategory.USER, false);	
		addColum(grid, "Total amount", BillingParameterCodes.BILLING_INVOICE_TOTAL_AMOUNT_MEASURE, ParameterType.MEASURE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Status", BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.SYSTEM, true);
		addColum(grid, "Mail status", BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, true);
		addColum(grid, "Run n°", BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Creation date", BillingParameterCodes.BILLING_RUN_DATE_PERIOD, ParameterType.PERIOD,
				GrilleColumnCategory.USER, false);
		
		return grid;
	}
	
	private void buildVariables() throws Exception {
		buildVariable(BillingParameterCodes.VARIABLE_CLIENT_ID, DimensionType.ATTRIBUTE);
		buildVariable(BillingParameterCodes.VARIABLE_INVOICE_NBR, DimensionType.ATTRIBUTE);
	}

	private void buildBillTemplate() throws Exception {
//		BillTemplate template = null;
//		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_DEFAULT_TEMPLATE, ParameterType.BILL_TEMPLATE);
//		if(parameter == null) {
//			parameter = new Parameter(BillingParameterCodes.BILLING_INVOICE_DEFAULT_TEMPLATE, ParameterType.BILL_TEMPLATE);
//		}		
//		if(parameter.getLongValue() != null ) {
//			template = service.getByOid(parameter.getValueOid());
//		}		
//		if(template == null) {
//			template = new BillTemplate();
//			template.setCode(BillingRunnerClientPerClient.DEFAULT_BILL_TEMPLATE_CODE);
//			template.setName("Default template");
//			template.setSystemTemplate(true);
//			template.setVisibleInShortcut(true);
//			template.setDescription("Default template");
//			
//			File souce = Paths.get(System.getProperty("user.dir"), "resources", "bill").toFile();
//	        if(souce.exists()) {	        	
//	        	File file = Paths.get(FilenameUtils.separatorsToSystem(getUserSession().filePath), "billtemplates", BillingRunnerClientPerClient.DEFAULT_BILL_TEMPLATE_CODE).toFile();
//				if(!file.exists()) {
//					file.mkdirs();					
//				}
//				FileUtil.copy(souce, file);	        	
//	        	template.setMainFile("invoice_detail.jrxml");
//	        	template.setRepository(file.getPath());
//	        }		
//	        	        
//			service.saveWithoutComit(template);
//		}		
//		parameter.setValueOid(template.getOid());
//		saveWithoutComit(parameter);
	}

	private void buildInvoiceParameters(HashMap<Parameter, IPersistent> parametersMap, HttpSession session, Locale locale) throws Exception {
		Measure billingMeasure = buildBillingMeasure();		
		Period billingPeriod = buildBillingPeriod();
		
		Attribute invoiceAttribute = (Attribute) buildParameterIfNotExist("Invoice number",
				BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null,
				INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Sub invoice number",
				BillingParameterCodes.BILLING_INVOICE_SUB_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		Attribute attribute = (Attribute) buildParameterIfNotExist("Invoice status",
				BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null,
				INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Draft", BillingParameterCodes.BILLING_INVOICE_STATUS_DRAFT_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("To check", BillingParameterCodes.BILLING_INVOICE_STATUS_TO_CHECK_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Validated", BillingParameterCodes.BILLING_INVOICE_STATUS_VALIDATED_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		if (attribute.getId() == null) {
			attribute.setDeclared(true);
		}

		attribute = (Attribute) buildParameterIfNotExist("Sending status",
				BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap,
				null, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Sent", BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_SENT_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Not yet sent",
				BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE,
				parametersMap, attribute, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		if (attribute.getId() == null) {
			attribute.setDeclared(true);
		}
				
		attribute = (Attribute) buildParameterIfNotExist("Invoice type",
				BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null,
				INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("INVOICE", BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_INVOICE_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("CREDIT NOTE", BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_CN_VALUE,
				ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, INVOICE_ENTITY_NAME, this.billingModel, session, locale);		
		if (attribute.getId() == null) {
			attribute.setDeclared(true);
		}
		
		buildParameterIfNotExist("Communication message",
				BillingParameterCodes.BILLING_INVOICE_COMMUNICATION_MESSAGE_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, INVOICE_ENTITY_NAME, this.billingModel, session, locale);
		
		buildParameterIfNotExist("Invoice description",
				BillingParameterCodes.BILLING_INVOICE_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, INVOICE_ENTITY_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Invoice amount (excl VAT)",
				BillingParameterCodes.BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE, ParameterType.MEASURE, parametersMap,
				billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("VAT amount", BillingParameterCodes.BILLING_INVOICE_VAT_AMOUNT_MEASURE,
				ParameterType.MEASURE, parametersMap, billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Invoice total amount", BillingParameterCodes.BILLING_INVOICE_TOTAL_AMOUNT_MEASURE,
				ParameterType.MEASURE, parametersMap, billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);
		
		buildParameterIfNotExist("Invoice validation date", BillingParameterCodes.BILLING_INVOICE_VALIDATION_DATE_PERIOD,
				ParameterType.PERIOD, parametersMap, billingPeriod, BILLING_PERIOD_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Invoice date", BillingParameterCodes.BILLING_INVOICE_DATE_PERIOD,
				ParameterType.PERIOD, parametersMap, billingPeriod, BILLING_PERIOD_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Due date", BillingParameterCodes.BILLING_INVOICE_DUE_DATE_PERIOD,
				ParameterType.PERIOD, parametersMap, billingPeriod, BILLING_PERIOD_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Due date calculation",
				BillingParameterCodes.BILLING_INVOICE_DUE_DATE_CALCULATION_MEASURE, ParameterType.MEASURE,
				parametersMap, billingMeasure, BILLING_MEASURE_NAME, this.billingModel, session, locale);

		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(
				BillingParameterCodes.BILLING_CREDIT_NOTE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		if (parameter == null) {
			parameter = new Parameter(BillingParameterCodes.BILLING_CREDIT_NOTE_NUMBER_ATTRIBUTE,
					ParameterType.ATTRIBUTE);
		}
		parametersMap.put(parameter, invoiceAttribute);

	}

}
