package com.moriset.bcephal.grid.domain.form;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.FilterItemType;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;

public class DynamicFactories {
	
	public static List<FormModel> buildFormModels(MaterializedGridService materializedGridService, GrilleService grilleService) {
		List<FormModel> items = new ArrayList<>();
		
		items.add(buildADV001Manualprefundingadvisement(materializedGridService));
		items.add(buildBIL001Billingcompany(materializedGridService));
		items.add(buildBIL002Clientlist(grilleService));
		items.add(buildBIL100Billingevents(grilleService));
		items.add(buildBIL900Orderform(materializedGridService, grilleService));
		items.add(buildPDT001EPBProductsandServices(materializedGridService));
		items.add(buildPRI001Yearlyfee(materializedGridService));
		items.add(buildPRI002Volumefee(materializedGridService));
		items.add(buildPRI003Markupdedicatedfee(materializedGridService));
		items.add(buildSCH001MCIPMLbillingevent(materializedGridService, grilleService));
		items.add(buildSCH002ClientIDenrichmentonPMLinvoice(materializedGridService));
		return items;
	}

	
	public static FormModel buildADV001Manualprefundingadvisement(MaterializedGridService materializedGridService) {
		FormModel model = new FormModel();
		
		model.setName("ADV001 Manual prefunding advisement");
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.getAdminFilter().getAttributeFilter().addItem(buildAttributeFilterItem(null, DataSourceType.UNIVERSE, 5L, "Advisement Account ID", DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Prefunding Advisement", null));
		model.setMenu(buildFormModelMenu("ADV001 Manual advisement", "Prefunding advisement", "List prefunding advisement", "New prefunding advisement"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Member bank name", null, 33L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Member bank name", 0, false, "Member bank name", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.SELECTION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "Member bank name is mandatory", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "PML type", null, 100L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "PML type", 0, false, "PML type", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.SELECTION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "PML type is mandatory", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Scheme ID", null, 68L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Scheme ID", 0, false, "Scheme ID", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.SELECTION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "Scheme ID is mandatory", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Member Bank ID", null, 31L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Member Bank ID", 0, false, "Member Bank ID", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.MATERIALIZED_GRID, null, null, 1646L, true, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 1647L, ReferenceConditionItemType.FIELD, null, null, buildPeriodValue(), 253L)), materializedGridService, null, "MAP110 Member Bank ID vs Member Bank Name", null)));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Advisement type", null, 5L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Advisement Account ID", 0, false, "Advisement Account ID", FormModelFieldNature.INPUT, true, 1, FormModelFieldType.SELECTION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "PML Type is mandatory", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Advisement message", null, 7L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Advisement message", 0, false, "Advisement message", FormModelFieldNature.INPUT, false, 7, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Value date", null, 1L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Value date", 0, false, "Value date", FormModelFieldNature.INPUT, false, 8, FormModelFieldType.EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.PERIOD, "Value date  is mandatory", null, null, FormModelFieldValidationType.MANDATORY), buildValidationItem(true, buildPeriodValue(), null, DimensionType.PERIOD, "Value date  is mandatory", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "D-C", null, 23L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "D-C", 0, false, "D-C", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.SELECTION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "D-C sign is mandatory", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Posting amount", null, 1L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Posting amount", 0, false, "Posting amount", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Manual advisement", "Data source", null, 150L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Data source", 0, false, "Data source", FormModelFieldNature.INPUT, true, 9, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, new BigDecimal(1), null, "Check new", null, 47L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Check new", 0, false, "Check new", FormModelFieldNature.INPUT, true, 10, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}
	
	public static FormModel buildBIL001Billingcompany(MaterializedGridService materializedGridService) {
		FormModel model = new FormModel();
		
		model.setName("BIL001 Billing company");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("SYS001 Billing company repository");
		Long dataSourceId = materializedGridService.getByName("SYS001 Billing company repository").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.setMenu(buildFormModelMenu("Billing company", "Billing forms", "List", "New"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "ID", null, 583L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "ID", 0, false, "ID", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Name", null, 584L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Name", 0, false, "Name", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Street, number and box", null, 589L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Street, number and box", 0, false, "Street, number and box", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "VAT number", null, 585L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "VAT number", 0, false, "VAT number", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Postal code", null, 590L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Postal code", 0, false, "Postal code", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "City", null, 591L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "City", 0, false, "City", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Email", null, 588L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Email", 0, false, "Email", FormModelFieldNature.INPUT, false, 7, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Country", null, 592L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Country", 0, false, "Country", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.SELECTION_EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Legal form", null, 586L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Legal form", 0, false, "Legal form", FormModelFieldNature.INPUT, false, 8, FormModelFieldType.SELECTION_EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "EN", "Language", null, 593L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Language", 0, false, "Language", FormModelFieldNature.INPUT, false, 9, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}

	public static FormModel buildBIL002Clientlist(GrilleService grilleService) {
		FormModel model = new FormModel();
		
		model.setName("BIL002 Client list");
		model.setDataSourceType(DataSourceType.INPUT_GRID);
		model.setDataSourceName("CLI100 Client ID list");
		Long dataSourceId = grilleService.getByName("CLI100 Client ID list").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.setMenu(buildFormModelMenu("Client", "EPB Client", "List clients", "New client"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client name", null, 111L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client name", 0, false, "Client name", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client ID", null, 110L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client ID", 0, false, "Client ID", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Legal form", null, 101L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Legal form", 0, false, "Legal form", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Email", null, 104L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Email", 0, false, "Email", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Street, number and box", null, 105L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Street, number and box", 0, false, "Street, number and box", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Postal code", null, 106L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Postal code", 0, false, "Postal code", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "City", null, 107L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "City", 0, false, "City", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Country", null, 108L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Country", 0, false, "Country", FormModelFieldNature.INPUT, false, 7, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "VAT number", null, 102L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "VAT number", 0, false, "VAT number", FormModelFieldNature.INPUT, false, 8, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client status", null, 151L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client status", 0, false, "Client status", FormModelFieldNature.INPUT, false, 9, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}
	
	public static FormModel buildBIL100Billingevents(GrilleService grilleService) {
		FormModel model = new FormModel();
		
		model.setName("BIL100 Billing events");
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.getAdminFilter().getAttributeFilter().addItem(buildAttributeFilterItem(null, DataSourceType.UNIVERSE, 124L, "Billing event status", DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Draft", null));
		model.getAdminFilter().getAttributeFilter().addItem(buildAttributeFilterItem(null, DataSourceType.UNIVERSE, 145L, "Billing event nature", DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, null, AttributeOperator.NOT_NULL));
		model.getAdminFilter().getAttributeFilter().addItem(buildAttributeFilterItem(null, DataSourceType.UNIVERSE, 125L, "Billing event type", DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, "Invoice", null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Billing event date", null, 33L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing event date", 0, false, "Billing event date", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Billing group 1", null, 133L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing group 1", 0, false, "Billing group 1", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Billing group 2", null, 134L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing group 2", 0, false, "Billing group 2", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Billing driver", null, 50L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing driver", 0, false, "Billing driver", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Unit cost", null, 51L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Unit cost", 0, false, "Unit cost", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "VAT rate", null, 58L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "VAT rate", 0, false, "VAT rate", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Billing event description", null, 123L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing event description", 0, false, "Billing event description", FormModelFieldNature.INPUT, false, 7, FormModelFieldType.SELECTION_EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, "To bill", "Billing event nature", null, 145L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing event nature", 0, false, "Billing event nature", FormModelFieldNature.INPUT, true, 8, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Billing amount", null, 55L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing amount", 0, false, "Billing amount", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.setMenu(buildFormModelMenu("BIL100 Draft billing events", "EPB Draft billing events", "List billing event", "New billing event"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client ID", null, 110L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client ID", 0, false, "Client ID", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.GRID, 110L, null, 934L, true, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 1647L, ReferenceConditionItemType.FIELD, null, null, buildPeriodValue(), 128L)), null, grilleService, "CLI100 Client ID list", null)));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client name", null, 111L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client name", 0, false, "Client name", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Draft", "Billing event status", null, 124L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing event status", 0, false, "Billing event status", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Invoice", "Billing event type", null, 125L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing event type", 0, false, "Billing event type", FormModelFieldNature.INPUT, true, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Invoice pivot", null, 174L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Invoice pivot", 0, false, "Invoice pivot", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.SELECTION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "The 'invoive pivot' field is mandatory. It indicates on which invoice template the billing event will appear.", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Manual billing event", "Billing event category", null, 146L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Billing event category", 0, false, "Billing event category", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}
	
	public static FormModel buildBIL900Orderform(MaterializedGridService materializedGridService, GrilleService grilleService) {
		FormModel model = new FormModel();
		
		model.setName("BIL900 Order form");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("FEE900 Client orders");
		Long dataSourceId = materializedGridService.getByName("FEE900 Client orders").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(3L, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, "Active", "Status", null, 605L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Status", 0, false, "Status", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Invoice pivot", null, 1755L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product package (Invoice pivot)", 0, false, "Invoice pivot", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null,  buildReference(JoinGridType.GRID, null, null, 1600L, true, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 527L, ReferenceConditionItemType.FREE, "Active", null, buildPeriodValue(), null)), materializedGridService, null, "FEE100 EPB Products and services", null)));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Date", null, 606L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Date", 0, false, "Date", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Check", null, 1481L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Check", 5, false, "Check", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Comment", null, 607L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Comment", 0, false, "Comment", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Product Id", null, 603L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product Id", 0, false, "Product Id", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.MATERIALIZED_GRID, null, null, 555L, false, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 556L, ReferenceConditionItemType.FREE, "Active", null, buildPeriodValue(), null), buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 1773L, ReferenceConditionItemType.FIELD, null, null, buildPeriodValue(), 226L)), materializedGridService, null, "PRI001 Pricing grid - membership - yearly fee", null)));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, "Membership - yearly", "Product type", null, 602L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product type", 0, false, "Product type", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.setMenu(buildFormModelMenu("Sales orders", "EPB Sales orders", "List orders", "New order"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(3L, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Order ID", null, 1584L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Order ID", 0, false, "Order ID", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.SEQUENCE,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "Please introduce an Order ID", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client ID", null, 584L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client ID", 0, false, "Client ID", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null,  buildReference(JoinGridType.GRID, null, null, 934L, true, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 938L, ReferenceConditionItemType.FIELD, null, null, buildPeriodValue(), 72L)), null, grilleService, "CLI100 Client ID list", null)));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client name", null,601L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client name", 0, false, "Client name", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null,  buildReference(JoinGridType.GRID, 111L, null, 938L, true, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 947L, ReferenceConditionItemType.FIELD, "Active", null, buildPeriodValue(), null)), null, grilleService, "CLI100 Client ID list", null)));
		return model;
	}

	public static FormModel buildPDT001EPBProductsandServices(MaterializedGridService materializedGridService) {
		FormModel model = new FormModel();
		
		model.setName("PDT001 EPB Products and Services");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("FEE100 EPB Products and services");
		Long dataSourceId = materializedGridService.getByName("FEE100 EPB Products and services").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.setMenu(buildFormModelMenu("EPB Products and services", "EPB Product catalogue", "List package", "New package"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Product Id", null, 525L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product Id", 0, false, "Product Id", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.SELECTION_EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Product name", null, 526L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product name", 0, false, "Product name", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Comment", null, 529L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Comment", 0, false, "Comment", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Check", null, 31L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Check", 0, false, "Check", FormModelFieldNature.INPUT, false, 7, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Entry date", null, 1475L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Entry date", 0, false, "Entry date", FormModelFieldNature.INPUT, true, 8, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Product type", null, 524L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product type", 0, false, "Product type", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.SELECTION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "The product type is mandatory", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Date valid from", null, 528L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Date valid from", 0, false, "Date valid from", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Invoice pivot", null, 1600L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Invoice pivot", 0, false, "Invoice pivot", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.SELECTION_EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.ATTRIBUTE, "The 'Invoice pivot' is a mandatory value", null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Active", "Status", null, 527L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Status", 0, false, "Status", FormModelFieldNature.INPUT, true, 4, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}
	
	public static FormModel buildPRI001Yearlyfee(MaterializedGridService materializedGridService) {
		FormModel model = new FormModel();
		
		model.setName("PRI001 Yearly fee");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("PRI001 Pricing grid - membership - yearly fee");
		Long dataSourceId = materializedGridService.getByName("PRI001 Pricing grid - membership - yearly fee").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.setMenu(buildFormModelMenu("PRICING 1 - Yearly fee", "EPB Pricing grid", "List Membership pricing - yearly fee", "New"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Membership - yearly", "Fee type", null, 549L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Fee type", 0, false, "Fee type", FormModelFieldNature.INPUT, true, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Date", null, 526L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Date", 0, false, "Date", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Monthly rate", null, 553L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Monthly rate", 0, false, "Monthly rate", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Active", "status", null, 556L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "status", 0, false, "status", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Product ID", null, 555L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product ID", 0, false, "Product ID", FormModelFieldNature.INPUT, true, 1, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.MATERIALIZED_GRID, null, null, 525L, false, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 524L, ReferenceConditionItemType.FREE, "Membership - yearly", null, buildPeriodValue(), null)), materializedGridService, null, "PRI001 Pricing grid - membership - yearly fee", null)));		
		return model;
	}
	
	public static FormModel buildPRI002Volumefee(MaterializedGridService materializedGridService) {
		FormModel model = new FormModel();
		
		model.setName("PRI002 Volume fee");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("PRI002 Pricing grid - membership - volume fee");
		Long dataSourceId = materializedGridService.getByName("PRI002 Pricing grid - membership - volume fee").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "To", null, 566L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "To", 0, false, "To", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.MEASURE, null, null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "From", null, 565L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "From", 0, false, "From", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.MEASURE, null, null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Range", null, 564L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Range", 0, false, "Range", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.SELECTION_EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "status", null, 570L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "status", 0, false, "status", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Rate", null, 567L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Rate", 0, false, "Rate", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.MEASURE, null, null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Minimum fee", null, 568L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Minimum fee", 0, false, "Minimum fee", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.MEASURE, null, null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, new BigDecimal(99999999999L), null, "Maximum fee", null, 1511L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Maximum fee", 0, false, "Maximum fee", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.MEASURE, null, null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Date valid from", null, 571L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Date valid from", 0, false, "Date valid from", FormModelFieldNature.INPUT, false, 7, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.setMenu(buildFormModelMenu("PRICING 2 - Volume fee", "EPB Pricing grid", "List volume fee", "New volume fee"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Membership - volume", "Fee type", null, 563L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Fee type", 0, false, "Fee type", FormModelFieldNature.INPUT, true, 0, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Product ID", null, 569L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product ID", 0, false, "Product ID", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.MATERIALIZED_GRID, null, null, 525L, false, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 524L, ReferenceConditionItemType.FREE, "Membership - volume", null, buildPeriodValue(), null)), materializedGridService, null, "FEE100 EPB Products and services", null)));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Invoice pivot", null, 1771L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Invoice pivot", 0, false, "Invoice pivot", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.SELECTION,
				true, true, true, null, null, buildPeriodValue(), null, null));
				
		return model;
	}

	public static FormModel buildPRI003Markupdedicatedfee(MaterializedGridService materializedGridService) {
		FormModel model = new FormModel();
		
		model.setName("PRI003 Markup dedicated fee");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("PRI003 Pricing grid - scheme fee - markup");
		Long dataSourceId = materializedGridService.getByName("PRI003 Pricing grid - scheme fee - markup").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.setMenu(buildFormModelMenu("PRICING 3 - Markup dedicated fee", "EPB Pricing grid", "List markup for dedicated fee", "New markup for dedicated fee"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Product ID", null, 580L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product ID", 0, false, "Product ID", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.MATERIALIZED_GRID, null, null,null, true, 
						null, null, null, null, 45L)));	
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Entry date", null, 579L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Entry date", 0, false, "Entry date", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "status", null, 570L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "status", 0, false, "status", FormModelFieldNature.INPUT, false, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Pricing valid from", null, 1641L, DimensionType.PERIOD,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Pricing valid from", 0, false, "Pricing valid from", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, List.of(buildValidationItem(true, buildPeriodValue(), null, DimensionType.MEASURE, null, null, null, FormModelFieldValidationType.MANDATORY)), buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Rate - 10% markup should be introduced as 0.01", null, 578L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Rate (in decimal)", 0, false, "Rate", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, "Scheme fee - dedicated", "Product type", null, 577L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Product type", 0, false, "Product type", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}
	
	public static FormModel buildSCH001MCIPMLbillingevent(MaterializedGridService materializedGridService, GrilleService grilleService) {
		FormModel model = new FormModel();
		
		model.setName("SCH001 MCI PML billing event");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("STA001 MCI PML Invoices");
		Long dataSourceId = materializedGridService.getByName("STA001 MCI PML Invoices").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter());
		model.setMenu(buildFormModelMenu("MCI PML billing event", "SCHEME Native billing event", "List MCI PML billing event", "New"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Invoice pivot", null, 1645L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Invoice pivot", 0, false, "Invoice pivot", FormModelFieldNature.INPUT, false, 15, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.MATERIALIZED_GRID, null, null, 1613L, true, 
						null, null, null, null, 50L)));	
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Client ID - non native", null, 678L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client ID - non native", 0, false, "Client ID - non native", FormModelFieldNature.INPUT, false, 14, FormModelFieldType.REFERENCE,
				true, true, true, null, null, buildPeriodValue(), null, buildReference(JoinGridType.GRID, 110L, null, 934L, true, 
						List.of(buildCondition(DimensionType.ATTRIBUTE, 0, "AND", null, null, "EQUALS", 947L, ReferenceConditionItemType.FREE, "Active", null, buildPeriodValue(), null)), null, grilleService, "CLI100 Client ID list", null)));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "INVOICE_NUMBER", null, 29L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "INVOICE_NUMBER", 0, false, "INVOICE_NUMBER", FormModelFieldNature.INPUT, true, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "AFFILIATE", null, 43L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "AFFILIATE", 0, false, "AFFILIATE", FormModelFieldNature.INPUT, true, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "EVENT_DESCRIPTION", null, 42L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "EVENT_DESCRIPTION", 0, false, "EVENT_DESCRIPTION", FormModelFieldNature.INPUT, true, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "EVENT_ID", null, 41L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "EVENT_ID", 0, false, "EVENT_ID", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "SERVICE_CODE", null, 36L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "SERVICE_CODE", 0, false, "SERVICE_CODE", FormModelFieldNature.INPUT, true, 1, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "SERVICE_CODE_DESCRIPTION", null, 37L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "SERVICE_CODE_DESCRIPTION", 0, false, "SERVICE_CODE_DESCRIPTION", FormModelFieldNature.INPUT, true, 2, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "UOM", null, 44L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "UOM", 0, false, "UOM", FormModelFieldNature.INPUT, true, 6, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "QUANTITY_AMOUNT", null, 45L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "QUANTITY_AMOUNT", 0, false, "QUANTITY_AMOUNT", FormModelFieldNature.INPUT, false, 7, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "RATE", null, 46L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "RATE", 0, false, "RATE", FormModelFieldNature.INPUT, false, 8, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "CHARGE", null, 47L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "CHARGE", 0, false, "CHARGE", FormModelFieldNature.INPUT, true, 9, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "TOTAL_CHARGE", null, 49L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "TOTAL_CHARGE", 0, false, "TOTAL_CHARGE", FormModelFieldNature.INPUT, true, 10, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "VAT_RATE", null, 53L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "VAT_RATE", 0, false, "VAT_RATE", FormModelFieldNature.INPUT, true, 11, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "VAT_CHARGE", null, 50L, DimensionType.MEASURE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "VAT_CHARGE", 0, false, "VAT_CHARGE", FormModelFieldNature.INPUT, true, 12, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "SBF_EXPLANATORY_TEXT", null, 54L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "SBF_EXPLANATORY_TEXT", 0, false, "SBF_EXPLANATORY_TEXT", FormModelFieldNature.INPUT, true, 13, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}

	public static FormModel buildSCH002ClientIDenrichmentonPMLinvoice(MaterializedGridService materializedGridService) {
		FormModel model = new FormModel();
		
		model.setName("SCH002 Client ID enrichment on PML invoice");
		model.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		model.setDataSourceName("STA001 MCI PML Invoices");
		Long dataSourceId = materializedGridService.getByName("STA001 MCI PML Invoices").getId();
		assertThat(dataSourceId).isNotNull();
		model.setDataSourceId(dataSourceId);
		model.setUserFilter(new UniverseFilter());
		model.setAdminFilter(new UniverseFilter()); 
		model.getAdminFilter().getAttributeFilter().addItem(buildAttributeFilterItem(materializedGridService, DataSourceType.MATERIALIZED_GRID, 54L, "SBF_EXPLANATORY_TEXT", DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, null, AttributeOperator.NOT_NULL));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "BIN - non native", null, 1947L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "BIN - non native", 0, false, "BIN - non native", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "Client ID - non native", null, 678L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Client ID - non native", 0, false, "Client ID - non native", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getDetailsFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.DETAILS, null, null, "SBF_EXPLANATORY_TEXT", null, 54L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "SBF_EXPLANATORY_TEXT", 0, false, "SBF_EXPLANATORY_TEXT", FormModelFieldNature.INPUT, true, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.setMenu(buildFormModelMenu("Allocate Client ID on PLM invoice", "SCHEME Native billing event", "List", "New"));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "INVOICE_NUMBER", null, 29L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "INVOICE_NUMBER", 0, false, "INVOICE_NUMBER", FormModelFieldNature.INPUT, false, 2, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "BILLING_CYCLE_DATE", null, 31L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "BILLING_CYCLE_DATE", 0, false, "BILLING_CYCLE_DATE", FormModelFieldNature.INPUT, false, 3, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "BILLABLE_ICA", null, 34L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "BILLABLE_ICA", 0, false, "BILLABLE_ICA", FormModelFieldNature.INPUT, false, 1, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "EVENT_DESCRIPTION", null, 42L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "EVENT_DESCRIPTION", 0, false, "EVENT_DESCRIPTION", FormModelFieldNature.INPUT, true, 4, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "Invoice pivot", null, 1645L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "Invoice pivot", 0, false, "Invoice pivot", FormModelFieldNature.INPUT, false, 0, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "PML ID", null, 7L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "PML ID", 0, false, "PML ID", FormModelFieldNature.INPUT, false, 5, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		model.getMainFieldListChangeHandler().addNew(buildFormModelField(null, HorizontalAlignment.Center, FormModelFieldCategory.MAIN, null, null, "PML Type", null, 26L, DimensionType.ATTRIBUTE,
				FormModelFieldDuplicationValue.CURRENT_VALUE, 0, buildDimensionFormat(2), "PML Type", 0, false, "PML Type", FormModelFieldNature.INPUT, false, 6, FormModelFieldType.EDITION,
				true, true, true, null, null, buildPeriodValue(), null, null));
		
		return model;
	}
	
	
	public static FormModelMenu buildFormModelMenu(String name, String parent, String listName, String newName) {
		FormModelMenu menu = new FormModelMenu();
		menu.setName(name);
		menu.setParent(parent);
		menu.setListMenuName(listName);
		menu.setNewMenuName(newName);
		return menu;
	}
	
	public static FormModelField buildFormModelField(Long objectId, HorizontalAlignment alignment, FormModelFieldCategory category, BigDecimal defaultDecimalValue, String defaultStringValue, String description, String dimensionFunction, Long dimensionId,
			DimensionType dimensionType, FormModelFieldDuplicationValue duplicationValue, int editorSize, DimensionFormat format, String label, int labelSize, boolean mandatory, String name,
			FormModelFieldNature nature, boolean readOnly, int position, FormModelFieldType type, boolean visible, boolean showLabel, boolean showColumnInBrowser, List<FormModelFieldCalculateItem> calculates, List<FormModelFieldValidationItem> validations,
			PeriodValue periodValue, List<FormModelFieldConcatenateItem> concatenates, FormModelFieldReference reference) {
		FormModelField field = new FormModelField();
		
		field.setAlignment(alignment);
		field.setObjectId(objectId);
		field.setCategory(category);
		field.setDescription(description);
		field.setDimensionFunction(dimensionFunction);
		field.setDimensionId(dimensionId);
		field.setDimensionType(dimensionType);
		field.setDuplicationValue(duplicationValue);
		field.setEditorSize(editorSize);
		field.setFormat(format);
		field.setLabel(label);
		field.setLabelSize(labelSize);
		field.setMandatory(mandatory);
		field.setName(name);
		field.setDefaultDecimalValue(defaultDecimalValue);
		field.setDefaultStringValue(defaultStringValue);
		field.setNature(nature);
		field.setReadOnly(readOnly);
		field.setPosition(position);
		field.setType(type);
		field.setVisible(visible);
		field.setShowLabel(showLabel);
		field.setDefaultDateValue(periodValue);
		field.setShowColumnInBrowser(showColumnInBrowser);
		
		if(reference != null) {
			field.setReference(reference);
		}
		
		if(calculates != null) {
			for (var item : calculates) {
				field.getCalculateItemListChangeHandler().addNew(item);
			}
		}
		
		if(concatenates != null) {
			for (var item : concatenates) {
				field.getConcatenateItemListChangeHandler().addNew(item);
			}
		}

		if(validations != null) {
			for (var item : validations) {
				field.getValidationItemListChangeHandler().addNew(item);
			}
		}
		
		return field;
	}
	
	public static FormModelFieldReference buildReference(JoinGridType dataSourceType, Long dimensionId, String formula, Long sourceId,
			boolean uniqueValue, List<FormModelFieldReferenceCondition> conditions, MaterializedGridService materializedGridService, GrilleService grilleService, String dataSourceName, Long dataSourceId) {
		FormModelFieldReference reference = new FormModelFieldReference();
		Long dataSourceId_ = null;
		if(dataSourceId != null) {
			dataSourceId_ = dataSourceId;
		}
		else {
			if(materializedGridService != null) {
				dataSourceId_ = materializedGridService.getByName(dataSourceName).getId();
			}
			
			if(grilleService != null) {
				dataSourceId_ = grilleService.getByName(dataSourceName).getId();
			}
		}
		assertThat(dataSourceId_).isNotNull();
		reference.setDataSourceId(dataSourceId_);
		reference.setDataSourceType(dataSourceType);
		reference.setDimensionId(dimensionId);
		reference.setFormula(formula);
		reference.setSourceId(sourceId);
		reference.setUniqueValue(uniqueValue);
		
		if(conditions != null) {
			for (var item : conditions) {
				reference.getConditionListChangeHandler().addNew(item);
			}
		}
		
		return reference;
	}
	
	public static FormModelFieldReferenceCondition buildCondition(DimensionType parameterType, int position, String verb, String openingBracket, String closingBracket, String comparator,
			Long keyId, ReferenceConditionItemType conditionItemType, String stringValue, BigDecimal decimalValue, PeriodValue periodValue, Long fieldId) {
		FormModelFieldReferenceCondition condition = new FormModelFieldReferenceCondition();
		
		condition.setParameterType(parameterType);
		condition.setPosition(position);
		condition.setVerb(verb);
		condition.setOpeningBracket(openingBracket);
		condition.setClosingBracket(closingBracket);
		condition.setComparator(comparator);
		condition.setKeyId(keyId);
		condition.setConditionItemType(conditionItemType);
		condition.setStringValue(stringValue);
		condition.setDecimalValue(decimalValue);
		condition.setPeriodValue(periodValue);
		condition.setFieldId(fieldId);
		return condition;
	}
	
	public static DimensionFormat buildDimensionFormat(int nbr) {
		DimensionFormat format = new DimensionFormat();
		format.setNbrOfDecimal(nbr);
		return format;
	}
	
	public static PeriodValue buildPeriodValue() {
		PeriodValue periodValue = new PeriodValue();
		return periodValue;
	}
	
	public static FormModelFieldValidationItem buildValidationItem(boolean active, PeriodValue periodValue, BigDecimal decimalValue, DimensionType dimensionType, String errorMessage,
			Integer integerValue, String stringValue, FormModelFieldValidationType type) {
		FormModelFieldValidationItem validationItem = new FormModelFieldValidationItem();
		
		validationItem.setActive(active);
		validationItem.setType(type);
		validationItem.setDateValue(periodValue);
		validationItem.setDecimalValue(decimalValue);
		validationItem.setDimensionType(dimensionType);
		validationItem.setErrorMessage(errorMessage);
		validationItem.setIntegerValue(integerValue);
		validationItem.setStringValue(stringValue);
		return validationItem;
	}
	
	public static AttributeFilterItem buildAttributeFilterItem(MaterializedGridService materializedGridService, DataSourceType dataSourceType, Long dimensionId, String dimensionName, DimensionType dimensionType,
			FilterVerb filterVerb, FilterItemType itemType, Boolean useLink, int position, String variable, String value, AttributeOperator operator) {
		AttributeFilterItem item = new AttributeFilterItem();
		
		if(materializedGridService != null) {
			Long dataSourceId_ = materializedGridService.getByName("MAP200 Product ID vs Scheme platform").getId();
			assertThat(dataSourceId_).isNotNull();
			item.setDataSourceId(dataSourceId_);
		}
		item.setDataSourceType(dataSourceType);
		item.setDimensionId(dimensionId);
		item.setDimensionName(dimensionName);
		item.setDimensionType(dimensionType);
		item.setFilterVerb(filterVerb);
		item.setItemType(itemType);
		item.setUseLink(useLink);
		item.setPosition(position);
		item.setVariables(variable);
		item.setValue(value);
		item.setOperator(operator);
		return item;
	}
}
