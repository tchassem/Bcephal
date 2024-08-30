/**
 * 
 */
package com.moriset.bcephal.reporting;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;

import com.moriset.bcephal.domain.BGroup;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleSource;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.service.InitiationService;

/**
 * @author Pascal Dev
 */
public class ReportGridFactory {
	
	
	
	public static List<Grille> buildReportingGrilles(MaterializedGridService materializedGrilleService, InitiationService initiationService) throws Throwable{
		List<Grille> items = new ArrayList<>();
		items.add(buildLOG001Financialmovements(initiationService));
		items.add(buildLOG002Prefundingadvisements(initiationService));
		items.add(buildLOG003Settlementadvisements(initiationService));
		items.add(buildLOG004Memberadvisements(initiationService));
		items.add(buildREC002ISSUINGR02Recooftheday(initiationService));
		items.add(buildREC003ISSUINGR03Recooftheday(initiationService));
		items.add(buildREC004ISSUINGR04Recooftheday(initiationService));
		items.add(buildREC102ACQUIRINGR02Recooftheday(initiationService));
		items.add(buildREC103ACQUIRINGR03Recooftheday(initiationService));
		items.add(buildREC104ACQUIRINGR04Recooftheday(initiationService));
		items.add(buildSCH100PMLInvoice(materializedGrilleService));
		items.add(buildSCH102AffiliateInvoice(materializedGrilleService));
		items.add(buildSYS001Financialmovements(initiationService));
		items.add(buildSYS002Prefundingadvisements(initiationService));
		items.add(buildSYS003Settlementadvisements(initiationService));
		items.add(buildSYS004Memberadvisements(initiationService));
		items.add(buildSYS100Advisements(initiationService));
		items.add(buildSYS200IssuingMAvolumecurrentmonth(initiationService));
		items.add(buildSYS210IssuingMAvolumepermember(initiationService));
		items.add(buildSYS220IssuingMAvolumepermemberperscheme(initiationService));
		items.add(buildSYS320EOMMAEPB(initiationService));
		items.add(buildSYS330M1097800(initiationService));
		return items;
	}
	
	
	public static Grille buildLOG001Financialmovements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("LOG001 Financial movements");
		grid.setConsolidated(false);
		grid.setEditable(true);
		grid.setVisibleColumnCount(6);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.setStatus(GrilleStatus.LOADED);
		grid.setGroup(new BGroup());
		grid.setSourceType(GrilleSource.USER);
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Counterpart Account N°", "Counterpart Account N°", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Counterpart Name", "Counterpart Name", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "MT942 Message", "MT942 Message", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Bank Account N°", "Bank Account N°", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Financial amount", "Financial amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R01", "R01", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R03", "R03", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R04", "R04", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Reco date R3", "Reco date R3", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Reco date R1", "Reco date R1", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Reco date R4", "Reco date R4", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Entry date", "Entry date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Associated advisement date", "Associated advisement date", initiationService, null));
		return grid;
	}
	
	public static Grille buildLOG002Prefundingadvisements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("LOG002 Prefunding advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setVisibleColumnCount(6);
		grid.setType(GrilleType.REPORT);
		grid.setGroup(new BGroup());
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Advisement Account ID", "Advisement Account ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Member Bank ID", "Member Bank ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Reconciled amount R1", "Reconciled amount R1", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Remaining amount R1", "Remaining amount R1", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Member bank name", "Member bank name", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R01", "R01", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Associated payment date", "Associated payment date", initiationService, null));
		return grid;
	}
	
	public static Grille buildLOG003Settlementadvisements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("LOG003 Settlement advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setVisibleColumnCount(6);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Reco date R2", "Reco date R2", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Advisement Account ID", "Advisement Account ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R02", "R02", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R04", "R04", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Unique Report ID", "Unique Report ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Advisement message", "Advisement message", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Processor ID", "Processor ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Reco date R4", "Reco date R4", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Associated payment date", "Associated payment date", initiationService, null));
		return grid;
	}
	
	public static Grille buildLOG004Memberadvisements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("LOG004 Member advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setVisibleColumnCount(6);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Advisement Account ID", "Advisement Account ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(15, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Member Bank ID", "Member Bank ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Member bank name", "Member bank name", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Reco date R2", "Reco date R2", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R02", "R02", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Reco date R3", "Reco date R3", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Unique Report ID", "Unique Report ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Advisement message", "Advisement message", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Processor ID", "Processor ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "R03", "R03", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(16, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Associated payment date", "Associated payment date", initiationService, null));
		return grid;
	}
	
	public static Grille buildREC002ISSUINGR02Recooftheday(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("REC002 ISSUING R02 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildREC003ISSUINGR03Recooftheday(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("REC003 ISSUING R03 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildREC004ISSUINGR04Recooftheday(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("REC004 ISSUING R04 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildREC102ACQUIRINGR02Recooftheday(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("REC102 ACQUIRING R02 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildREC103ACQUIRINGR03Recooftheday(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("REC103 ACQUIRING R03 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildREC104ACQUIRINGR04Recooftheday(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("REC104 ACQUIRING R04 Reco of the day");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildSCH100PMLInvoice(MaterializedGridService materializedGrilleService) {
		Grille grid = new Grille();
		grid.setName("SCH100 PML Invoice");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		MaterializedGrid matGrid = materializedGrilleService.getByName("INP001 MCI PML Invoices");
		Assertions.assertThat(matGrid).isNotNull();
		grid.setDataSourceId(matGrid.getId());
		grid.setDataSourceName(matGrid.getName());
		grid.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Invoice date", "Invoice date", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Invoice number", "Invoice number", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event ID", "Billing event ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event name", "Billing event name", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(4, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event category ID", "Billing event category ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(5, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event category name", "Billing event category name", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(6, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "PML ID", "PML ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(7, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Entry date", "Entry date", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(8, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "DOCUMENT_TYPE", "DOCUMENT_TYPE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "BILLING_CYCLE_DATE", "BILLING_CYCLE_DATE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "INVOICE_ICA", "INVOICE_ICA", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "ACTIVITY_ICA", "ACTIVITY_ICA", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "BILLABLE_ICA", "BILLABLE_ICA", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "COLLECTION_METHOD", "COLLECTION_METHOD", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "PERIOD_START_DATE", "PERIOD_START_DATE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(15, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "PERIOD_END_DATE", "PERIOD_END_DATE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(16, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "AFFILIATE", "AFFILIATE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(17, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "UOM", "UOM", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(18, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "QUANTITY_AMOUNT", "QUANTITY_AMOUNT", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(19, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "RATE", "RATE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(20, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "CHARGE", "CHARGE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(21, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "TOTAL_CHARGE", "TOTAL_CHARGE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(22, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "TAX_CHARGE", "TAX_CHARGE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(23, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "VAT_RATE", "VAT_RATE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(24, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "VAT_CHARGE", "VAT_CHARGE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(25, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "SBF_EXPLANATORY_TEXT", "SBF_EXPLANATORY_TEXT", null, matGrid));
		return grid;
	}
	
	public static Grille buildSCH102AffiliateInvoice(MaterializedGridService materializedGrilleService) {
		Grille grid = new Grille();
		grid.setName("SCH102 Affiliate invoice");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		MaterializedGrid matGrid = materializedGrilleService.getByName("INP002 MCI affiliate invoice");
		Assertions.assertThat(matGrid).isNotNull();
		grid.setDataSourceId(matGrid.getId());
		grid.setDataSourceName(matGrid.getName());
		grid.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.PERIOD, "Invoice date", "Invoice date", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event ID", "Billing event ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event name", "Billing event name", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event category ID", "Billing event category ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Billing event category name", "Billing event category name", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "INVOICE_CUSTOMER_ID", "INVOICE_CUSTOMER_ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "INVOICE_CUSTOMER_NAME", "INVOICE_CUSTOMER_NAME", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "ACTIVITY_CUSTOMER_ID", "ACTIVITY_CUSTOMER_ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "ACTIVITY_CUSTOMER_NAME", "ACTIVITY_CUSTOMER_NAME", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "AFFILIATE_CUSTOMER_ID", "AFFILIATE_CUSTOMER_ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "AFFILIATE_CUSTOMER_NAME", "AFFILIATE_CUSTOMER_NAME", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "COLLECTION_METHOD", "COLLECTION_METHOD", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "AFFILIATE_TYPE", "AFFILIATE_TYPE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "CARD_ACCEPTOR_ID", "CARD_ACCEPTOR_ID", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "QUANTITY_OR_AMOUNT", "QUANTITY_OR_AMOUNT", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(15, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "RATE", "RATE", null, matGrid));
		grid.getColumnListChangeHandler()
		.addNew(buildColumn(16, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "TOTAL_CHARGE", "TOTAL_CHARGE", null, matGrid));
		return grid;
	}
	
	public static Grille buildSYS001Financialmovements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setGroup(new BGroup());
		grid.setName("SYS001 Financial movements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Bank Account N°", "Bank Account N°", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Financial amount", "Financial amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R01", "R01", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R03", "R03", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R04", "R04", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R1", "Reco date R1", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R3", "Reco date R3", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R4", "Reco date R4", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Counterpart Account N°", "Counterpart Account N°", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Counterpart Name", "Counterpart Name", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "MT942 Message", "MT942 Message", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Entry date", "Entry date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Associated advisement date", "Associated advisement date", initiationService, null));
		return grid;
	}	
	
	public static Grille buildSYS002Prefundingadvisements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS002 Prefunding advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Advisement Account ID", "Advisement Account ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Member Bank ID", "Member Bank ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R01", "R01", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Reconciled amount R1", "Reconciled amount R1", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Remaining amount R1", "Remaining amount R1", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Member bank name", "Member bank name", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Associated payment date", "Associated payment date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R1", "Reco date R1", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS003Settlementadvisements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS003 Settlement advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Advisement Account ID", "Advisement Account ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Advisement message", "Advisement message", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R02", "R02", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R04", "R04", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R2", "Reco date R2", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R4", "Reco date R4", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Unique Report ID", "Unique Report ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Processor ID", "Processor ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Associated payment date", "Associated payment date", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS004Memberadvisements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS004 Member advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Advisement Account ID", "Advisement Account ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Member bank name", "Member bank name", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Advisement message", "Advisement message", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R02", "R02", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "R03", "R03", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R2", "Reco date R2", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(12, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Reco date R3", "Reco date R3", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(13, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Unique Report ID", "Unique Report ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(14, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Processor ID", "Processor ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(15, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Member Bank ID", "Member Bank ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(16, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Associated payment date", "Associated payment date", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS100Advisements(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS100 Advisements");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Advisement Account ID", "Advisement Account ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "Scheme Platform ID", "Scheme Platform ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Count", "Count", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(4, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(5, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(6, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.MEASURE, "Remaining amount R2", "Remaining amount R2", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(7, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.MEASURE, "Remaining amount R3", "Remaining amount R3", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(8, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.MEASURE, "Remaining amount R4", "Remaining amount R4", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(9, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.MEASURE, "Remaining amount R5", "Remaining amount R5", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(10, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.MEASURE, "Remaining amount R7", "Remaining amount R7", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(11, 200, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS200IssuingMAvolumecurrentmonth(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS200 Issuing MA volume current month");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS210IssuingMAvolumepermember(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS210 Issuing MA volume per member");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Member Bank ID", "Member Bank ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS220IssuingMAvolumepermemberperscheme(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS220 Issuing MA volume per member per scheme");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Member Bank ID", "Member Bank ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Member bank name", "Member bank name", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS320EOMMAEPB(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS320 EOM MA EPB");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.MEASURE, "Posting amount", "Posting amount", initiationService, null));
		return grid;
	}
	
	public static Grille buildSYS330M1097800(InitiationService initiationService) {
		Grille grid = new Grille();
		grid.setName("SYS330 M109 7800");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		grid.getColumnListChangeHandler().addNew(buildColumn(0, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.PERIOD, "Value date", "Value date", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(1, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.ATTRIBUTE, "Scheme ID", "Scheme ID", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(2, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER, DimensionType.ATTRIBUTE, "D-C", "D-C", initiationService, null));
		grid.getColumnListChangeHandler().addNew(buildColumn(3, null, true, HorizontalAlignment.Center, GrilleColumnCategory.USER,DimensionType.MEASURE, "M109_Amount Reconciliation", "M109_Amount Reconciliation", initiationService, null));
		return grid;
	}
	
	public static Grille buildGrille() {
		Grille grid = new Grille();
		grid.setName("report grille test");
		grid.setConsolidated(true);
		grid.setVisibleInShortcut(true);
		grid.setType(GrilleType.REPORT);
		return grid;
	}
	
	
	public static GrilleColumn buildColumn(int position, Integer width, boolean editable, HorizontalAlignment alignment, GrilleColumnCategory category, DimensionType type, 
			String name, String dimensionName, InitiationService initiationService, MaterializedGrid materializedGrid) {
		GrilleColumn column = new GrilleColumn();
		column.setName(name);
		column.setAlignment(alignment);
		column.setCategory(category);	
		column.setEditable(editable);
		column.setShow(true);
		column.setPosition(position);
		column.setWidth(width);
		if(initiationService != null) {
			Dimension dimension = initiationService.getDimension(type, dimensionName, false, null, null);
			Assertions.assertThat(dimension).isNotNull();
			column.setType(type);
			column.setDimensionId((Long) dimension.getId());
			column.setDimensionName(dimensionName);
		}
		else {
			MaterializedGridColumn columnMat = materializedGrid.getColumnByDimensionAndName(type, dimensionName);
			Assertions.assertThat(columnMat).isNotNull();
			column.setType(type);
			column.setDimensionId((Long) columnMat.getId());
			column.setDimensionName(dimensionName);
		}
		return column;
	}
}
