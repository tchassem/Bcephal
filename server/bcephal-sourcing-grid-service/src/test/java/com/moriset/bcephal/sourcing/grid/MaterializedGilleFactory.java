package com.moriset.bcephal.sourcing.grid;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodGrouping;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;

public class MaterializedGilleFactory {
	
	
	public static List<MaterializedGrid> buildMaterializedGrilles() throws Exception{
		List<MaterializedGrid> items = new ArrayList<>();
		items.add(buildBillingCompanyRepository());
		items.add(buildINP001MCIPMLInvoices());
		items.add(buildINP002MCIAffiliateInvoice());
		items.add(buildLOG100ReconciliationLog());
		return items;		
	}
	
	public static MaterializedGrid buildBillingCompanyRepository( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("Billing company repository");
		materialized.setAllowLineCounting(true);
		materialized.setCategory(GrilleCategory.SYSTEM);
		materialized.setCreationDate(Timestamp.valueOf("2023-04-28 14:29:32"));
		materialized.setModificationDate(Timestamp.valueOf("2023-04-28 14:29:32"));
		materialized.setPublished(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_ROLE_ID, 0, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Name", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_ROLE_NAME, 1, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("VAT numberVAT number", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_VAT_NBR, 2, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Legal form", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_LEGAL_FORM, 3, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Phone", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_PHONE, 4, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Email", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_EMAIL, 5, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Street, number and box", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_STREET, 6, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Postal code", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_POSTAL_CODE, 7, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("City", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_CITY, 8, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Country", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_COUNTRY, 9, PeriodGrouping.DAY_OF_MONTH, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Language", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.BILLING_LANGUAGE, 10, PeriodGrouping.DAY_OF_MONTH, true));
		
		return materialized;

	}
	
	public static MaterializedGrid buildINP001MCIPMLInvoices( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("INP001 MCI PML Invoices");
		materialized.setAllowLineCounting(false);
		materialized.setCategory(GrilleCategory.USER);
		materialized.setCreationDate(Timestamp.valueOf("2023-04-15 05:35:51"));
		materialized.setModificationDate(Timestamp.valueOf("2023-04-15 05:35:51"));
		materialized.setPublished(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("SI01", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 54, PeriodGrouping.DAY_OF_WEEK, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Invoice date", DimensionType.PERIOD, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 0, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Invoice number", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 1, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 2, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event name", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 3, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event category ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 4, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event category name", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 5, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 6, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Postal code", DimensionType.PERIOD, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 7, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Scheme", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 8, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Brand", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 9, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Driver", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 10, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Amount exl. Tax", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 12, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("D-C", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 13, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Tax rate", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 14, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Rate", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 11, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Tax amount", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 15, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("VAT rate", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 16, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("VAT amount", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 17, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Amount incl. tax", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 18, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Year", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 19, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Month", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 20, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Entity ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 21, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 22, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Currency0", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 23, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Check", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 24, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 25, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Invoice type", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 26, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("DOCUMENT_TYPE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 27, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("INVOICE_NUMBER", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 28, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("CURRENCY", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 29, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("BILLING_CYCLE_DATE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 30, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("INVOICE_ICA", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 31, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("ACTIVITY_ICA", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 32, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("BILLABLE_ICA", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 33, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("COLLECTION_METHOD", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 34, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("SERVICE_CODE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 35, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("SERVICE_CODE_DESCRIPTION", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 36, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("PERIOD_START_DATE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 37, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("PERIOD_END_DATE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 38, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("ORIGINAL_INVOICE_NUMBER", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 39, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("EVENT_ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 40, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("EVENT_DESCRIPTION", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 41, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("AFFILIATE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 42, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("UOM", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 43, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("QUANTITY_AMOUNT", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 44, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("RATE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 45, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("CHARGE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 46, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("TAX_CHARGE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 47, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("TOTAL_CHARGE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 48, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("VAT_CHARGE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 49, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("VAT_CURRENCY", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 50, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("VAT_CODE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 51, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("VAT_RATE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 52, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("SBF_EXPLANATORY_TEXT", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 53, PeriodGrouping.DAY_OF_MONTH, false));
		
		return materialized;

	}
	
	public static MaterializedGrid buildINP002MCIAffiliateInvoice( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("INP002 MCI affiliate invoice");
		materialized.setAllowLineCounting(false);
		materialized.setCategory(GrilleCategory.USER);
		materialized.setCreationDate(Timestamp.valueOf("2023-04-15 05:59:31"));
		materialized.setModificationDate(Timestamp.valueOf("2023-04-15 05:59:31"));
		materialized.setPublished(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("SI01", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 41, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event name", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 3, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 2, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Currency0", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 20, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event category ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 4, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 19, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Entity ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 18, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Month", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 17, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Year", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 16, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("D-C", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 15, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Amount exl. Tax", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 14, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Rate0", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 13, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Driver", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 12, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Brand", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 11, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Broker ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 8, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Member bank ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 7, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 6, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event category name", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 5, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Billing event category ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 4, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("TOTAL_CHARGE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 40, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("RATE", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 39, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("QUANTITY_OR_AMOUNT", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 38, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("CARD_ACCEPTOR_ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 37, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("EVENT_DESCRIPTION", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 36, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("EVENT", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 35, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("SERVICE_DESCRIPTION", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 34, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("SERVICE_CODE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 33, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("AFFILIATE_TYPE", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 32, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("CURRENCY", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 31, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("COLLECTION_METHOD", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 30, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("AFFILIATE_CUSTOMER_NAME", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 29, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("AFFILIATE_CUSTOMER_ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 28, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("ACTIVITY_CUSTOMER_NAME", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 27, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("ACTIVITY_CUSTOMER_ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 26, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("INVOICE_CUSTOMER_NAME", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 25, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("INVOICE_CUSTOMER_ID", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 24, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Invoice type", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 23, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 22, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Entry date", DimensionType.PERIOD, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 9, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("invoice date", DimensionType.PERIOD, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 0, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Invoice number", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 1, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Check", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 21, PeriodGrouping.DAY_OF_MONTH, false));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("Scheme", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 10, PeriodGrouping.DAY_OF_MONTH, false));
		return materialized;

	}
	
	public static MaterializedGrid buildLOG100ReconciliationLog( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("LOG100 Reconciliation log");
		materialized.setAllowLineCounting(false);
		materialized.setCategory(GrilleCategory.USER);
		materialized.setCreationDate(Timestamp.valueOf("2023-04-26 15:25:26"));
		materialized.setModificationDate(Timestamp.valueOf("2023-04-26 15:25:26"));
		materialized.setPublished(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("scheme cycle", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 12, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("scheme platform id", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 11, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco action", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_ACTION, 10, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco type", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_TYPE, 1, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 7, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco date", DimensionType.PERIOD, HorizontalAlignment.Center, GrilleColumnCategory.RECO_DATE, 6, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco nbr", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_NBR, 3, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco partial type", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_PARTIAL_TYPE, 2, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco am", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_AM, 9, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco filter", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_FILTER, 0, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("scheme id", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.NONE, 8, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco amount", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_AMOUNT, 5, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco partial  nbr", DimensionType.ATTRIBUTE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_PARTIAL_NBR, 4, PeriodGrouping.DAY_OF_WEEK, true));
		materialized.getColumnListChangeHandler()
		.addNew(buildColumn("reco write off", DimensionType.MEASURE, HorizontalAlignment.Center, GrilleColumnCategory.RECO_WRITEOFF, 13, PeriodGrouping.DAY_OF_WEEK, true));
		
		return materialized;

	}


	private static MaterializedGridColumn buildColumn(String name, DimensionType type, HorizontalAlignment alignment,GrilleColumnCategory role, int position,PeriodGrouping groupBy, boolean editable) {
		MaterializedGridColumn column = new MaterializedGridColumn();
		column.setName(name);
		column.setType(type);
		column.setAlignment(alignment);
		column.setPosition(position);
		column.setPublished(true);
		column.setMandatory(false);
		column.setGroupBy(groupBy);
		return column;
	}

}
