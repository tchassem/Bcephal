package com.moriset.bcephal.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;

public class MaterializedGilleFactory {
	
	
	public static List<MaterializedGrid> buildMaterializedGrilles() throws Exception{
		List<MaterializedGrid> items = new ArrayList<>();
		items.add(buildBIL010MAVolumePerClientPerInvoicePivot());
		items.add(buildBIL310SchemeFeeDedicatedFeeNotAllocatedToABillableMember());
		items.add(buildEOM001EndOfMonthInput());
		items.add(buildFEE100EPBProductsAndServices());
		items.add(buildFFEE310ServiceCodeForQMRFees());
		items.add(buildFEE900ClientOrders());
		items.add(buildLOG100Reconciliationlog());
		items.add(buildMAP100ClientIDVsMemberD());
		items.add(buildMAP110MemberBankIDVsMemberBankName());
		items.add(buildMAP120MemberBankIDVsBrokerID());
		items.add(buildMAP180RECMCI());
		items.add(buildMAP200ProductIDVsSchemePlatform());
		items.add(buildMAP210SchemeCycleVsSchemePlatformID());
		items.add(buildMAP220CounterpartAccountNVsSchemePlatformID());
		items.add(buildMAP230ProcessorDateVsValueDate());
		items.add(buildMAP300EOMMeasureIDVsMeasureName());
		items.add(buildMAP310RECAccountIDVsRECNameAndRECType());
		items.add(buildMAP900MCIAffiliateIDVsEPBMemberID());
		items.add(buildMAP910AffiliateInMastercardInvoicesVsClientID());
		items.add(buildMAP920InvoicePivotPerPMLID());
		items.add(buildMAP930RECAccountVsRECType());
		items.add(buildMAP940AffiliateInvoiceCalendar( ));
		items.add(buildMAP950CalendarMonthMappingForMCIInvoice());
		items.add(buildPRI001PricingGridMembershipYearlyFee());
		items.add(buildPRI002PricingGridMembershipVolumeFee());
		items.add(buildPRI003PricingGridSchemeFeeMarkup());
		items.add(buildPSYS001BillingCompanyRepository());
		items.add(buildSTA001MCIPMLInvoices());
		items.add(buildSTA002MCIAffiliateInvoice());
		items.add(buildSTA100M109Issuing());
		items.add(buildSTA110M109Acquiring());
		
		return items;		
	}
	
	public static MaterializedGrid buildBIL010MAVolumePerClientPerInvoicePivot( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("BIL010 MA volume per client per invoice pivot");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Client ID", DimensionType.ATTRIBUTE, "Client ID", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Posting amount", DimensionType.MEASURE, "Posting amount", 2 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Posting amount total", DimensionType.MEASURE,
				"Posting amount total", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Check", DimensionType.MEASURE, "Check new", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, "PML type", 6 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Reference month start date", DimensionType.PERIOD,
				"Reference month start date", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 9 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 10 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 11 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 12 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 13 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product type", DimensionType.ATTRIBUTE, "Product type", 14 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product Id", DimensionType.ATTRIBUTE, "Product ID", 15 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Scheme product ID", DimensionType.ATTRIBUTE, "Scheme product ID", 16 ));

		return materialized;

	}

	public static MaterializedGrid buildBIL310SchemeFeeDedicatedFeeNotAllocatedToABillableMember(
			 ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("BIL310 Scheme fee - dedicated fee not allocated to a billable member");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Member bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme", DimensionType.ATTRIBUTE, "Scheme", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Check", DimensionType.MEASURE, "Check new", 9 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("billing event date", DimensionType.PERIOD, "Billing event date", 10 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Scheme invoice net amount", DimensionType.MEASURE,
				"Scheme invoice net amount", 11 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Data Source", DimensionType.ATTRIBUTE, "Data source", 12 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Month", DimensionType.ATTRIBUTE, "Month", 13 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Year", DimensionType.ATTRIBUTE, "Year", 14 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Client ID", DimensionType.ATTRIBUTE, "Client ID", 15 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event status", DimensionType.ATTRIBUTE,
				"Billing event status", 16 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event nature", DimensionType.ATTRIBUTE,
				"Billing event nature", 17 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event category", DimensionType.ATTRIBUTE,
				"Billing event category", 18 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event description",
				DimensionType.ATTRIBUTE, "Billing event description", 19 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event type", DimensionType.ATTRIBUTE,
				"Billing event type", 20 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Billing group 1", DimensionType.ATTRIBUTE, "Billing group 1", 21 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Billing group 2", DimensionType.ATTRIBUTE, "Billing group 2", 22 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Billing amount", DimensionType.MEASURE, "Billing amount", 23 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D-C", 24 ));

		return materialized;

	}

	public static MaterializedGrid buildEOM001EndOfMonthInput( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("EOM001 End of month input");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Start date", DimensionType.PERIOD, "Start date", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("End date", DimensionType.PERIOD, "End date", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML", DimensionType.ATTRIBUTE, "PML", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Measure ID", DimensionType.ATTRIBUTE, "Measure ID", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Measure Name", DimensionType.ATTRIBUTE, "Measure Name", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Amount", DimensionType.MEASURE, "Amount", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Sign", DimensionType.ATTRIBUTE, "Sign", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Net amount", DimensionType.MEASURE, "Net amount", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Remark", DimensionType.ATTRIBUTE, "Remark", 9 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme", DimensionType.ATTRIBUTE, "Scheme", 10 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Scheme platform", DimensionType.ATTRIBUTE, "Scheme Platform ID", 11 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Status", 12 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Check", DimensionType.MEASURE, "Check new", 13 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Data source", DimensionType.ATTRIBUTE, "Data source", 14 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 15 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Bank account N°", DimensionType.ATTRIBUTE, "Bank account N°", 16 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 17 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 18 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 19 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 20 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 21 ));

		return materialized;

	}

	public static MaterializedGrid buildFEE100EPBProductsAndServices( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("FEE100 EPB Products and services");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product type", DimensionType.ATTRIBUTE, "Product type", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, "Product ID", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product name", DimensionType.ATTRIBUTE, "Product name", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Status", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Date valid from", DimensionType.PERIOD, "Date valid from", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Comment", DimensionType.ATTRIBUTE, "Comment", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Check", DimensionType.MEASURE, "Check new", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 8 ));
		return materialized;

	}

	public static MaterializedGrid buildFFEE310ServiceCodeForQMRFees( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("FEE310 Service code for QMR fees");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("SERVICE CODE", DimensionType.ATTRIBUTE, "SERVICE CODE", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Fee type", DimensionType.ATTRIBUTE, "Fee type", 1 ));
		return materialized;

	}

	public static MaterializedGrid buildFEE900ClientOrders( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("FEE900 Client orders");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Client ID", DimensionType.ATTRIBUTE, "Client ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Client name", DimensionType.ATTRIBUTE, "Client name", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product type", DimensionType.ATTRIBUTE, "Product type", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product Id", DimensionType.ATTRIBUTE, "Product ID", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product name", DimensionType.ATTRIBUTE, "Product name", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Status", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Valid since", DimensionType.PERIOD, "Valid since", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Comment", DimensionType.ATTRIBUTE, "Comment", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Check", DimensionType.MEASURE, "Check new", 9 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Order ID", DimensionType.ATTRIBUTE, "Order ID", 10 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Billing template", DimensionType.ATTRIBUTE, "Billing template", 11 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Invoice template", DimensionType.ATTRIBUTE, "Invoice template", 12 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 13 ));
		return materialized;

	}

	public static MaterializedGrid buildLOG100Reconciliationlog( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("LOG100 Reconciliation log");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco filter", DimensionType.ATTRIBUTE, "reco filter", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco type", DimensionType.ATTRIBUTE, "reco type", 1 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("reco partial type", DimensionType.ATTRIBUTE, "reco partial type", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco nbr", DimensionType.ATTRIBUTE, "reco nbr", 3 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("reco partial nbr", DimensionType.ATTRIBUTE, "reco partial nbr", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco amount", DimensionType.MEASURE, "reco amount", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco date", DimensionType.PERIOD, "reco date", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, "PML type", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("scheme id", DimensionType.ATTRIBUTE, "Scheme ID", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco am", DimensionType.ATTRIBUTE, "reco am", 9 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco action", DimensionType.ATTRIBUTE, "reco action", 10 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("scheme platform id", DimensionType.ATTRIBUTE,
				"Scheme Platform ID", 11 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("scheme cycle", DimensionType.ATTRIBUTE, "scheme cycle", 12 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("reco write off", DimensionType.MEASURE, "reco write off", 13 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP110MemberBankIDVsMemberBankName( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP110 Member Bank ID vs Member Bank Name");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 0 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member bank name", DimensionType.ATTRIBUTE, "Member bank name", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 6 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP120MemberBankIDVsBrokerID( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("MAP120 Member Bank ID vs Broker ID");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Broker ID", DimensionType.ATTRIBUTE, "Broker ID", 1 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Broker ID 6 Position", DimensionType.ATTRIBUTE,
				"Broker ID 6 Position", 2 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP180RECMCI( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP180 REC MCI");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Native account ID", DimensionType.ATTRIBUTE, "Native account ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Target type ID", DimensionType.ATTRIBUTE, "Target type ID", 1 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Target Account ID", DimensionType.ATTRIBUTE, "Target Account ID", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 7 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP200ProductIDVsSchemePlatform( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP200 Product ID vs Scheme platform");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, "PML type", 0 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Scheme Platform ID", DimensionType.ATTRIBUTE, "Scheme Platform ID", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, "Product ID", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Data source", DimensionType.ATTRIBUTE, "Data source", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Pricing status", DimensionType.ATTRIBUTE, "Pricing status", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 9 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP210SchemeCycleVsSchemePlatformID( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP210 Scheme cycle vs Scheme Platform ID");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme Cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 1 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Scheme Platform ID", DimensionType.ATTRIBUTE,
				"Scheme Platform ID", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 7 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP220CounterpartAccountNVsSchemePlatformID(
			 ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP220 Counterpart Account N° vs Scheme Platform ID");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler().addNew(buildColumn("Counterpart Account Nbr", DimensionType.ATTRIBUTE,
				"Counterpart Account Nbr", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 1 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Scheme Platform ID", DimensionType.ATTRIBUTE, "Scheme Platform ID", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 8 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP230ProcessorDateVsValueDate( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP230 Processor Date vs Value Date");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Processor Date", DimensionType.PERIOD, "Processor date", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Value Date", DimensionType.PERIOD, "Value date", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme Cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 7 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP300EOMMeasureIDVsMeasureName( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP300 EOM measure ID vs measure name");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ID", DimensionType.ATTRIBUTE, "ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Name", DimensionType.ATTRIBUTE, "Name", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 6 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP310RECAccountIDVsRECNameAndRECType( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP310 REC Account ID vs REC name and REC type");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("REC Account ID", DimensionType.ATTRIBUTE, "REC Account ID", 0 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("REC Account Name", DimensionType.ATTRIBUTE, "REC Account name", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Rec Type ID", DimensionType.ATTRIBUTE, "REC Type ID", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 7 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP900MCIAffiliateIDVsEPBMemberID( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP900 MCI affiliate ID vs EPB member ID");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler().addNew(buildColumn("AFFILIATE_CUSTOMER_ID", DimensionType.ATTRIBUTE,
				"AFFILIATE_CUSTOMER_ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("EPB Member ID", DimensionType.ATTRIBUTE, "EPB Member ID", 1 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("EPM Member Name", DimensionType.ATTRIBUTE, "EPM Member Name", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Status", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 8 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP910AffiliateInMastercardInvoicesVsClientID(
			 ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("MAP910 Affiliate in Mastercard invoices vs Client ID");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler().addNew(buildColumn("AFFILIATE_CUSTOMER_ID", DimensionType.ATTRIBUTE,
				"AFFILIATE_CUSTOMER_ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("EPB Member ID", DimensionType.ATTRIBUTE, "EPB Member ID", 1 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("EPB Member name", DimensionType.ATTRIBUTE, "EPB Member name", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 7 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP930RECAccountVsRECType( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("MAP930 REC Account vs REC Type");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("REC Type Name", DimensionType.ATTRIBUTE, "REC Type Name", 0 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("REC Account Name", DimensionType.ATTRIBUTE, "REC Account name", 1 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP920InvoicePivotPerPMLID( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("MAP920 Invoice pivot per PML ID");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 1 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP940AffiliateInvoiceCalendar( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("MAP940 Affiliate invoice calendar");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Year", DimensionType.ATTRIBUTE, "Year", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Month", DimensionType.ATTRIBUTE, "Month", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice date", DimensionType.PERIOD, "Invoice date", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 7 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP950CalendarMonthMappingForMCIInvoice( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("MAP950 Calendar month mapping for MCI invoice");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler().addNew(buildColumn("Month 3 letter acronymn", DimensionType.ATTRIBUTE,
				"Month 3 letter acronymn", 0 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Month 2 figures acronymns",
				DimensionType.ATTRIBUTE, "Month 2 figures acronymns", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Year", DimensionType.ATTRIBUTE, "Year", 2 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("LStart date month", DimensionType.PERIOD, "Start date month", 3 ));
		return materialized;

	}

	public static MaterializedGrid buildMAP100ClientIDVsMemberD( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("MAP100 Client ID vs Member ID");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Client ID", DimensionType.ATTRIBUTE, "Client ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Member bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Status", 2 ));
		return materialized;

	}

	public static MaterializedGrid buildPRI001PricingGridMembershipYearlyFee( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("PRI001 Pricing grid - membership - yearly fee");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product type", DimensionType.ATTRIBUTE, "Product type", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, "Product ID", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Monthly rate", DimensionType.MEASURE, "Monthly rate", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Minimum fee", DimensionType.MEASURE, "Minimum fee", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Maximum fee", DimensionType.MEASURE, "Maximum fee", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("status", DimensionType.ATTRIBUTE, "Status", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Valid from", DimensionType.PERIOD, "Valid from", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Valid from", DimensionType.PERIOD, "Valid from", 9 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product name", DimensionType.ATTRIBUTE, "Product name", 10 ));
		return materialized;

	}

	public static MaterializedGrid buildPRI002PricingGridMembershipVolumeFee( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("PRI002 Pricing grid - membership - volume fee");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, "Product ID", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Range", DimensionType.ATTRIBUTE, "Range", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("From", DimensionType.MEASURE, "From", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("To", DimensionType.MEASURE, "To", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Rate", DimensionType.MEASURE, "Rate", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Minimum fee", DimensionType.MEASURE, "Minimum fee", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Maximum fee", DimensionType.MEASURE, "Maximum fee", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("status", DimensionType.ATTRIBUTE, "Status", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Date valid from", DimensionType.PERIOD, "Date 	valid from", 9 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Fee amount before range (progressive tier)",
				DimensionType.MEASURE, "Fee amount before range (progressive tier)", 10 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 11 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product type", DimensionType.ATTRIBUTE, "Product type", 12 ));
		return materialized;

	}

	public static MaterializedGrid buildPRI003PricingGridSchemeFeeMarkup( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("PRI003 Pricing grid - scheme fee - markup");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product type", DimensionType.ATTRIBUTE, "Product type", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product Id", DimensionType.ATTRIBUTE, "Product ID", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Rate", DimensionType.MEASURE, "Rate", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("status", DimensionType.ATTRIBUTE, "Status", 3 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Billing event ID", DimensionType.ATTRIBUTE, "Billing event ID", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 5 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Pricing valid from", DimensionType.PERIOD, "Pricing valid from", 6 ));
		return materialized;

	}

	public static MaterializedGrid buildPSYS001BillingCompanyRepository( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("SYS001 Billing company repository");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ID", DimensionType.ATTRIBUTE, "ID", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Name", DimensionType.ATTRIBUTE, "Name", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("VAT numbe", DimensionType.ATTRIBUTE, "VAT numbe", 2 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Legal form", DimensionType.ATTRIBUTE, "Legal form", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Phone", DimensionType.ATTRIBUTE, "Phone", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Email", DimensionType.ATTRIBUTE, "Email", 5 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Street, number and box", DimensionType.ATTRIBUTE,
				"Street, number and box", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Postal code", DimensionType.ATTRIBUTE, "Postal code", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("City", DimensionType.ATTRIBUTE, "City", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Country", DimensionType.ATTRIBUTE, "Country", 9 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Language", DimensionType.ATTRIBUTE, "Language", 10 ));
		return materialized;

	}

	public static MaterializedGrid buildSTA001MCIPMLInvoices( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("STA001 MCI PML Invoices");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("DOCUMENT_TYPE", DimensionType.ATTRIBUTE, "DOCUMENT_TYPE", 0 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("INVOICE_NUMBER", DimensionType.ATTRIBUTE, "INVOICE_NUMBER", 1 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("CURRENCY", DimensionType.ATTRIBUTE, "CURRENCY", 2 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("BILLING_CYCLE_DATE", DimensionType.ATTRIBUTE, "BILLING_CYCLE_DATE", 3 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("INVOICE_ICA", DimensionType.ATTRIBUTE, "INVOICE_ICA", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ACTIVITY_ICA", DimensionType.ATTRIBUTE, "ACTIVITY_ICA", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("BILLABLE_ICA", DimensionType.ATTRIBUTE, "BILLABLE_ICA", 6 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("COLLECTION_METHOD", DimensionType.ATTRIBUTE, "COLLECTION_METHOD", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("SERVICE_CODE", DimensionType.ATTRIBUTE, "SERVICE_CODE", 8 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("SERVICE_CODE_DESCRIPTION",
				DimensionType.ATTRIBUTE, "SERVICE_CODE_DESCRIPTION", 9 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("PERIOD_START_DATE", DimensionType.ATTRIBUTE, "PERIOD_START_DATE", 10 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("PERIOD_END_DATE", DimensionType.ATTRIBUTE, "PERIOD_END_DATE", 11 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("ORIGINAL_INVOICE_NUMBER", DimensionType.ATTRIBUTE,
				"ORIGINAL_INVOICE_NUMBER", 12 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("EVENT_ID", DimensionType.ATTRIBUTE, "EVENT_ID", 13 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("EVENT_DESCRIPTION", DimensionType.ATTRIBUTE, "EVENT_DESCRIPTION", 14 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("AFFILIATE", DimensionType.ATTRIBUTE, "AFFILIATE", 15 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("UOM", DimensionType.ATTRIBUTE, "UOM", 16 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("QUANTITY_AMOUNT", DimensionType.MEASURE, "QUANTITY_AMOUNT", 17 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("RATE", DimensionType.MEASURE, "RATE", 18 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("CHARGE", DimensionType.MEASURE, "CHARGE", 19 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("TAX_CHARGE", DimensionType.MEASURE, "TAX_CHARGE", 20 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("TOTAL_CHARGE", DimensionType.MEASURE, "TOTAL_CHARGE", 21 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("VAT_CHARGE", DimensionType.MEASURE, "VAT_CHARGE", 22 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("VAT_CURRENCY", DimensionType.ATTRIBUTE, "VAT_CURRENCY", 23 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("VAT_CODE", DimensionType.ATTRIBUTE, "VAT_CODE", 24 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("VAT_RATE", DimensionType.MEASURE, "VAT_RATE", 25 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("SBF_EXPLANATORY_TEXT", DimensionType.ATTRIBUTE,
				"SBF_EXPLANATORY_TEXT", 26 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Invoice number", DimensionType.ATTRIBUTE, "Invoice number", 27 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Billing event ID", DimensionType.ATTRIBUTE, "Billing event ID", 28 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event name", DimensionType.ATTRIBUTE,
				"Billing event name", 29 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event category ID",
				DimensionType.ATTRIBUTE, "Billing event category ID", 30 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event category name",
				DimensionType.ATTRIBUTE, "Billing event category name", 31 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 32 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 33 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 34 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Brand", DimensionType.ATTRIBUTE, "Brand", 35 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Driver", DimensionType.MEASURE, "Driver", 36 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Rate", DimensionType.MEASURE, "Rate", 37 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount exl. Tax", DimensionType.MEASURE, "Amount exl. Tax", 38 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D-C", 39 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Tax rate", DimensionType.MEASURE, "Tax rate", 40 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Tax amount", DimensionType.MEASURE, "Tax amount", 41 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("VAT rate", DimensionType.MEASURE, "VAT rate", 42 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("VAT amount", DimensionType.MEASURE, "VAT amount", 43 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount incl. tax", DimensionType.MEASURE, "Amount incl. tax", 44 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Year", DimensionType.ATTRIBUTE, "Year", 45 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Month", DimensionType.ATTRIBUTE, "Month", 46 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entity ID", DimensionType.ATTRIBUTE, "Entity ID", 47 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, "Product ID", 48 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Currency0", DimensionType.ATTRIBUTE, "Currency0", 49 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Check", DimensionType.MEASURE, "Check new", 50 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 51 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice type", DimensionType.ATTRIBUTE, "Invoice type", 52 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("SI01", DimensionType.ATTRIBUTE, "SI01", 53 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 54 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 55 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 56 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 57 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 58 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Client ID - non native", DimensionType.ATTRIBUTE,
				"Client ID - non native", 59 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Fee type", DimensionType.ATTRIBUTE, "Fee type", 60 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("SYS001 Quarterly fee", DimensionType.ATTRIBUTE,
				"SYS001 Quarterly fee", 61 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("SYS002 JOIN BILL400 treatment date",
				DimensionType.PERIOD, "SYS002 JOIN BILL400 treatment date", 62 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("SYS003 JOIN BILL500 treatment date",
				DimensionType.PERIOD, "SYS003 JOIN BILL500 treatment date", 63 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 64 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("BIN - non native", DimensionType.ATTRIBUTE, "BIN - non native", 65 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Temp month - step 2", DimensionType.ATTRIBUTE,
				"Temp month - step 2", 66 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Temp month", DimensionType.ATTRIBUTE, "Temp month", 67 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Temp day", DimensionType.ATTRIBUTE, "Temp day", 68 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Scheme invoice date", DimensionType.PERIOD, "Scheme invoice date", 69 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Operation code", DimensionType.ATTRIBUTE, "Operation code", 70 ));
		return materialized;

	}

	public static MaterializedGrid buildSTA002MCIAffiliateInvoice( ) {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(true);
		materialized.setName("STA002 MCI affiliate invoice");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler().addNew(buildColumn("INVOICE_CUSTOMER_ID", DimensionType.ATTRIBUTE,
				"INVOICE_CUSTOMER_ID", 0 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("INVOICE_CUSTOMER_NAME", DimensionType.ATTRIBUTE,
				"INVOICE_CUSTOMER_NAME", 1 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("ACTIVITY_CUSTOMER_ID", DimensionType.ATTRIBUTE,
				"ACTIVITY_CUSTOMER_ID", 2 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("ACTIVITY_CUSTOMER_NAME", DimensionType.ATTRIBUTE,
				"ACTIVITY_CUSTOMER_NAME", 3 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("AFFILIATE_CUSTOMER_ID", DimensionType.ATTRIBUTE,
				"AFFILIATE_CUSTOMER_ID", 4 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("AFFILIATE_CUSTOMER_NAME", DimensionType.ATTRIBUTE,
				"AFFILIATE_CUSTOMER_NAME", 5 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("COLLECTION_METHOD", DimensionType.ATTRIBUTE, "COLLECTION_METHOD", 6 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("CURRENCY", DimensionType.ATTRIBUTE, "CURRENCY", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("AFFILIATE_TYPE", DimensionType.ATTRIBUTE, "AFFILIATE_TYPE", 8 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("SERVICE_CODE", DimensionType.ATTRIBUTE, "SERVICE_CODE", 9 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("SERVICE_DESCRIPTION", DimensionType.ATTRIBUTE,
				"SERVICE_DESCRIPTION", 10 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("EVENT", DimensionType.ATTRIBUTE, "EVENT", 11 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("EVENT_DESCRIPTION", DimensionType.ATTRIBUTE, "EVENT_DESCRIPTION", 12 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("CARD_ACCEPTOR_ID", DimensionType.ATTRIBUTE, "CARD_ACCEPTOR_ID", 13 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("QUANTITY_OR_AMOUNT", DimensionType.MEASURE, "QUANTITY_OR_AMOUNT", 14 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("RATE", DimensionType.MEASURE, "RATE", 15 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("TOTAL_CHARGE", DimensionType.MEASURE, "TOTAL_CHARGE", 16 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Scheme invoice date", DimensionType.PERIOD, "Scheme invoice date", 17 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Invoice number", DimensionType.ATTRIBUTE, "Invoice number", 18 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Billing event ID", DimensionType.ATTRIBUTE, "Billing event ID", 19 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event name", DimensionType.ATTRIBUTE,
				"Billing event name", 20 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event category ID",
				DimensionType.ATTRIBUTE, "Billing event category ID", 21 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Billing event category name",
				DimensionType.ATTRIBUTE, "Billing event category name", 22 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 23 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 24 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Broker ID", DimensionType.ATTRIBUTE, "Broker ID", 25 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 26 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 27 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Brand", DimensionType.ATTRIBUTE, "Brand", 28 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Driver", DimensionType.MEASURE, "Driver", 29 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Rate0", DimensionType.MEASURE, "Rate0", 30 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount exl. Tax", DimensionType.MEASURE, "Amount exl. Tax", 31 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D-C", 32 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Year", DimensionType.ATTRIBUTE, "Year", 33 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Month", DimensionType.ATTRIBUTE, "Month", 34 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Entity ID", DimensionType.ATTRIBUTE, "Entity ID", 35 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Product ID", DimensionType.ATTRIBUTE, "Product ID", 36 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Currency0", DimensionType.ATTRIBUTE, "Currency0", 37 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Check", DimensionType.MEASURE, "Check new", 38 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 39 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice type", DimensionType.ATTRIBUTE, "Invoice type", 40 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("SI01", DimensionType.ATTRIBUTE, "SI01", 41 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 42 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 43 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 44 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 45 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 46 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Invoice pivot", DimensionType.ATTRIBUTE, "Invoice pivot", 47 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Client ID", DimensionType.ATTRIBUTE, "Client ID", 48 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Scheme invoice net amount", DimensionType.MEASURE,
				"Scheme invoice net amount", 49 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Month reference date", DimensionType.PERIOD,
				"Month reference date", 50 ));
		return materialized;

	}

	public static MaterializedGrid buildSTA100M109Issuing() {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("STA100 M109 Issuing Name");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Company_issuer", DimensionType.ATTRIBUTE, "Company_issuer", 0 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_Issuer", DimensionType.ATTRIBUTE, "Member_ID_Issuer", 1 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Code_Brand_acquirer", DimensionType.ATTRIBUTE,
				"Code_Brand_acquirer", 2 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Company_acquirer", DimensionType.ATTRIBUTE, "Company_acquirer", 3 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_acquirer", DimensionType.ATTRIBUTE, "Member_ID_acquirer", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Business_Date", DimensionType.PERIOD, "Business_Date", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Business_Cycle", DimensionType.ATTRIBUTE, "Business_Cycle", 6 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Reference_Scheme", DimensionType.ATTRIBUTE, "Reference_Scheme", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Type_Message", DimensionType.ATTRIBUTE, "Type_Message", 8 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Reversal_Indicator", DimensionType.ATTRIBUTE, "Reversal_Indicator", 9 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Status_Transaction", DimensionType.ATTRIBUTE,
				"Status_Transaction", 10 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Transaction_code", DimensionType.ATTRIBUTE, "Transaction_code", 11 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Date___Time_Transaction", DimensionType.PERIOD,
				"Date___Time_Transaction", 12 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_data_input_information",
				DimensionType.ATTRIBUTE, "Card_data_input_information", 13 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Cardholder_information_authentification",
				DimensionType.ATTRIBUTE, "Cardholder_information_authentification", 14 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_capturing_information",
				DimensionType.ATTRIBUTE, "Card_capturing_information", 15 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Environment", DimensionType.ATTRIBUTE, "Environment", 16 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_Cardholder_Presence",
				DimensionType.ATTRIBUTE, "Identification_Cardholder_Presence", 17 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_presence_card",
				DimensionType.ATTRIBUTE, "Identification_presence_card", 18 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_Entering_Method", DimensionType.ATTRIBUTE,
				"Card_Entering_Method", 19 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Method_Cardholder_authentification",
				DimensionType.ATTRIBUTE, "Method_Cardholder_authentification", 20 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Cardholder_authentification",
				DimensionType.ATTRIBUTE, "Cardholder_authentification", 21 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("PIN_information_capturing",
				DimensionType.ATTRIBUTE, "PIN_information_capturing", 22 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Date_financial_processing",
				DimensionType.ATTRIBUTE, "Date_financial_processing", 23 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Bank_Identification", DimensionType.ATTRIBUTE,
				"Bank_Identification", 24));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Transaction_code_routing",
				DimensionType.ATTRIBUTE, "Transaction_code_routing", 25));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Routing_code_acquiring_processing",
				DimensionType.ATTRIBUTE, "Routing_code_acquiring_processing", 26));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Routing_code_issuing_processing",
				DimensionType.ATTRIBUTE, "Routing_code_issuing_processing", 27));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Card_product_code", DimensionType.ATTRIBUTE, "Card_product_code", 28));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Cardholder_Country_Code", DimensionType.ATTRIBUTE,
				"Cardholder_Country_Code", 29));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Authorization_Date_Time", DimensionType.ATTRIBUTE,
				"Authorization_Date_Time", 30));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Authorization_amount", DimensionType.MEASURE,
				"Authorization_amount", 31));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Currency_code", DimensionType.ATTRIBUTE, "Currency_code", 32));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Member_ID_Acquirer2", DimensionType.ATTRIBUTE,
				"Member_ID_Acquirer2", 33));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Acquirer_Country_code", DimensionType.ATTRIBUTE,
				"Acquirer_Country_code", 34));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Acquirer_Currency_code", DimensionType.ATTRIBUTE,
				"Acquirer_Currency_code", 35));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ARN", DimensionType.ATTRIBUTE, "ARN", 36));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_Issuer2", DimensionType.ATTRIBUTE, "Member_ID_Issuer2", 37));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_Issuer2", DimensionType.ATTRIBUTE, "Member_ID_Issuer2", 38));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Issuer_Country_code", DimensionType.ATTRIBUTE,
				"Issuer_Country_code", 39));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Issuer_reference_currency",
				DimensionType.ATTRIBUTE, "Issuer_reference_currency", 40));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_transaction",
				DimensionType.ATTRIBUTE, "Currency_code_transaction", 41));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Amount_transaction", DimensionType.ATTRIBUTE,
				"Sign_Amount_transaction", 42));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount_transaction", DimensionType.MEASURE, "Amount_transaction", 43));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_reconciliation",
				DimensionType.ATTRIBUTE, "Currency_code_reconciliation", 44));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Amount_Reconciliation",
				DimensionType.ATTRIBUTE, "Sign_Amount_Reconciliation", 45));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_reconciliation", DimensionType.MEASURE,
				"Amount_reconciliation", 46));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_cashback", DimensionType.ATTRIBUTE,
				"Currency_code_cashback", 47));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_cashback", DimensionType.ATTRIBUTE,
				"Sign_amount_cashback", 48));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount_cashback", DimensionType.MEASURE, "Amount_cashback", 49));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_dynamic_currency_conversion",
				DimensionType.ATTRIBUTE, "Currency_code_dynamic_currency_conversion", 50));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Sign_DCC", DimensionType.ATTRIBUTE, "Sign_DCC", 51));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Amount_DCC", DimensionType.MEASURE, "Amount_DCC", 52));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Exchange_rate_transaction", DimensionType.MEASURE,
				"Exchange_rate_transaction", 53));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Interchange_region", DimensionType.ATTRIBUTE,
				"Interchange_region", 54));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Description_interchange_fee",
				DimensionType.ATTRIBUTE, "Description_interchange_fee", 55));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_interchange_fee",
				DimensionType.ATTRIBUTE, "Currency_code_interchange_fee", 56));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_interchange_fee",
				DimensionType.ATTRIBUTE, "Sign_amount_interchange_fee", 57));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_interchange_fee", DimensionType.MEASURE,
				"Amount_interchange_fee", 58));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_interchange_fee_in_currency_issuer",
				DimensionType.ATTRIBUTE, "Currency_code_interchange_fee_in_currency_issuer", 59));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_interchange_fee_issuer",
				DimensionType.ATTRIBUTE, "Sign_amount_interchange_fee_issuer", 60));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_interchange_fee_in_currency_issuer",
				DimensionType.MEASURE, "Amount_interchange_fee_in_currency_issuer", 61));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_interchange_fee_networks",
				DimensionType.ATTRIBUTE, "Currency_code_interchange_fee_networks", 62));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_interchange_fee_networks",
				DimensionType.ATTRIBUTE, "Sign_amount_interchange_fee_networks", 63));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_interchange_fee_networks",
				DimensionType.MEASURE, "Amount_interchange_fee_networks", 64));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_interchange_fee", DimensionType.ATTRIBUTE,
				"Sign_interchange_fee", 65 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("IRD_Customer", DimensionType.ATTRIBUTE, "IRD_Customer", 66));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_markup_fee", DimensionType.MEASURE,
				"Currency_code_markup_fee", 67));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_markup_fee", DimensionType.ATTRIBUTE,
				"Sign_amount_markup_fee", 68));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount_markup_fee", DimensionType.MEASURE, "Amount_markup_fee", 69));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_processing_fee",
				DimensionType.ATTRIBUTE, "Currency_code_processing_fee", 70));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_processing_fee",
				DimensionType.ATTRIBUTE, "Sign_amount_processing_fee", 71));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_processing_fee", DimensionType.MEASURE,
				"Amount_processing_fee", 72));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Amount_Assessment_fee",
				DimensionType.ATTRIBUTE, "Sign_Amount_Assessment_fee", 73));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_Assessment_fee", DimensionType.MEASURE,
				"Amount_Assessment_fee", 74));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_ATM_access_fee", DimensionType.MEASURE,
				"Amount_ATM_access_fee", 75));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Merchant_identifier", DimensionType.ATTRIBUTE,
				"Merchant_identifier", 76));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_Acceptor_Terminal_ID",
				DimensionType.ATTRIBUTE, "Card_Acceptor_Terminal_ID", 77));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Name_merchant", DimensionType.ATTRIBUTE, "Name_merchant", 78));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("City_merchant", DimensionType.ATTRIBUTE, "City_merchant", 79));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Postal_code_merchant", DimensionType.ATTRIBUTE,
				"Postal_code_merchant", 80 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Province_merchant", DimensionType.ATTRIBUTE, "Province_merchant", 81));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Country_code_merchant", DimensionType.ATTRIBUTE,
				"Country_code_merchant", 82 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Area_merchant", DimensionType.ATTRIBUTE, "Area_merchant", 83));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Merchant_Category_code", DimensionType.ATTRIBUTE,
				"Merchant_Category_code", 84 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Merchant_period_id", DimensionType.ATTRIBUTE,
				"Merchant_period_id", 85 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_Reference", DimensionType.ATTRIBUTE,
				"Chargeback_Reference", 86));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_Reason_Code", DimensionType.ATTRIBUTE,
				"Chargeback_Reason_Code", 87));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_message", DimensionType.ATTRIBUTE,
				"Chargeback_message", 88));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Indicator_Special_chargeback",
				DimensionType.ATTRIBUTE, "Indicator_Special_chargeback", 89));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("DSMU_File_number", DimensionType.ATTRIBUTE, "DSMU_File_number", 90));
		materialized.getColumnListChangeHandler().addNew(buildColumn("DSMU_Reference_number", DimensionType.ATTRIBUTE,
				"DSMU_Reference_number", 91));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_Case_ID", DimensionType.ATTRIBUTE,
				"Chargeback_Case_ID", 92));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Filler_12", DimensionType.ATTRIBUTE, "Filler_12", 93));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Retrieval_reason_code", DimensionType.ATTRIBUTE,
				"Retrieval_reason_code", 94));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Reason_code", DimensionType.ATTRIBUTE, "Reason_code", 95));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_document_type",
				DimensionType.ATTRIBUTE, "Identification_document_type", 96));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_Response", DimensionType.ATTRIBUTE,
				"Identification_Response", 97));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Petrol_-_volume", DimensionType.MEASURE, "Petrol_-_volume", 98));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Petrol_-_Unit_price", DimensionType.MEASURE,
				"Petrol_-_Unit_price", 99));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Petrol_-_product_ID", DimensionType.MEASURE,
				"Petrol_-_product_ID", 100));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_settlement",
				DimensionType.ATTRIBUTE, "Currency_code_settlement", 101));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Settlement_Amount", DimensionType.ATTRIBUTE,
				"Sign_Settlement_Amount", 102));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Settlement_amount", DimensionType.MEASURE, "Settlement_amount", 103));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_1", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_1", 104));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_2", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_2", 105));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_3", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_3", 106));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_4", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_4", 107));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_5", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_5", 108));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("File_Name", DimensionType.ATTRIBUTE, "File_Name", 109));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Reco", DimensionType.ATTRIBUTE, "Reco", 110));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Reconciliation_date", DimensionType.ATTRIBUTE,
				"Reconciliation_date", 11));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("EntryDate", DimensionType.PERIOD, "Entry date", 112));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML_ID", DimensionType.ATTRIBUTE, "PML ID", 113));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Affiliate_ID", DimensionType.ATTRIBUTE, "Affiliate_ID", 114));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme_ID", DimensionType.ATTRIBUTE, "Scheme ID", 115));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Scheme_Platform_ID", DimensionType.ATTRIBUTE,
				"Scheme Platform ID", 116));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme_Cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 117));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("TPPN", DimensionType.ATTRIBUTE, "TPPN", 118));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Value_date", DimensionType.PERIOD, "Value date", 119));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Processor_Date", DimensionType.PERIOD, "Processor date", 120));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D-C", 121));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ATM_ID", DimensionType.ATTRIBUTE, "ATM_ID", 122));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Card_Pan_6_digit", DimensionType.ATTRIBUTE, "Card_Pan_6_digit", 123));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("CountItems", DimensionType.MEASURE, "CountItems", 124));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 125));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 126));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 127));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 128));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 129));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Operation code", DimensionType.ATTRIBUTE, "Operation code", 130));
		return materialized;

	}
	public static MaterializedGrid buildSTA110M109Acquiring() {
		MaterializedGrid materialized = new MaterializedGrid();
		materialized.setEditable(false);
		materialized.setName("STA110 M109 Acquiring");
		materialized.setVisibleInShortcut(true);
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Company_issuer", DimensionType.ATTRIBUTE, "Company_issuer", 0 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_Issuer", DimensionType.ATTRIBUTE, "Member_ID_Issuer", 1 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Code_Brand_acquirer", DimensionType.ATTRIBUTE,
				"Code_Brand_acquirer", 2 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Company_acquirer", DimensionType.ATTRIBUTE, "Company_acquirer", 3 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_acquirer", DimensionType.ATTRIBUTE, "Member_ID_acquirer", 4 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Business_Date", DimensionType.PERIOD, "Business_Date", 5 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Business_Cycle", DimensionType.ATTRIBUTE, "Business_Cycle", 6 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Reference_Scheme", DimensionType.ATTRIBUTE, "Reference_Scheme", 7 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Type_Message", DimensionType.ATTRIBUTE, "Type_Message", 8 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Reversal_Indicator", DimensionType.ATTRIBUTE, "Reversal_Indicator", 9 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Status_Transaction", DimensionType.ATTRIBUTE,
				"Status_Transaction", 10 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Transaction_code", DimensionType.ATTRIBUTE, "Transaction_code", 11 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Date___Time_Transaction", DimensionType.PERIOD,
				"Date___Time_Transaction", 12 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_data_input_information",
				DimensionType.ATTRIBUTE, "Card_data_input_information", 13 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Cardholder_information_authentification",
				DimensionType.ATTRIBUTE, "Cardholder_information_authentification", 14 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_capturing_information",
				DimensionType.ATTRIBUTE, "Card_capturing_information", 15 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Environment", DimensionType.ATTRIBUTE, "Environment", 16 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_Cardholder_Presence",
				DimensionType.ATTRIBUTE, "Identification_Cardholder_Presence", 17 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_presence_card",
				DimensionType.ATTRIBUTE, "Identification_presence_card", 18 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_Entering_Method", DimensionType.ATTRIBUTE,
				"Card_Entering_Method", 19 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Method_Cardholder_authentification",
				DimensionType.ATTRIBUTE, "Method_Cardholder_authentification", 20 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Cardholder_authentification",
				DimensionType.ATTRIBUTE, "Cardholder_authentification", 21 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("PIN_information_capturing",
				DimensionType.ATTRIBUTE, "PIN_information_capturing", 22 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Date_financial_processing",
				DimensionType.ATTRIBUTE, "Date_financial_processing", 23 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Bank_Identification", DimensionType.ATTRIBUTE,
				"Bank_Identification", 24));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Transaction_code_routing",
				DimensionType.ATTRIBUTE, "Transaction_code_routing", 25));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Routing_code_acquiring_processing",
				DimensionType.ATTRIBUTE, "Routing_code_acquiring_processing", 26));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Routing_code_issuing_processing",
				DimensionType.ATTRIBUTE, "Routing_code_issuing_processing", 27));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Card_product_code", DimensionType.ATTRIBUTE, "Card_product_code", 28));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Cardholder_Country_Code", DimensionType.ATTRIBUTE,
				"Cardholder_Country_Code", 29));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Authorization_Date_Time", DimensionType.ATTRIBUTE,
				"Authorization_Date_Time", 30));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Authorization_amount", DimensionType.MEASURE,
				"Authorization_amount", 31));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Currency_code", DimensionType.ATTRIBUTE, "Currency_code", 32));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Member_ID_Acquirer2", DimensionType.ATTRIBUTE,
				"Member_ID_Acquirer2", 33));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Acquirer_Country_code", DimensionType.ATTRIBUTE,
				"Acquirer_Country_code", 34));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Acquirer_Currency_code", DimensionType.ATTRIBUTE,
				"Acquirer_Currency_code", 35));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ARN", DimensionType.ATTRIBUTE, "ARN", 36));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_Issuer2", DimensionType.ATTRIBUTE, "Member_ID_Issuer2", 37));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Member_ID_Issuer2", DimensionType.ATTRIBUTE, "Member_ID_Issuer2", 38));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Issuer_Country_code", DimensionType.ATTRIBUTE,
				"Issuer_Country_code", 39));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Issuer_reference_currency",
				DimensionType.ATTRIBUTE, "Issuer_reference_currency", 40));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_transaction",
				DimensionType.ATTRIBUTE, "Currency_code_transaction", 41));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Amount_transaction", DimensionType.ATTRIBUTE,
				"Sign_Amount_transaction", 42));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount_transaction", DimensionType.MEASURE, "Amount_transaction", 43));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_reconciliation",
				DimensionType.ATTRIBUTE, "Currency_code_reconciliation", 44));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Amount_Reconciliation",
				DimensionType.ATTRIBUTE, "Sign_Amount_Reconciliation", 45));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_reconciliation", DimensionType.MEASURE,
				"Amount_reconciliation", 46));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_cashback", DimensionType.ATTRIBUTE,
				"Currency_code_cashback", 47));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_cashback", DimensionType.ATTRIBUTE,
				"Sign_amount_cashback", 48));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount_cashback", DimensionType.MEASURE, "Amount_cashback", 49));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_dynamic_currency_conversion",
				DimensionType.ATTRIBUTE, "Currency_code_dynamic_currency_conversion", 50));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Sign_DCC", DimensionType.ATTRIBUTE, "Sign_DCC", 51));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Amount_DCC", DimensionType.MEASURE, "Amount_DCC", 52));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Exchange_rate_transaction", DimensionType.MEASURE,
				"Exchange_rate_transaction", 53));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Interchange_region", DimensionType.ATTRIBUTE,
				"Interchange_region", 54));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Description_interchange_fee",
				DimensionType.ATTRIBUTE, "Description_interchange_fee", 55));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_interchange_fee",
				DimensionType.ATTRIBUTE, "Currency_code_interchange_fee", 56));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_interchange_fee",
				DimensionType.ATTRIBUTE, "Sign_amount_interchange_fee", 57));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_interchange_fee", DimensionType.MEASURE,
				"Amount_interchange_fee", 58));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_interchange_fee_in_currency_issuer",
				DimensionType.ATTRIBUTE, "Currency_code_interchange_fee_in_currency_issuer", 59));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_interchange_fee_issuer",
				DimensionType.ATTRIBUTE, "Sign_amount_interchange_fee_issuer", 60));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_interchange_fee_in_currency_issuer",
				DimensionType.MEASURE, "Amount_interchange_fee_in_currency_issuer", 61));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_interchange_fee_networks",
				DimensionType.ATTRIBUTE, "Currency_code_interchange_fee_networks", 62));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_interchange_fee_networks",
				DimensionType.ATTRIBUTE, "Sign_amount_interchange_fee_networks", 63));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_interchange_fee_networks",
				DimensionType.MEASURE, "Amount_interchange_fee_networks", 64));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_interchange_fee", DimensionType.ATTRIBUTE,
				"Sign_interchange_fee", 65 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("IRD_Customer", DimensionType.ATTRIBUTE, "IRD_Customer", 66));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_markup_fee", DimensionType.MEASURE,
				"Currency_code_markup_fee", 67));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_markup_fee", DimensionType.ATTRIBUTE,
				"Sign_amount_markup_fee", 68));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Amount_markup_fee", DimensionType.MEASURE, "Amount_markup_fee", 69));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_processing_fee",
				DimensionType.ATTRIBUTE, "Currency_code_processing_fee", 70));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_amount_processing_fee",
				DimensionType.ATTRIBUTE, "Sign_amount_processing_fee", 71));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_processing_fee", DimensionType.MEASURE,
				"Amount_processing_fee", 72));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Amount_Assessment_fee",
				DimensionType.ATTRIBUTE, "Sign_Amount_Assessment_fee", 73));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_Assessment_fee", DimensionType.MEASURE,
				"Amount_Assessment_fee", 74));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Amount_ATM_access_fee", DimensionType.MEASURE,
				"Amount_ATM_access_fee", 75));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Merchant_identifier", DimensionType.ATTRIBUTE,
				"Merchant_identifier", 76));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Card_Acceptor_Terminal_ID",
				DimensionType.ATTRIBUTE, "Card_Acceptor_Terminal_ID", 77));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Name_merchant", DimensionType.ATTRIBUTE, "Name_merchant", 78));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("City_merchant", DimensionType.ATTRIBUTE, "City_merchant", 79));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Postal_code_merchant", DimensionType.ATTRIBUTE,
				"Postal_code_merchant", 80 ));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Province_merchant", DimensionType.ATTRIBUTE, "Province_merchant", 81));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Country_code_merchant", DimensionType.ATTRIBUTE,
				"Country_code_merchant", 82 ));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Area_merchant", DimensionType.ATTRIBUTE, "Area_merchant", 83));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Merchant_Category_code", DimensionType.ATTRIBUTE,
				"Merchant_Category_code", 84 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Merchant_period_id", DimensionType.ATTRIBUTE,
				"Merchant_period_id", 85 ));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_Reference", DimensionType.ATTRIBUTE,
				"Chargeback_Reference", 86));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_Reason_Code", DimensionType.ATTRIBUTE,
				"Chargeback_Reason_Code", 87));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_message", DimensionType.ATTRIBUTE,
				"Chargeback_message", 88));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Indicator_Special_chargeback",
				DimensionType.ATTRIBUTE, "Indicator_Special_chargeback", 89));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("DSMU_File_number", DimensionType.ATTRIBUTE, "DSMU_File_number", 90));
		materialized.getColumnListChangeHandler().addNew(buildColumn("DSMU_Reference_number", DimensionType.ATTRIBUTE,
				"DSMU_Reference_number", 91));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Chargeback_Case_ID", DimensionType.ATTRIBUTE,
				"Chargeback_Case_ID", 92));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Filler_12", DimensionType.ATTRIBUTE, "Filler_12", 93));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Retrieval_reason_code", DimensionType.ATTRIBUTE,
				"Retrieval_reason_code", 94));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Reason_code", DimensionType.ATTRIBUTE, "Reason_code", 95));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_document_type",
				DimensionType.ATTRIBUTE, "Identification_document_type", 96));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Identification_Response", DimensionType.ATTRIBUTE,
				"Identification_Response", 97));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Petrol_-_volume", DimensionType.MEASURE, "Petrol_-_volume", 98));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Petrol_-_Unit_price", DimensionType.MEASURE,
				"Petrol_-_Unit_price", 99));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Petrol_-_product_ID", DimensionType.MEASURE,
				"Petrol_-_product_ID", 100));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Currency_code_settlement",
				DimensionType.ATTRIBUTE, "Currency_code_settlement", 101));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Sign_Settlement_Amount", DimensionType.ATTRIBUTE,
				"Sign_Settlement_Amount", 102));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Settlement_amount", DimensionType.MEASURE, "Settlement_amount", 103));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_1", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_1", 104));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_2", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_2", 105));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_3", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_3", 106));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_4", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_4", 107));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Clearing_Warning_Code_5", DimensionType.ATTRIBUTE,
				"Clearing_Warning_Code_5", 108));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("File_Name", DimensionType.ATTRIBUTE, "File_Name", 109));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Reco", DimensionType.ATTRIBUTE, "Reco", 110));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Reconciliation_date", DimensionType.ATTRIBUTE,
				"Reconciliation_date", 11));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("EntryDate", DimensionType.PERIOD, "Entry date", 112));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("PML_ID", DimensionType.ATTRIBUTE, "PML ID", 113));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Affiliate_ID", DimensionType.ATTRIBUTE, "Affiliate_ID", 114));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme_ID", DimensionType.ATTRIBUTE, "Scheme ID", 115));
		materialized.getColumnListChangeHandler().addNew(buildColumn("Scheme_Platform_ID", DimensionType.ATTRIBUTE,
				"Scheme Platform ID", 116));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Scheme_Cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 117));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("TPPN", DimensionType.ATTRIBUTE, "TPPN", 118));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Value_date", DimensionType.PERIOD, "Value date", 119));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Processor_Date", DimensionType.PERIOD, "Processor date", 120));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D-C", 121));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("ATM_ID", DimensionType.ATTRIBUTE, "ATM_ID", 122));
		materialized.getColumnListChangeHandler().addNew(
				buildColumn("Card_Pan_6_digit", DimensionType.ATTRIBUTE, "Card_Pan_6_digit", 123));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("CountItems", DimensionType.MEASURE, "CountItems", 124));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load nbr", DimensionType.ATTRIBUTE, "Load nbr", 125));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load date", DimensionType.PERIOD, "Load date", 126));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load mode", DimensionType.ATTRIBUTE, "Load mode", 127));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Creator", DimensionType.ATTRIBUTE, "Creator", 128));
		materialized.getColumnListChangeHandler()
				.addNew(buildColumn("Load source", DimensionType.ATTRIBUTE, "Load source", 129));
		return materialized;

	}

	private static MaterializedGridColumn buildColumn(String name, DimensionType type, String dimensionName, int position) {
		MaterializedGridColumn column = new MaterializedGridColumn();
		column.setName(name);
		column.setType(type);
		column.setPosition(position);
		return column;
	}

}
