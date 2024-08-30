package com.moriset.bcephal.sourcing.grid;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.service.InitiationService;


public class InputGrilleFactory {	
	
	public static List<Grille> buildGrilles(InitiationService initiationService) throws Exception {
		List<Grille> items = new ArrayList<>();
		items.add(buildBNK001EPBFundingAccount(initiationService));
		items.add(buildCLI100ClientIDList(initiationService));
		items.add(buildINP001IssuingPF(initiationService));
		items.add(buildINP002IssuingSA(initiationService));
		items.add(buildINP003IssuingMA(initiationService));
		items.add(buildINP005IssuingREC(initiationService));
		items.add(buildINP005RECMaestro(initiationService));
		items.add(buildINP007IssuingM109(initiationService));
		items.add(buildINP102AcquiringSA(initiationService));
		items.add(buildINP103AcquiringMA(initiationService));
		items.add(buildINP105AcquiringREC(initiationService));
		items.add(buildMAP001MemberBankID(initiationService));
		items.add(buildMAP301RECAccountID(initiationService));
		items.add(buildORD100OrderonthlyFee(initiationService));
		items.add(buildPRI100MonthlyFee(initiationService));
		items.add(buildPRI110Volumefeepricing(initiationService));
		items.add(buildSYS001ValueInitialization(initiationService));
		
		return items;
	}

	public static Grille buildBNK001EPBFundingAccount(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-05-31 07:13:02"));
		grille.setModificationDate(Timestamp.valueOf("2023-06-05 16:03:32"));
		grille.setName("BNK001 EPB funding account");
		grille.getColumnListChangeHandler().addNew(buildColumn("Value date", DimensionType.PERIOD, "Value date", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Financial amount", DimensionType.MEASURE, "Financial amount", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Account n°", DimensionType.ATTRIBUTE, "Bank Account N°", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Statement n°", DimensionType.ATTRIBUTE, "Statement N°", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Sequence n°", DimensionType.ATTRIBUTE, "Sequence N°", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Date/time indication", DimensionType.PERIOD, "MT942 Entry Date", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Entry Date", DimensionType.PERIOD, "Entry date", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SwiftTxCode", DimensionType.ATTRIBUTE, "SwiftTxCode", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Transaction N°", DimensionType.ATTRIBUTE, "Transaction N°", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("MT942 Message", DimensionType.ATTRIBUTE, "MT942 Message", 17, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 18, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("MT942 TYPE", DimensionType.ATTRIBUTE, "MT942 Type", 19, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("MT942 CODE", DimensionType.ATTRIBUTE, "MT942 Code", 20, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Counterpart name", DimensionType.ATTRIBUTE, "Counterpart Name", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Counterpart Account N°", DimensionType.ATTRIBUTE, "Counterpart Account N°", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SCHEME ID", DimensionType.ATTRIBUTE, "Scheme ID", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SCHEME PLATFORM ID", DimensionType.ATTRIBUTE, "Scheme Platform ID", 15, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Account ID", DimensionType.ATTRIBUTE, "Account ID", 16, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Ultimate Beneficiary Name", DimensionType.ATTRIBUTE, "Ultimate Beneficiary Name", 21, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Ultimate Beneficiary ID", DimensionType.ATTRIBUTE, "Ultimate Beneficiary ID", 22, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("eWL REPORT DATE", DimensionType.PERIOD, "eWL report date", 23, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D-C", DimensionType.ATTRIBUTE, "D-C", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SCHEME CYCLE", DimensionType.ATTRIBUTE, "Scheme Cycle", 24, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Sponsor_unique_report_ID", DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 25, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("processing_date", DimensionType.PERIOD, "Processor date", 26, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 27, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 28, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 29, initiationService));
		return grille;
	}
	
	public static Grille buildCLI100ClientIDList(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-04-19 08:13:29"));
		grille.setModificationDate(Timestamp.valueOf("2023-04-19 08:14:10"));
		grille.setName("CLI100 Client ID list");
		grille.getColumnListChangeHandler().addNew(buildColumn("Client ID", DimensionType.ATTRIBUTE, "Client ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Broker ID", DimensionType.ATTRIBUTE, "Broker ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank Name NL", DimensionType.ATTRIBUTE, "Member bank name", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Client name", DimensionType.ATTRIBUTE, "Client name", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Legal form", DimensionType.ATTRIBUTE, "Legal form", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Email", DimensionType.ATTRIBUTE, "Email", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Street number and box", DimensionType.ATTRIBUTE, "Street, number and box", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Postal code", DimensionType.ATTRIBUTE, "Postal code", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("City", DimensionType.ATTRIBUTE, "City", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Country", DimensionType.ATTRIBUTE, "Country", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("VAT number", DimensionType.ATTRIBUTE, "VAT number", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Language", DimensionType.ATTRIBUTE, "Language", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Client status", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Client contact", DimensionType.ATTRIBUTE, "Client contact", 14, initiationService));
		return grille;
	}
	
	public static Grille buildINP001IssuingPF(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(true);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-03-27 08:11:59"));
		grille.setModificationDate(Timestamp.valueOf("2023-03-30 08:35:48"));
		grille.setName("INP001 Issuing PF");
		grille.getColumnListChangeHandler().addNew(buildColumn("Advisment N°", DimensionType.ATTRIBUTE, "Advisement nbr", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Advisement Account ID", DimensionType.ATTRIBUTE, "Advisement Account ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme PML ID", DimensionType.ATTRIBUTE, "Scheme PML ID", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Currency ID", DimensionType.ATTRIBUTE, "Currency ID", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D-C", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Value Date", DimensionType.ATTRIBUTE, "Value date", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Advisement message", DimensionType.ATTRIBUTE, "Advisement message", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, "PML type", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting Amount", DimensionType.ATTRIBUTE, "Posting amount", 7, initiationService));
		return grille;
	}
	
	public static Grille buildINP002IssuingSA(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-05-30 15:35:12"));
		grille.setModificationDate(Timestamp.valueOf("2023-06-01 09:28:47"));
		grille.setName("INP002 Issuing SA");
		grille.getColumnListChangeHandler().addNew(buildColumn(" Unique report ID", DimensionType.ATTRIBUTE, "Unique Report ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Advisement ID", DimensionType.ATTRIBUTE, "Advisement ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Sponsor Unique Report ID", DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Operation ID", DimensionType.ATTRIBUTE, "Operation ID", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Account ID", DimensionType.ATTRIBUTE, "Account ID", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D-C", DimensionType.ATTRIBUTE, "D-C", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SCHEME Platform ID ", DimensionType.ATTRIBUTE, "Scheme Platform ID", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Currency ID", DimensionType.ATTRIBUTE, "Currency ID", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting amount", DimensionType.ATTRIBUTE, "Posting amount", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Message", DimensionType.ATTRIBUTE, "Advisement message", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme report date", DimensionType.PERIOD, "Scheme Date", 16, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 17, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme PML ID", DimensionType.ATTRIBUTE, "Scheme PML ID", 18, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 19, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Name", DimensionType.ATTRIBUTE, "Processor Name", 20, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Native value date", DimensionType.PERIOD, "Native Value Date", 21, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 22, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 23, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 24, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Value date", DimensionType.PERIOD, "Value date", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor date", DimensionType.PERIOD, "Processor date", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Report time", DimensionType.ATTRIBUTE, "Processor Report Time", 15, initiationService));
		return grille;
	}
	
	public static Grille buildINP003IssuingMA(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-05-30 09:53:51"));
		grille.setModificationDate(Timestamp.valueOf("2023-05-31 07:56:36"));
		grille.setName("INP003 Issuing MA");
		grille.getColumnListChangeHandler().addNew(buildColumn("D-C", DimensionType.ATTRIBUTE, "D-C", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Unique report ID", DimensionType.ATTRIBUTE, "Unique Report ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Advisement ID", DimensionType.ATTRIBUTE, "Advisement ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Sponsor Unique Report ID", DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Operation ID", DimensionType.ATTRIBUTE, "Operation ID", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Account ID", DimensionType.ATTRIBUTE, "Account ID", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme value date", DimensionType.PERIOD, "Value date", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SCHEME Platform ID ", DimensionType.ATTRIBUTE, "Scheme Platform ID", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Currency ID", DimensionType.ATTRIBUTE, "Currency ID", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting amount", DimensionType.ATTRIBUTE, "Posting amount", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Message", DimensionType.ATTRIBUTE, "Advisement message", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Worldline Report Date", DimensionType.PERIOD, "Processor date", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Wordline report time", DimensionType.ATTRIBUTE, "Processor Report Time", 15, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme report date", DimensionType.PERIOD, "Scheme Date", 16, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 17, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme PML ID", DimensionType.ATTRIBUTE, "Scheme PML ID", 18, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 19, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Name", DimensionType.ATTRIBUTE, "Processor Name", 20, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Native value date", DimensionType.PERIOD, "Native Value Date", 21, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 22, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 23, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 24, initiationService));
		return grille;
	}
	
	public static Grille buildINP005IssuingREC(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-05-30 15:38:59"));
		grille.setModificationDate(Timestamp.valueOf("2023-06-01 20:49:56"));
		grille.setName("INP005 Issuing REC");
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme Platform ID", DimensionType.ATTRIBUTE, "Scheme Platform ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Date", DimensionType.PERIOD, "Processor date", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("REC Account ID", DimensionType.ATTRIBUTE, "REC Account ID", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting amount", DimensionType.MEASURE, "Posting amount", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D/C", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Value date", DimensionType.PERIOD, "Value date", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("REC Type Name", DimensionType.ATTRIBUTE, "REC Type Name", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, "PML type", 13, initiationService));
		return grille;
	}
	
	public static Grille buildINP005RECMaestro(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-06-01 19:37:18"));
		grille.setModificationDate(Timestamp.valueOf("2023-06-01 19:37:18"));
		grille.setName("INP005 REC Maestro");
		grille.getColumnListChangeHandler().addNew(buildColumn("Rec Type ID", DimensionType.ATTRIBUTE, "REC Type ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme Date", DimensionType.PERIOD, "Scheme Date", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme Platform ID", DimensionType.ATTRIBUTE, "Scheme Platform ID", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Date", DimensionType.PERIOD, "Processor date", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("TPPN Period", DimensionType.ATTRIBUTE, "TPPN", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("REC Account ID", DimensionType.ATTRIBUTE, "REC Account ID", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting amount", DimensionType.MEASURE, "Posting amount", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D-C", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Value date", DimensionType.PERIOD, "Value date", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("REC Type Name", DimensionType.ATTRIBUTE, "REC Type Name", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 15, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 16, initiationService));
		return grille;
	}
	
	public static Grille buildINP007IssuingM109(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-03-27 08:16:15"));
		grille.setModificationDate(Timestamp.valueOf("2023-03-28 12:16:12"));
		grille.setName("INP007 Issuing M109");
		grille.getColumnListChangeHandler().addNew(buildColumn("﻿M109_Sequence Number Record", DimensionType.ATTRIBUTE, "M109_Sequence Number Record", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Member ID issuer", DimensionType.ATTRIBUTE, "M109_Member ID issuer", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Date", DimensionType.PERIOD, "Processor date", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Business Cycle", DimensionType.ATTRIBUTE, "M109_Business Cycle", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Reference Number", DimensionType.ATTRIBUTE, "M109_Reference Number", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Type Message", DimensionType.ATTRIBUTE, "M109_Type Message", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Reversal Indicator", DimensionType.ATTRIBUTE, "M109_Reversal Indicator", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Status Transaction", DimensionType.ATTRIBUTE, "M109_Status Transaction", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Bank Identification", DimensionType.ATTRIBUTE, "M109_Bank Identification", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Transaction Code Routing", DimensionType.ATTRIBUTE, "M109_Transaction Code Routing", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Currency Code Reconciliation", DimensionType.ATTRIBUTE, "M109_Currency Code Reconciliation", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D-C", DimensionType.ATTRIBUTE, "D-C", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Amount Reconciliation", DimensionType.MEASURE, "M109_Amount Reconciliation", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("M109_Reason Code", DimensionType.ATTRIBUTE, "M109_Reason Code", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Broker ID", DimensionType.ATTRIBUTE, "Broker ID", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 15, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 16, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme Platform ID", DimensionType.ATTRIBUTE, "Scheme Platform ID", 17, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme PML ID", DimensionType.ATTRIBUTE, "Scheme PML ID", 18, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme Cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 19, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Value Date", DimensionType.PERIOD, "Value date", 20, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, "PML type", 21, initiationService));
		return grille;
	}
	
	public static Grille buildINP102AcquiringSA(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-05-30 15:09:23"));
		grille.setModificationDate(Timestamp.valueOf("2023-05-30 15:09:23"));
		grille.setName("INP102 Acquiring SA");
		grille.getColumnListChangeHandler().addNew(buildColumn(" Unique report ID", DimensionType.ATTRIBUTE, "Unique Report ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Advisement ID", DimensionType.ATTRIBUTE, "Advisement ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Sponsor Unique Report ID", DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Operation ID", DimensionType.ATTRIBUTE, "Operation ID", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Account ID", DimensionType.ATTRIBUTE, "Account ID", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme value date", DimensionType.PERIOD, "Value date", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SCHEME Platform ID ", DimensionType.ATTRIBUTE, "Scheme Platform ID", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Currency ID", DimensionType.ATTRIBUTE, "Currency ID", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D/C", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting amount", DimensionType.MEASURE, "Posting amount", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Message", DimensionType.ATTRIBUTE, "Advisement message", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Worldline Report Date", DimensionType.PERIOD, "Processor date", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Wordline report time", DimensionType.ATTRIBUTE, "Processor Report Time", 15, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme report date", DimensionType.PERIOD, "Scheme Date", 16, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 17, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme PML ID", DimensionType.ATTRIBUTE, "Scheme PML ID", 18, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 19, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Name", DimensionType.ATTRIBUTE, "Processor Name", 20, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Native value date", DimensionType.PERIOD, "Native Value Date", 21, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 22, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 23, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 24, initiationService));
		return grille;
	}
	public static Grille buildINP103AcquiringMA(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-05-30 15:05:39"));
		grille.setModificationDate(Timestamp.valueOf("2023-05-30 15:06:31"));
		grille.setName("INP103 Acquiring MA");
		grille.getColumnListChangeHandler().addNew(buildColumn(" Unique report ID", DimensionType.ATTRIBUTE, "Unique Report ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Advisement ID", DimensionType.ATTRIBUTE, "Advisement ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Sponsor Unique Report ID", DimensionType.ATTRIBUTE, "Sponsor Unique Report ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Operation ID", DimensionType.ATTRIBUTE, "Operation ID", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Account ID", DimensionType.ATTRIBUTE, "Account ID", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme value date", DimensionType.PERIOD, "Value date", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML ID", DimensionType.ATTRIBUTE, "PML ID", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("SCHEME Platform ID ", DimensionType.ATTRIBUTE, "Scheme Platform ID", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Currency ID", DimensionType.ATTRIBUTE, "Currency ID", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D/C", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting amount", DimensionType.MEASURE, "Posting amount", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Message", DimensionType.ATTRIBUTE, "Advisement message", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Worldline Report Date", DimensionType.PERIOD, "Processor date", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Wordline report time", DimensionType.ATTRIBUTE, "Processor Report Time", 15, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme report date", DimensionType.PERIOD, "Scheme Date", 16, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 17, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme PML ID", DimensionType.ATTRIBUTE, "Scheme PML ID", 18, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 19, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Name", DimensionType.ATTRIBUTE, "Processor Name", 20, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Native value date", DimensionType.PERIOD, "Native Value Date", 21, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 22, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 23, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 24, initiationService));
		return grille;
	}
	
	public static Grille buildINP105AcquiringREC(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-05-31 07:43:44"));
		grille.setModificationDate(Timestamp.valueOf("2023-05-31 07:43:44"));
		grille.setName("INP105 Acquiring REC");
		grille.getColumnListChangeHandler().addNew(buildColumn("Rec Type ID", DimensionType.ATTRIBUTE, "REC Type ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme Date", DimensionType.PERIOD, "Scheme Date", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme cycle", DimensionType.ATTRIBUTE, "Scheme Cycle", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme Platform ID", DimensionType.ATTRIBUTE, "Scheme Platform ID", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Processor Date", DimensionType.PERIOD, "Processor date", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("TPPN Period", DimensionType.ATTRIBUTE, "TPPN", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("REC Account ID", DimensionType.ATTRIBUTE, "REC Account ID", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Posting amount", DimensionType.MEASURE, "Posting amount", 9, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("D/C", DimensionType.ATTRIBUTE, "D/C", 10, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Brocker ID", DimensionType.ATTRIBUTE, "Broker ID", 11, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Value date", DimensionType.PERIOD, "Value date", 12, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 13, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("REC Type Name", DimensionType.ATTRIBUTE, "REC Type Name", 14, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Product Type", DimensionType.ATTRIBUTE, "Product type", 15, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Check", DimensionType.MEASURE, "Check", 16, initiationService));
		return grille;
	}
	
	public static Grille buildMAP001MemberBankID(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-03-25 07:49:37"));
		grille.setModificationDate(Timestamp.valueOf("2023-04-16 06:42:23"));
		grille.setName("MAP001 Member Bank ID");
		grille.getColumnListChangeHandler().addNew(buildColumn("﻿Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Broker ID", DimensionType.ATTRIBUTE, "Broker ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank Name NL", DimensionType.ATTRIBUTE, "Member bank name", 2, initiationService));
		return grille;
	}
	
	public static Grille buildMAP301RECAccountID(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-03-25 07:49:37"));
		grille.setModificationDate(Timestamp.valueOf("2023-04-16 06:42:23"));
		grille.setName("MAP301 REC Account ID");
		grille.getColumnListChangeHandler().addNew(buildColumn("﻿REC Account ID", DimensionType.ATTRIBUTE, "REC Account ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("REC Account Name", DimensionType.ATTRIBUTE, "REC Account name", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Rec Type ID", DimensionType.ATTRIBUTE, "REC Type Name", 2, initiationService));
		return grille;
	}
	public static Grille buildORD100OrderonthlyFee(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(false);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-04-19 11:43:26"));
		grille.setModificationDate(Timestamp.valueOf("2023-04-28 07:41:25"));
		grille.setName("ORD100 Order - Monthly fee");
		grille.getColumnListChangeHandler().addNew(buildColumn("Entry date", DimensionType.PERIOD, "Entry date", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("﻿Client ID", DimensionType.ATTRIBUTE, "Client ID", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank ID", DimensionType.ATTRIBUTE, "Member Bank ID", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Member Bank Name NL", DimensionType.ATTRIBUTE, "Member bank name", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Order status", DimensionType.ATTRIBUTE, "Order status", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Fee type", DimensionType.ATTRIBUTE, "Fee type", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Data source", DimensionType.ATTRIBUTE, "Data source", 7, initiationService));
		return grille;
	}
	
	public static Grille buildPRI100MonthlyFee(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(true);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-04-19 10:52:09"));
		grille.setModificationDate(Timestamp.valueOf("2023-04-19 11:48:17"));
		grille.setName("PRI100 Monthly fee");
		grille.getColumnListChangeHandler().addNew(buildColumn("Fee amount", DimensionType.MEASURE, "Fee amount", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Scheme ID", DimensionType.ATTRIBUTE, "Scheme ID", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML Type", DimensionType.ATTRIBUTE, "PML type", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Pricing status", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Pricing end date", DimensionType.PERIOD, "Pricing end date", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Fee type", DimensionType.ATTRIBUTE, "Fee type", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Data source", DimensionType.ATTRIBUTE, "Data source", 6, initiationService));
		return grille;
	}
	
	public static Grille buildPRI110Volumefeepricing(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(true);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-04-19 10:14:49"));
		grille.setModificationDate(Timestamp.valueOf("2023-04-19 10:29:26"));
		grille.setName("PRI110 Volume fee pricing");
		grille.getColumnListChangeHandler().addNew(buildColumn("Volume fee type", DimensionType.ATTRIBUTE, "Volume fee type", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Range", DimensionType.ATTRIBUTE, "Volume fee range", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Fee amount", DimensionType.MEASURE, "Fee amount", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Minimum fee amount", DimensionType.MEASURE, "Minimum fee amount", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Fee type", DimensionType.ATTRIBUTE, "Fee type", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Range - Minimum volume", DimensionType.MEASURE, "Range minimum amount", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Range - Maximum volume", DimensionType.MEASURE, "Range maximum amount", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Status", DimensionType.ATTRIBUTE, "Pricing status", 7, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Pricing start date", DimensionType.PERIOD, "Pricing start date", 8, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Pricing end date", DimensionType.PERIOD, "Pricing end date", 9, initiationService));
		return grille;
	}
	
	public static Grille buildSYS001ValueInitialization(InitiationService initiationService) {
		Grille grille = new Grille();
		grille.setEditable(true);
		grille.setType(GrilleType.INPUT);
		grille.setVisibleInShortcut(true);
		grille.setCreationDate(Timestamp.valueOf("2023-03-24 07:12:31"));
		grille.setModificationDate(Timestamp.valueOf("2023-03-28 12:05:55"));
		grille.setName("SYS001 Value initialization");
		grille.getColumnListChangeHandler().addNew(buildColumn("Neu01", DimensionType.ATTRIBUTE, "Neu01", 0, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Neu02", DimensionType.ATTRIBUTE, "Neu02", 1, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Neu03", DimensionType.ATTRIBUTE, "Neu03", 2, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Neu04", DimensionType.MEASURE, "Neu04", 3, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Neu05", DimensionType.ATTRIBUTE, "Neu05", 4, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Neu06", DimensionType.ATTRIBUTE, "Neu06", 5, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("Neu07", DimensionType.ATTRIBUTE, "Neu07", 6, initiationService));
		grille.getColumnListChangeHandler().addNew(buildColumn("PML type", DimensionType.ATTRIBUTE, "PML type", 7, initiationService));
		return grille;
	}
	
	private static GrilleColumn buildColumn(String name, DimensionType type, String dimensionName, int position, InitiationService initiationService) {
		GrilleColumn column = new GrilleColumn();
		column.setName(name);
		column.setPosition(position);
		Dimension dimension = initiationService.getDimension(type, dimensionName, false, null, null);
		if(dimension != null) {
			column.setType(type);
			column.setDimensionId((Long) dimension.getId());
			column.setDimensionName(dimensionName);
		}
		return column;
	}
	
	
}
