/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
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
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.MaterializedGridService;

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
public class BillingParameterRoleBuilder extends BillingParameterBuilder {

	private String ROLE_ENTITY_NAME = "Role";
	private String CLIENT_ENTITY_NAME = "Client";
	private String BILLING_COMPANY_ENTITY_NAME = "Billing Company";
	private String ADRESS_ENTITY_NAME = "Address and Contact details";
	private String BILLING_CLIENT_REPOSITOPRY_GRID_NAME = "Client repository";
	private String BILLING_COMPANY_REPOSITOPRY_GRID_NAME = "Billing company repository";
	
	
	@Autowired
	protected MaterializedGridService materializedGridService;
	
	public BillingParameterRoleBuilder() {
		super();
		entityName = ROLE_ENTITY_NAME;
	}

	@Transactional
	public void buildParameters(HttpSession session, Locale locale) {
		HashMap<Parameter, IPersistent> parameters = new HashMap<Parameter, IPersistent>(0);
		try {
			this.billingModel = getOrBuildModel(BILLING_MODEL_NAME, session, locale);
			buildRoleParameters(parameters, session, locale);
			saveParameters(parameters, locale);
			
			Grille repositoryGrid = null;
			Parameter parameter = parameterRepository.findByCodeAndParameterType(
					BillingParameterCodes.BILLING_CLIENT_REPOSITORY_GRID, ParameterType.GRID);
			if (parameter != null && parameter.getLongValue() != null) {
				Optional<Grille> result = grilleService.getRepository().findById(parameter.getLongValue());
				if (result.isPresent()) {
					repositoryGrid = result.get();
				}
			}
			if (repositoryGrid == null) {
				repositoryGrid = buildClientRepositoryGrid();
				if (repositoryGrid != null) {
					grilleService.save(repositoryGrid, Locale.ENGLISH);
					if (parameter == null) {
						parameter = new Parameter(BillingParameterCodes.BILLING_CLIENT_REPOSITORY_GRID,
								ParameterType.GRID);
					}
					parameter.setLongValue(repositoryGrid.getId());
					parameterRepository.save(parameter);
				}
			}
			
			
			MaterializedGrid companyGrid = null;
			parameter = parameterRepository.findByCodeAndParameterType(
					BillingParameterCodes.BILLING_COMPANY_REPOSITORY_GRID, ParameterType.MAT_GRID);
			if (parameter != null && parameter.getLongValue() != null) {
				Optional<MaterializedGrid> result = materializedGridService.getRepository().findById(parameter.getLongValue());
				if (result.isPresent()) {
					companyGrid = result.get();
				}
			}
			if (companyGrid == null) {
				companyGrid = buildCompanyRepositoryGrid();
				if (companyGrid != null) {
					companyGrid = materializedGridService.save(companyGrid, Locale.ENGLISH);
					companyGrid = materializedGridService.publish(companyGrid, Locale.ENGLISH);
					if (parameter == null) {
						parameter = new Parameter(BillingParameterCodes.BILLING_COMPANY_REPOSITORY_GRID,
								ParameterType.MAT_GRID);
					}
					parameter.setLongValue(companyGrid.getId());
					parameterRepository.save(parameter);
				}
			}
			
			
			
		} catch (Exception ex) {
			log.error("Unable to save billing role parameters ", ex);
		}
	}
	
	
	

	private void buildRoleParameters(HashMap<Parameter, IPersistent> parametersMap, HttpSession session, Locale locale) throws Exception {
		buildParameterIfNotExist("Legal form", BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("VAT number", BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Phone", BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, ADRESS_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Email", BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, ADRESS_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Email cc", BillingParameterCodes.BILLING_ROLE_EMAIL_CC_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, ADRESS_ENTITY_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Street, number and box", BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, ADRESS_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Postal code", BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, ADRESS_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("City", BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, ADRESS_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Country", BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, ADRESS_ENTITY_NAME, this.billingModel, session, locale);

		Attribute attribute = (Attribute) buildParameterIfNotExist("Language",
				BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap,
				null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
		if (attribute.getId() == null) {
			attribute.setDeclared(true);
			getOrBuildAttributeValue("EN", attribute);
			getOrBuildAttributeValue("FR", attribute);
			getOrBuildAttributeValue("NL", attribute);
			getOrBuildAttributeValue("GE", attribute);
		}
		buildClientParameters(parametersMap, this.billingModel, session, locale);
		buildBillingCompanyParameters(parametersMap, this.billingModel, session, locale);

	}

	private void buildClientParameters(HashMap<Parameter, IPersistent> parametersMap, Model model, HttpSession session, Locale locale) throws Exception {
		buildParameterIfNotExist("Client ID", BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Client name", BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Contact title", BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_TITLE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Contact firstname", BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_FIRSTNAME_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Contact lastname", BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_LASTNAME_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);

		buildParameterIfNotExist("Department number",
				BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Department name", BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NAME_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, CLIENT_ENTITY_NAME, this.billingModel, session, locale);
	}

	private void buildBillingCompanyParameters(HashMap<Parameter, IPersistent> parametersMap, Model model, HttpSession session, Locale locale)
			throws Exception {
		buildParameterIfNotExist("Billing company ID", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, null, BILLING_COMPANY_ENTITY_NAME, this.billingModel, session, locale);
		buildParameterIfNotExist("Billing company name",
				BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE,
				parametersMap, null, BILLING_COMPANY_ENTITY_NAME, this.billingModel, session, locale);
	}

	public Grille buildClientRepositoryGrid() {
		Grille grid = new Grille();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName(BILLING_CLIENT_REPOSITOPRY_GRID_NAME);
		grid.setType(GrilleType.CLIENT_REPOSITORY);
		//grid.setUseLink(true);

		addColum(grid, "Client ID", BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.SYSTEM, true);
		addColum(grid, "Client Name", BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.SYSTEM, true);

		addColum(grid, "VAT number", BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Legal form", BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		
		addColum(grid, "Contact title", BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_TITLE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
		addColum(grid, "Contact firstname", BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_FIRSTNAME_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
		addColum(grid, "Contact lastname", BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_LASTNAME_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
				
		addColum(grid, "Phone", BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Email", BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, true);
		addColum(grid, "Email cc", BillingParameterCodes.BILLING_ROLE_EMAIL_CC_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Street, number and box", BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
		addColum(grid, "Postal code", BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
		addColum(grid, "City", BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Country", BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE,
				GrilleColumnCategory.USER, false);
		addColum(grid, "Language", BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);

		Attribute attribute = parameterService.getAttribute(BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE);
		Attribute nameAttribute = parameterService
				.getAttribute(BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE);

		if (attribute != null && nameAttribute != null) {
			grid.setAdminFilter(new UniverseFilter());
			grid.getAdminFilter().setAttributeFilter(new AttributeFilter());
			
			AttributeFilterItem item = new AttributeFilterItem();
			item.setDimensionType(DimensionType.ATTRIBUTE);
			item.setDimensionName(attribute.getName());
			item.setDimensionId(attribute.getId());
			item.setFilterVerb(FilterVerb.AND);
			item.setUseLink(false);
			item.setPosition(0);
			grid.getAdminFilter().getAttributeFilter().addItem(item);

			item = new AttributeFilterItem();
			item.setDimensionType(DimensionType.ATTRIBUTE);
			item.setDimensionName(nameAttribute.getName());
			item.setDimensionId(nameAttribute.getId());
			item.setFilterVerb(FilterVerb.AND);
			item.setUseLink(false);
			item.setPosition(1);
			grid.getAdminFilter().getAttributeFilter().addItem(item);
		}

		return grid;
	}
	
	
	private MaterializedGrid buildCompanyRepositoryGrid() {
		MaterializedGrid grid = new MaterializedGrid();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName(BILLING_COMPANY_REPOSITOPRY_GRID_NAME);
		grid.setVisibleInShortcut(true);
		grid.setEditable(true);
		grid.setAllowLineCounting(true);

		addColum(grid, "ID", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_ROLE_ID);
		addColum(grid, "Name", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_ROLE_NAME);
		addColum(grid, "VAT number", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_VAT_NBR);
		addColum(grid, "Legal form", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_LEGAL_FORM);
		addColum(grid, "Phone", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_PHONE);
		addColum(grid, "Email",DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_EMAIL);
		addColum(grid, "Street, number and box", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_STREET);	
		addColum(grid, "Postal code", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_POSTAL_CODE);
		addColum(grid, "City", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_CITY);
		addColum(grid, "Country", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_COUNTRY);
		addColum(grid, "Language", DimensionType.ATTRIBUTE, GrilleColumnCategory.BILLING_LANGUAGE);
				
		return grid;
	}
	
	protected void addColum(MaterializedGrid grid, String name, DimensionType type, GrilleColumnCategory role) {
		if (type != null) {
			int position = grid.getColumnListChangeHandler().getItems().size();			
			MaterializedGridColumn column = new MaterializedGridColumn();
			column.setName(name);
			column.setRole(role);
			column.setType(type);
			column.setPosition(position);
			column.setCategory(GrilleColumnCategory.USER);
			column.setEditable(true);
			grid.getColumnListChangeHandler().addNew(column);
		}		
	}

//	public Grille buildCompanyRepositoryGrid() {
//		Grille grid = new Grille();
//		grid.setCategory(GrilleCategory.SYSTEM);
//		grid.setName(BILLING_COMPANY_REPOSITOPRY_GRID_NAME);
//		grid.setType(GrilleType.RECONCILIATION);
//		//grid.setUseLink(true);
//
//		addColum(grid, "Client ID", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE,
//				ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);
//		addColum(grid, "Client name", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_NAME_ATTRIBUTE,
//				ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);
//
//		addColum(grid, "VAT number", BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE,
//				GrilleColumnCategory.USER, false);
//		addColum(grid, "Legal form", BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE,
//				GrilleColumnCategory.USER, false);
//		addColum(grid, "Phone", BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE,
//				GrilleColumnCategory.USER, false);
//		addColum(grid, "Email", BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE,
//				GrilleColumnCategory.USER, true);
//		addColum(grid, "Street, number and box", BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE,
//				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Postal code", BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE,
//				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "City", BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE,
//				GrilleColumnCategory.USER, false);
//		addColum(grid, "Country", BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE,
//				GrilleColumnCategory.USER, false);
//		addColum(grid, "Language", BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE,
//				ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//
//		Attribute attribute = parameterService
//				.getAttribute(BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE);
//		Attribute nameAttribute = parameterService
//				.getAttribute(BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_NAME_ATTRIBUTE);
//
//		if (attribute != null && nameAttribute != null) {
//			List<AttributeFilterItem> items = new ArrayList<>();
//			AttributeFilterItem item = new AttributeFilterItem();
//			item.setDimensionType(DimensionType.ATTRIBUTE);
//			item.setDimensionName(attribute.getName());
//			item.setDimensionId(attribute.getId());
//			item.setFilterVerb(FilterVerb.AND);
//			item.setUseLink(true);
//			item.setPosition(0);
//			items.add(item);
//
//			item = new AttributeFilterItem();
//			item.setDimensionType(DimensionType.ATTRIBUTE);
//			item.setDimensionName(nameAttribute.getName());
//			item.setDimensionId(nameAttribute.getId());
//			item.setFilterVerb(FilterVerb.AND);
//			item.setUseLink(true);
//			item.setPosition(1);
//			items.add(item);
//
//			grid.setAdminFilter(new UniverseFilter());
//			grid.getAdminFilter().setAttributeFilter(new AttributeFilter());
//			grid.getAdminFilter().getAttributeFilter().setItems(items);
//		}
//
//		return grid;
//	}

//	private Grille buildClientGrid() {
//		Grille grid = new Grille();
//		grid.setName(CLIENT_GRID_NAME);
//		grid.setReport(false);
//		grid.setReconciliation(false);
//		grid.setUseLink(true);
//		GroupService groupService = new GroupService();
//		groupService.setUserSession(getUserSession());
//		grid.setGroup(groupService.getDefaultGroupBySubject(SubjectType.INPUT_GRID.getLabel()));
//		
//		addColum(grid, "Client ID", BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);
//		addColum(grid, "Client name", BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, false);
//		//addColum(grid, "Client group", BillingParameterCodes.BILLING_ROLE_CLIENT_GROUP_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);			
//		addColum(grid, "VAT number", BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Legal form", BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Phone", BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Email", BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, true);
//		addColum(grid, "Street, number and box", BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Postal code", BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "City", BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Country", BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Language", BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		return grid;
//	}

//	private DynamicForm buildClientForm(Grille grid) {
//		DynamicForm form = new DynamicForm();
//		form.setName("Client Form");	
//		form.setGridOid(grid.getOid());
//		GroupService groupService = new GroupService();
//		groupService.setUserSession(getUserSession());
//		form.setGroup(groupService.getDefaultGroupBySubject(SubjectType.DYNAMIC_FORM.getLabel()));
//		
//		DynamicFormApplicationMenu menu = new DynamicFormApplicationMenu();
//		menu.setActive(true);
//		menu.setParent(PARENT_MENU_NAME);
//		menu.setCaption("Clients");
//		menu.setHasNewMenu(true);
//		menu.setNewMenuCaption("New Client");
//		menu.setHasListMenu(true);
//		menu.setListMenuCaption("List Clients");
//		menu.setShowAll(false);
//		form.setMenu(menu);
//				
//		DynamicFormField field = addFormField(form, grid, "Nature", BillingParameterCodes.BILLING_ROLE_NATURE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM,
//				DynamicFormFieldType.SELECTION, DynamicFormFieldNature.INPUT, false, true, true, true);
//		if(field != null) {
//			field.setDefaultValue(getValueNameByCodeAndType(BillingParameterCodes.BILLING_ROLE_NATURE_COMPANY_VALUE, ParameterType.ATTRIBUTE_VALUE));
//		}
//		
//		addFormField(form, grid, "Client ID", BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, true, true, true, true);
//		addFormField(form, grid, "Client company name", BillingParameterCodes.BILLING_ROLE_CLIENT_COMPANY_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Client first name", BillingParameterCodes.BILLING_ROLE_CLIENT_FIRST_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Client last name", BillingParameterCodes.BILLING_ROLE_CLIENT_LAST_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Client group", BillingParameterCodes.BILLING_ROLE_CLIENT_GROUP_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);		
//		addFormField(form, grid, "VAT number", BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Legal form", BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Phone", BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, true, true, true);
//		addFormField(form, grid, "Email", BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, true, true, true);
//		addFormField(form, grid, "Street, number and box", BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Postal code", BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "City", BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Country", BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		field = addFormField(form, grid, "Language", BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		if(field != null) {
//			field.setDefaultValue("FR");
//		}
//		return form;
//	}

//	private Grille buildBillingCompanyGrid() {
//		Grille grid = new Grille();
//		grid.setName(BILLING_COMPANY_GRID_NAME);
//		grid.setReport(false);
//		grid.setReconciliation(false);
//		grid.setUseLink(true);
//		GroupService groupService = new GroupService();
//		groupService.setUserSession(getUserSession());
//		grid.setGroup(groupService.getDefaultGroupBySubject(SubjectType.INPUT_GRID.getLabel()));
//		
//		//addColum(grid, "Nature", BillingParameterCodes.BILLING_ROLE_NATURE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Billing company ID", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, true);
//		addColum(grid, "Billing company name", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_COMPANY_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM, false);
//		//addColum(grid, "First name", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_FIRST_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		//addColum(grid, "Last name", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_LAST_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);			
//		addColum(grid, "VAT number", BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Legal form", BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Phone", BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Email", BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, true);
//		addColum(grid, "Street, number and box", BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Postal code", BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "City", BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		addColum(grid, "Country", BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER, false);
//		return grid;
//	}

//	private DynamicForm buildBillingCompanyForm(Grille grid) {
//		DynamicForm form = new DynamicForm();
//		form.setName("Billing Company Form");
//		form.setGridOid(grid.getOid());
//		GroupService groupService = new GroupService();
//		groupService.setUserSession(getUserSession());
//		form.setGroup(groupService.getDefaultGroupBySubject(SubjectType.DYNAMIC_FORM.getLabel()));
//		
//		DynamicFormApplicationMenu menu = new DynamicFormApplicationMenu();
//		menu.setActive(true);
//		menu.setParent(PARENT_MENU_NAME);
//		menu.setCaption("Billing Companies");
//		menu.setHasNewMenu(true);
//		menu.setNewMenuCaption("New Billing Company");
//		menu.setHasListMenu(true);
//		menu.setListMenuCaption("List Billing Companies");
//		menu.setShowAll(false);
//		form.setMenu(menu);
//				
//		addFormField(form, grid, "Billing company ID", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, true, true, true, true);
//		addFormField(form, grid, "Billing company name", BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_COMPANY_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.SYSTEM,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, true, true, true);
//		addFormField(form, grid, "VAT number", BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Legal form", BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Phone", BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, true, true, true);
//		addFormField(form, grid, "Email", BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Street, number and box", BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Postal code", BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "City", BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		addFormField(form, grid, "Country", BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE, GrilleColumnCategory.USER,
//				DynamicFormFieldType.SELECTION_AND_EDITION, DynamicFormFieldNature.INPUT, false, false, true, true);
//		return form;
//	}

}
