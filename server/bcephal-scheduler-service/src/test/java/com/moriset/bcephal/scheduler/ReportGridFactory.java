/**
 * 
 */
package com.moriset.bcephal.scheduler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleType;

/**
 * @author Pascal Dev
 */
public class ReportGridFactory {
	
	
	
	public static List<Grille> buildReportingGrilles() throws Throwable{
		List<Grille> items = new ArrayList<>();
		items.add(buildBillingEvents());
		items.add(buildBNKAccountBalanceCurrentYear());
		items.add(buildCHK100BillingEventAmount());
		items.add(buildCHK100BillingEventAmountDetailsPerClient());
		items.add(buildCHK130BillingAmountPerClientPerBillingGroup());
		items.add(buildCHK200BillingAmountTotalPerBillingGroup());
		items.add(buildCHK210BillingAmountTotalPerBillingGroupPerClient());
		items.add(buildCHK300MCIPMLInvoiceTotalAmountPerMonth());
		items.add(buildCHK350AffiliateinvoiceTotalPerMonth());
		items.add(buildEditableReport());
		items.add(buildEOM006IssuingM109());
		items.add(buildEOM100MAReconciled());
		items.add(buildEOM105MANotReconciled());
		items.add(buildEOM106AcquiringM109());
		items.add(buildEOM200SAReconciled());
		items.add(buildEOM205SANotReconciled());
		items.add(buildEOM300MT942MAReconciled());
		items.add(buildEOM305MT942SAReconciled());
		items.add(buildEOM310MT942NeutralisedReconciled());
		items.add(buildEOM320MT942NotReconciled());
		items.add(buildEOM900EPBIssuing());
		items.add(buildEOM910EPBAcquiring());
		items.add(buildLOG001FinancialMovements());
		items.add(buildLOG002PrefundingAdvisements());
		items.add(buildLOG003SettlementAdvisements());
		items.add(buildLOG004MemberAdvisements());
		items.add(buildREC002ISSUINGR02RecoOfTheDay());
		items.add(buildREC003ISSUINGR03RecoOfTheDay());
		items.add(buildREC004ISSUINGR04RecoOfTheDay());
		items.add(buildREC102ACQUIRINGR02RecOfTheDay());
		items.add(buildREC103ACQUIRINGR03RecoOfTheDay());
		items.add(buildREC104ACQUIRINGR04RecoOfTheDay());
		items.add(buildREP001EPBBankAccount());
		items.add(buildREP002IssuingSA());
		items.add(buildREP003IssuingMA());
		items.add(buildREP005IssuingREC());
		items.add(buildREP006IssuingM109());
		items.add(buildREP102AcquiringSA());
		items.add(buildREP105AcquiringREC());
		items.add(buildREP106AcquiringM109());
		items.add(buildREP201PrefundingAdvisement());
		items.add(buildREP901EPBBankAccountNeutralizedItems());
		items.add(buildReportGrid1());
		items.add(buildRREP103AcquiringMA());
		items.add(buildSCH100PMLInvoice());
		items.add(buildSCH102AffiliateInvoice());
		items.add(buildSYS001FinancialMovements());
		items.add(buildSYS002PrefundingAdvisements());
		items.add(buildSYS003SettlementAdvisements());
		items.add(buildSYS004MemberAdvisements());
		items.add(buildSYS100Advisements());
		items.add(buildSYS105ListSchemeSchemePlatformAndSchemeProduct());
		items.add(buildSYS150ListOfAffiliateInMastercardInvoices());
		items.add(buildSYS210RECTypeAndAccount());
		items.add(buildSYS230MAPerClientForMembershipVolumeFeeBilling());
		items.add(buildSYS235MATotalForMembershipVol());
		items.add(buildSYS300MAPerBank());
		items.add(buildSYS320EOMMAEPB());
		items.add(buildSYS330M1097800());
		items.add(buildTEST001SettlementAdvisement());
		items.add(buildTEST002MemberAdvisement());
		return items;
	}
	
	
	public static Grille buildBillingEvents() {
		Grille grid = new Grille();
		grid.setName("Billing events");
		grid.setConsolidated(false);
		grid.setEditable(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice number", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing event description", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing event status", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Client name", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Client ID", 5L));
		return grid;
	}
	
	public static Grille buildBNKAccountBalanceCurrentYear() {
		Grille grid = new Grille();
		grid.setName("BNK Account balance - current year");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net financial amount", 2L));
		return grid;
	}
	
	public static Grille buildCHK100BillingEventAmount() {
		Grille grid = new Grille();
		grid.setName("CHK100 Billing event amount");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Billing event date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 1", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 2", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing amount", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing driver", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing measure", 7L));
		
		return grid;
	}
	
	public static Grille buildCHK100BillingEventAmountDetailsPerClient() {
		Grille grid = new Grille();
		grid.setName("CHK100 Billing event amount - details per client");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Billing event date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Client ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 1", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 2", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing amount", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing driver", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing measure", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing event status", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice number", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice status", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice type", 12L));
		
		return grid;
	}
	
	public static Grille buildCHK130BillingAmountPerClientPerBillingGroup() {
		Grille grid = new Grille();
		grid.setName("CHK130 Billing amount per client per billing group");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Billing event date", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Client ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Client name", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 1", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 2", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Unit cost", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing driver", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing amount", 9L));
		
		return grid;
	}
	
	public static Grille buildCHK200BillingAmountTotalPerBillingGroup() {
		Grille grid = new Grille();
		grid.setName("CHK200 Billing amount total  per billing group");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Billing event date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 1", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 2", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing amount", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing event status", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing event nature", 7L));
		
		return grid;
	}
	
	public static Grille buildCHK210BillingAmountTotalPerBillingGroupPerClient() {
		Grille grid = new Grille();
		grid.setName("CHK210 Billing amount total  per billing group per client");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Billing event date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 1", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing group 2", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Client ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Billing amount", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing event status", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Billing event nature", 8L));
		
		return grid;
	}
	
	public static Grille buildCHK300MCIPMLInvoiceTotalAmountPerMonth() throws Throwable {
		Grille grid = new Grille();
		grid.setName("CHK300 MCI PML Invoice total amount - per month");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Invoice pivot", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Scheme invoice date", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "AFFILIATE", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Client ID - non native", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount incl. tax", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "TOTAL_CHARGE", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Check", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "QUANTITY_AMOUNT", 8L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Driver", 9L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "RATE", 10L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Rate", 11L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount exl. Tax", 12L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "CHARGE", 13L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Month", 14L));
		
		return grid;
	}
	
	public static Grille buildCHK350AffiliateinvoiceTotalPerMonth() throws Throwable {
		Grille grid = new Grille();
		grid.setName("CHK350 Affiliate invoice - total per month");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Invoice pivot", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Month", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount exl. Tax", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "TOTAL_CHARGE", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Check", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "QUANTITY_OR_AMOUNT", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Driver", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "RATE", 8L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Rate0", 9L));
		return grid;
	}
	
	public static Grille buildEditableReport() throws Throwable {
		Grille grid = new Grille();
		grid.setName("Editable report");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(0).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Client ID", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(0).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Creator", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(0).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Load mode", 3L));
		return grid;
	}
	
	public static Grille buildEOM006IssuingM109() throws Throwable {
		Grille grid = new Grille();
		grid.setName("EOM006 Issuing M109");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Business_Date", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Member_ID_Issuer", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Type_Message", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount_reconciliation", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Bank_Identification", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "CountItems", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Business_Cycle", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Reason_code", 8L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Scheme_ID", 9L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Member_ID_Issuer2", 10L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Member_ID_acquirer", 11L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Value_date", 12L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(29).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "D/C", 13L));
		
		return grid;
	}
	
	public static Grille buildEOM100MAReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM100 MA reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 5L));
		return grid;
	}
	
	public static Grille buildEOM105MANotReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM105 MA not reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 5L));
		return grid;
	}
	
	public static Grille buildEOM106AcquiringM109() throws Throwable {
		Grille grid = new Grille();
		grid.setName("EOM106 Acquiring M109");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Business_Date", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Member_ID_acquirer", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Type_Message", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount_reconciliation", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Bank_Identification", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "CountItems", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Business_Cycle", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Reason_code", 8L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Scheme_ID", 9L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Member_ID_Issuer", 10L));
		return grid;
	}
	
	public static Grille buildEOM200SAReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM200 SA reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 5L));
		return grid;
	}
	
	public static Grille buildEOM205SANotReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM205 SA not reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 5L));
		return grid;
	}
	
	public static Grille buildEOM300MT942MAReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM300 MT942 MA reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Financial amount", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 7L));
		return grid;
	}
	
	public static Grille buildEOM305MT942SAReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM305 MT942 SA reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Financial amount", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 6L));
		return grid;
	}
	
	public static Grille buildEOM310MT942NeutralisedReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM310 MT942 neutralised reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 8L));
		return grid;
	}
	
	public static Grille buildEOM320MT942NotReconciled() {
		Grille grid = new Grille();
		grid.setName("EOM320 MT942 not reconciled");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reconciliation YES NO", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 7L));
		return grid;
	}
	
	public static Grille buildEOM900EPBIssuing() throws Throwable {
		Grille grid = new Grille();
		grid.setName("EOM900 EPB Issuing");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Start date", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Measure ID", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Measure Name", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount ", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Sign", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Remark", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "End date", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "PML Type", 8L));
		return grid;
	}
	
	public static Grille buildEOM910EPBAcquiring() throws Throwable {
		Grille grid = new Grille();
		grid.setName("EOM910 EPB Acquiring");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Start date", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Measure ID", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Measure Name", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount ", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Sign", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Remark", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Period end date", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(2).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "PML Type", 8L));
		return grid;
	}
	
	public static Grille buildLOG001FinancialMovements() {
		Grille grid = new Grille();
		grid.setName("LOG001 Financial movements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Financial amount", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R1", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Account N°", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Name", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Message", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated advisement date", 15L));
		return grid;
	}
	
	public static Grille buildLOG002PrefundingAdvisements() {
		Grille grid = new Grille();
		grid.setName("LOG002 Prefunding advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R1", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R1", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member bank name", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 12L));
		return grid;
	}
	
	public static Grille buildLOG003SettlementAdvisements() {
		Grille grid = new Grille();
		grid.setName("LOG003 Settlement advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement message", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique Report ID", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 15L));
		return grid;
	}
	
	public static Grille buildLOG004MemberAdvisements() {
		Grille grid = new Grille();
		grid.setName("LOG004 Member advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member bank name", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement message", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique Report ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor ID", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 17L));
		return grid;
	}
	
	public static Grille buildREC002ISSUINGR02RecoOfTheDay() {
		Grille grid = new Grille();
		grid.setName("REC002 ISSUING R02 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 3L));
		return grid;
	}
	
	public static Grille buildREC003ISSUINGR03RecoOfTheDay() {
		Grille grid = new Grille();
		grid.setName("REC003 ISSUING R03 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 4L));
		return grid;
	}
	
	public static Grille buildREC004ISSUINGR04RecoOfTheDay() {
		Grille grid = new Grille();
		grid.setName("REC004 ISSUING R04 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 4L));
		return grid;
	}
	
	public static Grille buildREC102ACQUIRINGR02RecOfTheDay() {
		Grille grid = new Grille();
		grid.setName("REC102 ACQUIRING R02 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 3L));
		return grid;
	}
	
	public static Grille buildREC103ACQUIRINGR03RecoOfTheDay() {
		Grille grid = new Grille();
		grid.setName("REC103 ACQUIRING R03 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 4L));
		return grid;
	}
	
	public static Grille buildREC104ACQUIRINGR04RecoOfTheDay() {
		Grille grid = new Grille();
		grid.setName("REC104 ACQUIRING R04 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 4L));
		return grid;
	}
	
	public static Grille buildREP001EPBBankAccount() {
		Grille grid = new Grille();
		grid.setName("REP001 EPB bank account");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Transaction N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Statement N°", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sequence N°", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "MT942 Date", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value Date", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "MT942 Entry Date", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Financial Amount", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "SwiftTxCode", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Name", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Account N°", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Financial Account ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Message", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Broker ID", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Type", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Code", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Ultimate Beneficiary Name", 22L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Ultimate Beneficiary ID", 23L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "eWL report date", 24L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 25L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 26L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 27L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 28L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 29L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 30L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 31L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 32L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 33L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 34L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F01", 35L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F03", 36L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F04", 37L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 38L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R03", 39L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R04", 40L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R1", 41L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 42L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 43L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R05", 44L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R1", 45L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R1", 46L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R3", 47L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R3", 48L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R4", 49L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R4", 50L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PR03", 51L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "ETL Load File name", 52L));
		return grid;
	}
	
	public static Grille buildREP002IssuingSA() {
		Grille grid = new Grille();
		grid.setName("REP002 Issuing SA");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique report ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Name", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Broker ID", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor Date", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Report Time", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Date", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Value Date", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency Name", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting Amount", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Native DC", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Message", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Operation ID", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Name", 22L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value Date", 23L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 24L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 25L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 26L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 27L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 28L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 29L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 30L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R02", 31L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F02", 32L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 33L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 34L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R04", 35L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F04", 36L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 37L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R2", 38L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R2", 39L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R4", 40L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R4", 41L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "ETL Load File name", 42L));
		return grid;
	}
	
	public static Grille buildREP003IssuingMA() {
		Grille grid = new Grille();
		grid.setName("REP003 Issuing MA");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique report ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Name", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Broker ID", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor Date", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Report Time", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Date", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Value Date", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency Name", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting Amount", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Native DC", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Message", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Operation ID", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Name", 22L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value Date", 23L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 24L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 25L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 26L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 27L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 28L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 29L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 30L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 31L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R02", 32L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F02", 33L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 34L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 35L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R03", 36L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F03", 37L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 38L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 39L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R2", 40L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R2", 41L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R3", 42L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R3", 43L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "ETL Load File name", 44L));
		return grid;
	}
	
	public static Grille buildREP005IssuingREC() {
		Grille grid = new Grille();
		grid.setName("REP005 Issuing REC");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "REC Type ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Brocker ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor Date", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "REC Account ID", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R07", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R7", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R7", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R7", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R02", 22L));
		return grid;
	}
	
	public static Grille buildREP006IssuingM109() {
		Grille grid = new Grille();
		grid.setName("REP006 Issuing M109");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Business_Date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member_ID_Issuer", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Type_Message", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Amount_reconciliation", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank_Identification", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "CountItems", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Business_Cycle", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Reason_code", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme_ID", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member_ID_Issuer2", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member_ID_acquirer", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value_date", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D/C", 13L));
		return grid;
	}
	
	public static Grille buildREP102AcquiringSA() {
		Grille grid = new Grille();
		grid.setName("REP102 Acquiring SA");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique report ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Name", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Broker ID", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor Date", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Report Time", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Date", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Value Date", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency Name", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting Amount", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Native DC", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Message", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Operation ID", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Name", 22L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value Date", 23L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 24L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 25L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 26L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 27L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 28L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 29L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 30L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F02", 31L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R02", 32L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 33L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 34L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R04", 35L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F04", 36L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 37L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 38L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R2", 39L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R2", 40L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R4", 41L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R4", 42L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "ETL Load File name", 43L));
		return grid;
	}
	
	public static Grille buildRREP103AcquiringMA() {
		Grille grid = new Grille();
		grid.setName("REP103 Acquiring MA");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique report ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Name", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Broker ID", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor Date", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Report Time", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Date", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Scheme Value Date", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency Name", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting Amount", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Message", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Operation ID", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Member Bank ID", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor Name", 22L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value Date", 23L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 24L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 25L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 26L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 27L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 28L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 29L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 30L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 31L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 32L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F02", 33L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R02", 34L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 35L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 36L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R03", 37L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F03", 38L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 39L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Associated payment date", 40L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R2", 41L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R2", 42L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R3", 43L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R3", 44L));
		return grid;
	}
	
	public static Grille buildREP105AcquiringREC() {
		Grille grid = new Grille();
		grid.setName("REP105 Acquiring REC");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "REC Type ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Broker ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor date", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "TPPN", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "REC Account ID", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting Amount", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R07", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R07", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F07", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R7", 22L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R7", 23L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R7", 24L));
		return grid;
	}
	
	public static Grille buildREP106AcquiringM109() throws Throwable {
		Grille grid = new Grille();
		grid.setName("REP106 Acquiring M109");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Business_Date", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Member_ID_acquirer", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Type_Message", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "Amount_reconciliation", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Bank_Identification", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "CountItems", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Business_Cycle", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Reason_code", 8L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Scheme_ID", 9L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(30).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Member_ID_Issuer", 10L));
		return grid;
	}
	
	public static Grille buildREP201PrefundingAdvisement() {
		Grille grid = new Grille();
		grid.setName("REP201 Prefunding advisement");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme PML ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Currency ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D/C", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting Amount", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value Date", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement message", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R1", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member bank name", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement ID", 16L));
		return grid;
	}
	
	public static Grille buildREP901EPBBankAccountNeutralizedItems() {
		Grille grid = new Grille();
		grid.setName("REP901 EPB bank account - Neutralized items");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Neu04", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Neu03", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Transaction N°", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Statement N°", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sequence N°", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "MT942 Date", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "MT942 Entry Date", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Financial Amount", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "SwiftTxCode", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Name", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Account N°", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Financial Account ID", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Message", 20L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Broker ID", 21L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Type", 22L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Code", 23L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Ultimate Beneficiary Name", 24L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Ultimate Beneficiary ID", 25L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "eWL report date", 26L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Cycle", 27L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 28L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML Type", 29L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Product Type", 30L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Check", 31L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 32L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 33L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 34L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 35L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 36L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F01", 37L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F03", 38L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "F04", 39L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R01", 40L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R03", 41L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Note R04", 42L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R1", 43L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 44L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 45L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Note R05", 46L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining  amount R1", 47L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R1", 48L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R3", 49L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R3", 50L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R4", 51L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R4", 52L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PR03", 53L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "ETL Load File name", 54L));
		return grid;
	}
	
	public static Grille buildReportGrid1() {
		Grille grid = new Grille();
		grid.setName("Report Grid 1");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.CALCULATED_MEASURE, "Measure 1", 1L));
		return grid;
	}
	
	public static Grille buildSCH100PMLInvoice() throws Throwable {
		Grille grid = new Grille();
		grid.setName("SCH100 PML Invoice");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Invoice date", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Invoice number", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event ID", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event name", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event category ID", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event category name", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "PML ID", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Entry date", 8L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "DOCUMENT_TYPE", 9L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "BILLING_CYCLE_DATE", 10L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "INVOICE_ICA", 11L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "ACTIVITY_ICA", 12L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "BILLABLE_ICA", 13L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "COLLECTION_METHOD", 14L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "PERIOD_START_DATE", 15L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "PERIOD_END_DATE", 16L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "AFFILIATE", 17L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "UOM", 18L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "QUANTITY_AMOUNT", 19L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "RATE", 20L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "CHARGE", 21L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "TOTAL_CHARGE", 22L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "TAX_CHARGE", 23L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "VAT_RATE", 24L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "VAT_CHARGE", 25L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(27).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "SBF_EXPLANATORY_TEXT", 26L));
		return grid;
	}
	
	public static Grille buildSCH102AffiliateInvoice() throws Throwable {
		Grille grid = new Grille();
		grid.setName("SCH102 Affiliate invoice");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.PERIOD, "Entry date", 1L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event ID", 2L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event name", 3L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event category ID", 4L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "Billing event category name", 5L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "INVOICE_CUSTOMER_ID", 6L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "INVOICE_CUSTOMER_NAME", 7L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "ACTIVITY_CUSTOMER_ID", 8L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "ACTIVITY_CUSTOMER_NAME", 9L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "AFFILIATE_CUSTOMER_ID", 10L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "AFFILIATE_CUSTOMER_NAME", 11L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "COLLECTION_METHOD", 12L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "AFFILIATE_TYPE", 13L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "CARD_ACCEPTOR_ID", 14L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "QUANTITY_OR_AMOUNT", 15L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "RATE", 16L));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.MEASURE, "TOTAL_CHARGE", 17L));
		return grid;
	}
	
	public static Grille buildSYS001FinancialMovements() {
		Grille grid = new Grille();
		grid.setName("SYS001 Financial movements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Bank Account N°", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Financial amount", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R1", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Account N°", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Counterpart Name", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "MT942 Message", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Entry date", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated advisement date", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net financial amount", 17L));
		return grid;
	}
	
	public static Grille buildSYS002PrefundingAdvisements() {
		Grille grid = new Grille();
		grid.setName("SYS002 Prefunding advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Reconciled amount R1", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Remaining amount R1", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member bank name", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R1", 13L));
		return grid;
	}
	
	public static Grille buildSYS003SettlementAdvisements() {
		Grille grid = new Grille();
		grid.setName("SYS003 Settlement advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement message", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique Report ID", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor ID", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 16L));
		return grid;
	}
	
	public static Grille buildSYS004MemberAdvisements() {
		Grille grid = new Grille();
		grid.setName("SYS004 Member advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member bank name", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement message", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R03", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R3", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique Report ID", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor ID", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 18L));
		return grid;
	}
	
	public static Grille buildSYS100Advisements() {
		Grille grid = new Grille();
		grid.setName("SYS100 Advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Processor date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement message", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Count", 9L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R01", 10L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R02", 11L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "R04", 12L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R1", 13L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R2", 14L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Reco date R4", 15L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Unique Report ID", 16L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Processor ID", 17L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Associated payment date", 18L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 19L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 20L));
		return grid;
	}
	
	
	
	public static Grille buildSYS105ListSchemeSchemePlatformAndSchemeProduct() {
		Grille grid = new Grille();
		grid.setName("SYS105 List scheme, scheme platform and scheme product");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme product type", 3L));
		return grid;
	}
	
	public static Grille buildSYS150ListOfAffiliateInMastercardInvoices() throws Throwable {
		Grille grid = new Grille();
		grid.setName("SYS150 List of affiliate in Mastercard invoices");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(MaterializedGilleFactory.buildMaterializedGrilles().get(28).getId(),DataSourceType.MATERIALIZED_GRID,DimensionType.ATTRIBUTE, "AFFILIATE_CUSTOMER_ID", 1L));
		return grid;
	}
	
	public static Grille buildSYS210RECTypeAndAccount() {
		Grille grid = new Grille();
		grid.setName("SYS210 REC Type and Account");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "REC Type ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "REC Account ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "REC Account name", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Data source", 4L));
		return grid;
	}
	
	public static Grille buildSYS230MAPerClientForMembershipVolumeFeeBilling() {
		Grille grid = new Grille();
		grid.setName("SYS230 MA per client for membership volume fee billing");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Client ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 6L));
		return grid;
	}
	
	public static Grille buildSYS235MATotalForMembershipVol() {
		Grille grid = new Grille();
		grid.setName("SYS235 MA total for membership vol");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE, DimensionType.ATTRIBUTE, "PML type", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Invoice pivot", 5L));
		return grid;
	}
	
	public static Grille buildSYS300MAPerBank() {
		Grille grid = new Grille();
		grid.setName("SYS300 MA per bank");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE, DimensionType.ATTRIBUTE, "PML ID", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.PERIOD, "Value date", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "PML type", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Advisement Account ID", 4L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 5L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme Platform ID", 6L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Member Bank ID", 7L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 8L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 9L));
		return grid;
	}
	
	public static Grille buildSYS320EOMMAEPB() {
		Grille grid = new Grille();
		grid.setName("SYS320 EOM MA EPB");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE, DimensionType.PERIOD, "Value date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 4L));
		return grid;
	}
	
	public static Grille buildSYS330M1097800() {
		Grille grid = new Grille();
		grid.setName("SYS330 M109 7800");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE, DimensionType.PERIOD, "Value date", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "Scheme ID", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.ATTRIBUTE, "D-C", 3L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "M109_Amount Reconciliation", 4L));
		return grid;
	}
	
	public static Grille buildTEST001SettlementAdvisement() {
		Grille grid = new Grille();
		grid.setName("TEST001 Settlement advisement");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE, DimensionType.ATTRIBUTE, "R02", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 3L));
		return grid;
	}
	
	public static Grille buildTEST002MemberAdvisement() {
		Grille grid = new Grille();
		grid.setName("TEST002 Member advisement");
		grid.setConsolidated(true);
		grid.setType(GrilleType.REPORT);
		grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grid.setModificationDate(new Timestamp(System.currentTimeMillis()));
		grid.setDescription(String.format("Report test building at %s", System.currentTimeMillis()));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE, DimensionType.ATTRIBUTE, "R02", 1L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Posting amount", 2L));
		grid.getColumnListChangeHandler().addNew(buildColumn(null,DataSourceType.UNIVERSE,DimensionType.MEASURE, "Net posting amount", 3L));
		return grid;
	}
	
	
	public static GrilleColumn buildColumn(Long dataSourceId,DataSourceType source,DimensionType type, String name, Long DimensionId) {
		GrilleColumn column = new GrilleColumn();
		column.setName(name);
		column.setDimensionId(DimensionId);
		column.setDimensionName(name);
		column.setType(type);
		column.setEditable(true);
		column.setShow(true);
		if (type == DimensionType.ATTRIBUTE) {
			column.setDefaultStringValue("New column");
		} else if (type == DimensionType.MEASURE) {
			column.setDefaultDecimalValue(new BigDecimal(2));
		} else {
			column.setDefaultDateValue(Date.from(Instant.now()));
		}
		return column;
	}

}
