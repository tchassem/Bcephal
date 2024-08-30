package com.moriset.bcephal.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinColumnCategory;
import com.moriset.bcephal.grid.domain.JoinColumnField;
import com.moriset.bcephal.grid.domain.JoinColumnProperties;
import com.moriset.bcephal.grid.domain.JoinColumnType;
import com.moriset.bcephal.grid.domain.JoinCondition;
import com.moriset.bcephal.grid.domain.JoinConditionItem;
import com.moriset.bcephal.grid.domain.JoinConditionItemType;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.JoinKey;
import com.moriset.bcephal.grid.domain.JoinPublicationMethod;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.service.InitiationService;

public class JoinFactory {
	
	@Autowired
	MaterializedGridService materializedGridService;
	
  public static List<Join> buildJoinds(GrilleService gridService,MaterializedGridService materializedGrilleService,InitiationService initiationService) throws Throwable{
	  List<Join> joins = new ArrayList<>();
	  joins.add(buildBILL100BillingEventGenerationMonthlyFee(gridService, initiationService));
	  joins.add(buildBILL110BillingEventGenerationVolumeFee(gridService, initiationService));
	  joins.add(buildBILL120BillingEventGenerationPenaltyFee(gridService, initiationService));
	  joins.add(buildBILL200BillingEventGenerationCommonSchemeFee(materializedGrilleService, initiationService));
	  joins.add(buildBILL210BillingEventGenerationDirectSchemeFee(materializedGrilleService, initiationService));
	  joins.add(buildSYS400InputForEom(materializedGrilleService, initiationService));
	  joins.add(buildSYS500RecoR02(materializedGrilleService, initiationService));
	  joins.add(buildSYS502RecoR03(materializedGrilleService, initiationService));
	  joins.add(buildSYS504RecoR04(materializedGrilleService, initiationService));
	  joins.add(buildSYS504RECOR04SAVES(materializedGrilleService, initiationService));
	  joins.add(buildGPH001PendingPerRecoTypePerSchemePlatform(materializedGrilleService, initiationService));
	  joins.add(buildTet(gridService, initiationService));
	  
	  return joins;
  }
 
	public static Join buildBILL100BillingEventGenerationMonthlyFee(GrilleService gridService,
			InitiationService initiationService) {
		Join join = new Join();

		Grille grille1 = buildInputGridByName("ORD100 Order - Monthly fee", gridService);
		Grille grille2 = buildInputGridByName("PRI100 Monthly fee", gridService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		grille2.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "ORD100 Order - Monthly fee", JoinGridType.GRID, grille1.getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(1, "PRI100 Monthly fee", JoinGridType.GRID, grille2.getId()));
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 1, JoinGridType.GRID, grille1.getId(), 1046L, 100L,
				JoinGridType.GRID, grille2.getId(), 1013L, 100L));
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 0, JoinGridType.GRID, grille1.getId(), 1045L, 68L,
				JoinGridType.GRID, grille2.getId(), 1012L, 68L));
		join.setName("BILL100 Billing event generation - Monthly fee");
		join.setAddPublicationRunNbr(true);
		join.setCreationDate(Timestamp.valueOf("2023-04-19 11:09:00"));
		join.setModificationDate(Timestamp.valueOf("2023-04-28 08:11:59"));
		join.setRefreshGridsBeforePublication(true);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridId(86L);
		join.setPublicationGridName("");
		join.setRowType(GrilleRowType.NOT_RECONCILIATED);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 0, DimensionType.ATTRIBUTE,
						"﻿Client ID", "Client ID", "Client ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "﻿Client ID").getId(),
						grille1.getColumns().get(0).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 8,
				DimensionType.ATTRIBUTE, "Column1", "Billing event type", "Billing event type", null,
				initiationService.getAttributeRepository().findByName("Billing event type").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Invoice"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 9,
				DimensionType.ATTRIBUTE, "Column1", "Billing event category", "Billing event category", null,
				initiationService.getAttributeRepository().findByName("Billing event category").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Issuing"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 10,
				DimensionType.ATTRIBUTE, "Column1", "Billing group 1", "Billing group 1", null,
				initiationService.getAttributeRepository().findByName("Billing group 1").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Monthly fee"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 11, DimensionType.ATTRIBUTE,
						"Column1", "Billing group 2", "Billing group 2", null,
						initiationService.getAttributeRepository().findByName("Billing group 2").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 12,
				DimensionType.ATTRIBUTE, "Column1", "Billing event status", "Billing event status", null,
				initiationService.getAttributeRepository().findByName("Billing event status").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Draft"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 0, DimensionType.PERIOD,
						"Column1", "Billing event date", "Billing event date", null,
						initiationService.getAttributeRepository().findByName("Billing event date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.BEGIN_MONTH, Timestamp.valueOf("2023-04-01"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 13, DimensionType.ATTRIBUTE,
						"﻿Client ID", "Client ID", "Client ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "﻿Client ID").getId(),
						grille1.getColumns().get(0).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 14, DimensionType.MEASURE,
						"Column1", "Billing amount", "Billing amount", null,
						initiationService.getAttributeRepository().findByName("Billing amount").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 16, DimensionType.MEASURE,
						"Column1", "Billing driver", "Billing driver", null,
						initiationService.getAttributeRepository().findByName("Billing driver").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 15, DimensionType.MEASURE,
						"Column1", "Unit cost", "Unit cost", null,
						initiationService.getAttributeRepository().findByName("Unit cost").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 17, DimensionType.MEASURE,
						"Column1", "VAT rate", "VAT rate", null,
						initiationService.getAttributeRepository().findByName("VAT rate").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 18,
				DimensionType.ATTRIBUTE, "Column1", "Billing event description", "Billing event description", null,
				initiationService.getAttributeRepository().findByName("Billing event description").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Monthly fee"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 19, DimensionType.PERIOD,
						"Column1", "Entry date", "Entry date", null,
						initiationService.getAttributeRepository().findByName("Entry date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.TODAY, Timestamp.valueOf("2023-04-01"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 1, DimensionType.ATTRIBUTE,
						"Member Bank ID", "Member Bank ID", "Member Bank ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Member Bank ID").getId(),
						grille1.getColumns().get(1).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 3, DimensionType.ATTRIBUTE,
						"Scheme ID", "Scheme ID", "Scheme ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Scheme ID").getId(),
						grille1.getColumns().get(3).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 2, DimensionType.ATTRIBUTE,
						"Member Bank Name NL", "Member bank name", "Member Bank Name NL",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Member Bank Name NL").getId(),
						grille1.getColumns().get(2).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 4, DimensionType.ATTRIBUTE,
						"PML Type", "PML Type", "PML Type",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "PML Type").getId(),
						grille1.getColumns().get(4).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 5, DimensionType.ATTRIBUTE,
						"Order status", "Order status", "Order status",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Order status").getId(),
						grille1.getColumns().get(5).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 7, DimensionType.MEASURE,
						"Fee amount", "Fee amount", "Fee amount",
						grille2.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Fee amount").getId(),
						grille2.getColumns().get(2).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 6, DimensionType.ATTRIBUTE,
						"Fee type", "Fee type", "Order type",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Fee type").getId(),
						grille1.getColumns().get(6).getDimensionId(), null));
		join.getConditionListChangeHandler()
				.addNew(buildJoinCondition(
						buildJoinConditionItem(JoinGridType.GRID, DimensionType.ATTRIBUTE, "Order status", null,
								grille1.getId(), null, grille1.getColumns().get(5).getDimensionId(),
								grille1.getColumns().get(5).getId()),
						buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, null, "Active", null,
								null, grille1.getColumns().get(5).getDimensionId(), null),
						"EQUALS", 0));

		return join;
	}
	
	public static Join buildBILL110BillingEventGenerationVolumeFee(GrilleService gridService,
			InitiationService initiationService) {
		Join join = new Join();

		Grille grille1 = buildInputGridByName("ORD100 Order - Monthly fee", gridService);
		Grille grille2 = buildInputGridByName("PRI100 Monthly fee", gridService);
		Grille grille3 = ReportGridFactory.buildSYS210IssuingMAvolumepermember(initiationService);
		Grille grille4 = ReportGridFactory.buildSYS200IssuingMAvolumecurrentmonth(initiationService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		grille2.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		grille3.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		grille4.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));

		join.getGridListChangeHandler().addNew(buildJoingrid(1, "ORD100 Order - Monthly fee", JoinGridType.GRID, grille1.getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "PRI100 Monthly fee", JoinGridType.GRID, grille2.getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(2, "SYS210 Issuing MA volume per member", JoinGridType.REPORT_GRID, grille3.getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(3, "SYS200 Issuing MA volume current month", JoinGridType.REPORT_GRID, grille4.getId()));
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 0, JoinGridType.GRID, grille1.getId(), 1043L, 31L,
				JoinGridType.REPORT_GRID, grille3.getId(), 950L, 31L));
		join.setName("BILL110 Billing event generation - Volume fee");
		join.setAddPublicationRunNbr(true);
		join.setCreationDate(Timestamp.valueOf("2023-04-19 12:37:30"));
		join.setModificationDate(Timestamp.valueOf("2023-04-27 12:38:33"));
		join.setRefreshGridsBeforePublication(true);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridId(101L);
		join.setPublicationGridName("");
		join.setRowType(GrilleRowType.NOT_RECONCILIATED);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 0, DimensionType.ATTRIBUTE,
						"﻿Client ID", "Client ID", "Client ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "﻿Client ID").getId(),
						grille1.getColumns().get(0).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 18,
				DimensionType.MEASURE, "Column1",null, "Range fee", null,
				null,
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.CALCULATE, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 8,
				DimensionType.ATTRIBUTE, "Column1", "Billing event type", "Billing event type", null,
				initiationService.getAttributeRepository().findByName("Billing event type").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Invoice"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 9,
				DimensionType.ATTRIBUTE, "Column1", "Billing event category", "Billing event category", null,
				initiationService.getAttributeRepository().findByName("Billing event category").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Issuing"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 10, DimensionType.ATTRIBUTE,
						"Column1", "Billing group 1", "Billing group 1", null,
						initiationService.getAttributeRepository().findByName("Billing group 1").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Volume fee"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 11,
				DimensionType.ATTRIBUTE, "Column1", "Billing group 2", "Billing Group 2", null,
				initiationService.getAttributeRepository().findByName("Billing group 2").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "MasterCard"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 12, DimensionType.ATTRIBUTE,
						"Column1", "Billing event status", "Billing event status", null,
						initiationService.getAttributeRepository().findByName("Billing event status").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, 
								"Draft"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 13, DimensionType.PERIOD,
						"Column1", "Billing event date", "Billing event date",
						null,
						initiationService.getAttributeRepository().findByName("Billing event date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.BEGIN_MONTH, Timestamp.valueOf(
										"2023-04-01"), "+", 0, PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 20, DimensionType.MEASURE,
						"Column2", "Billing driver", "Billing driver", null,
						initiationService.getAttributeRepository().findByName("Billing driver").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 21, DimensionType.MEASURE,
						"Column1", "VAT rate", "VAT rate", null,
						initiationService.getAttributeRepository().findByName("VAT rate").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 14, DimensionType.ATTRIBUTE,
						"Column1", "Billing event description", "Billing event description", null,
						initiationService.getAttributeRepository().findByName("Billing event description").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Volume fee"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 17, DimensionType.MEASURE,
						"Column1", null, "Fee for range", null,
						null,
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.CONDITION, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 22,
				DimensionType.MEASURE, "Column1", "Billing amount", "Billing amount", null,
				initiationService.getAttributeRepository().findByName("Billing amount").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 23, DimensionType.PERIOD,
						"Column2", "Entry date", "Entry date", null,
						initiationService.getAttributeRepository().findByName("Entry date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.TODAY, Timestamp.valueOf("2023-04-26"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 19, DimensionType.MEASURE,
						"Column1", "Unit cost", "Unit cost",
						null,
						initiationService.getAttributeRepository().findByName("Unit cost").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 15, DimensionType.MEASURE,
						"Posting amount", "Posting amount", "Posting amount total",
						grille3.getColumnByDimensionAndName(DimensionType.MEASURE, "Posting amount").getId(),
						grille3.getColumns().get(1).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 16, DimensionType.MEASURE,
						"Fee amount", "Fee amount", "Fee amount",
						grille2.getColumnByDimensionAndName(DimensionType.MEASURE, "Fee amount").getId(),
						grille2.getColumns().get(2).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 3, DimensionType.MEASURE,
						"Posting amount", "Posting amount", "Posting amount MA",
						grille3.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Posting amount").getId(),
						grille3.getColumns().get(1).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 4, DimensionType.ATTRIBUTE,
						"Range", "Volume fee range", "Range",
						grille2.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Range").getId(),
						grille2.getColumns().get(1).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 7, DimensionType.MEASURE,
						"Minimum fee amount", "Minimum fee amount", "Minimum fee amount",
						grille2.getColumnByDimensionAndName(DimensionType.MEASURE, "Minimum fee amount").getId(),
						grille2.getColumns().get(2).getDimensionId(), null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 6, DimensionType.MEASURE,
				"Range - Maximum volume", "Range maximum amount", "Range - Maximum volume",
				grille2.getColumnByDimensionAndName(DimensionType.MEASURE, "Range - Maximum volume").getId(),
				grille2.getColumns().get(6).getDimensionId(), null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 5, DimensionType.MEASURE,
				"Range - Minimum volume", "Range minimum amount", "Range - Minimum volume",
				grille2.getColumnByDimensionAndName(DimensionType.MEASURE, "Range - Minimum volume").getId(),
				grille2.getColumns().get(5).getDimensionId(), null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 2, DimensionType.ATTRIBUTE,
				"Member Bank Name NL", "Member bank name", "Member Bank Name NL",
				grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Member Bank Name NL").getId(),
				grille1.getColumns().get(2).getDimensionId(), null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 1, DimensionType.ATTRIBUTE,
				"Member Bank ID", "Member Bank ID", "Member Bank ID",
				grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Member Bank ID").getId(),
				grille1.getColumns().get(1).getDimensionId(), null));

		return join;
	}
	public static Join buildBILL120BillingEventGenerationPenaltyFee(GrilleService gridService,
			InitiationService initiationService) {
		Join join = new Join();
		join.setName("BILL120 Billing event generation - Penalty fee");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-04-19 11:12:15"));
		join.setModificationDate(Timestamp.valueOf("2023-04-19 11:12:36"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.MATERIALIZED_GRID);
		join.setPublicationMethod(JoinPublicationMethod.NEW_GRID);
		join.setPublicationGridId(null);
		join.setPublicationGridName(null);
		join.setRowType(GrilleRowType.NOT_RECONCILIATED);
		
		return join;
	}
	
	public static Join buildBILL200BillingEventGenerationCommonSchemeFee(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
		Grille grille1 = ReportGridFactory.buildSYS210IssuingMAvolumepermember(initiationService);
		Grille grille2 = ReportGridFactory.buildSYS200IssuingMAvolumecurrentmonth(initiationService);
		MaterializedGrid materializedGrid = buildMaterializeGridByName("INP001 MCI PML Invoices", materializedGrilleService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		grille2.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		materializedGrid.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(2, "SYS210 Issuing MA volume per member", JoinGridType.REPORT_GRID, grille1.getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(3, "SYS200 Issuing MA volume current month", JoinGridType.REPORT_GRID, grille2.getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(1, "INP001 MCI PML Invoices", JoinGridType.MATERIALIZED_GRID, materializedGrid.getId()));
		
		
		join.setName("BILL200 Billing event generation - Common scheme fee");
		join.setAddPublicationRunNbr(true);
		join.setCreationDate(Timestamp.valueOf("2023-04-19 08:57:45"));
		join.setModificationDate(Timestamp.valueOf("2023-04-28 08:08:25"));
		join.setRefreshGridsBeforePublication(true);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridId(88L);
		join.setPublicationGridName("");
		join.setRowType(GrilleRowType.NOT_RECONCILIATED);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 4, DimensionType.MEASURE,
						"Column1", "Fee amount", "Fee amount",null
						,
						initiationService.getAttributeRepository().findByName("Fee amount").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.CALCULATE, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 5,
				DimensionType.MEASURE, "Column1","Billing amount", "Biiling amount", null,
				initiationService.getAttributeRepository().findByName("Billing amount").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 6,
				DimensionType.MEASURE, "Column1", "Billing driver", "Billing driver", null,
				initiationService.getAttributeRepository().findByName("Billing driver").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 7,
				DimensionType.MEASURE, "Column1", "Unit cost", "Unit cost", null,
				initiationService.getAttributeRepository().findByName("Unit cost").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 8, DimensionType.MEASURE,
						"Column1", "VAT rate", "VAT rate", null,
						initiationService.getAttributeRepository().findByName("VAT rate").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 9,
				DimensionType.ATTRIBUTE, "Column1", "Billing event description", "Billing event description", null,
				initiationService.getAttributeRepository().findByName("Billing event description").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Common scheme fee "))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 10, DimensionType.ATTRIBUTE,
						"Column1", "Billing event type", "Billing event type", null,
						initiationService.getAttributeRepository().findByName("Billing event type").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, 
								"Invoice"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 11, DimensionType.ATTRIBUTE,
						"Column1", "Billing event status", "Billing event status",
						null,
						initiationService.getAttributeRepository().findByName("Billing event status").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Draft"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 12, DimensionType.PERIOD,
						"Column2", "Billing event date", "Billing event date", null,
						initiationService.getAttributeRepository().findByName("Billing event date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.BEGIN_MONTH, Timestamp.valueOf("2023-04-01"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 13, DimensionType.ATTRIBUTE,
						"Column1", "Billing group 1", "Billing Group 1", null,
						initiationService.getAttributeRepository().findByName("Billing group 1").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Common scheme fee"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 14, DimensionType.ATTRIBUTE,
						"Column1", "Billing group 2", "Billing Group 2", null,
						initiationService.getAttributeRepository().findByName("Billing group 2").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "MasterCard"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 15, DimensionType.ATTRIBUTE,
						"Column1", "Billing event category", "Billing event category", null,
						initiationService.getAttributeRepository().findByName("Billing event category").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Issuing"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 16,
				DimensionType.ATTRIBUTE, "Column1", "Billing event nature", "Billing event nature", null,
				initiationService.getAttributeRepository().findByName("Billing event nature").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "To bill"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 17, DimensionType.ATTRIBUTE,
						"Column1", "Client ID", "Client ID", null,
						initiationService.getAttributeRepository().findByName("Client ID").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 18, DimensionType.PERIOD,
						"Column1", "Entry date", "Entry date",
						null,
						initiationService.getAttributeRepository().findByName("Entry date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.TODAY, Timestamp.valueOf("2023-04-26"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 3, DimensionType.MEASURE,
						"Posting amount", "Posting amount", "Posting amount total",
						grille1.getColumnByDimensionAndName(DimensionType.MEASURE, "Posting amount").getId(),
						grille1.getColumns().get(1).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 0, DimensionType.MEASURE,
						"Amount incl. tax", null, "Amount incl. tax",
						materializedGrid.getColumnByDimensionAndName(DimensionType.MEASURE, "Amount incl. tax").getId(),
						null, null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 2, DimensionType.MEASURE,
						"Member Bank ID", "Member Bank ID", "Member Bank ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Posting amount").getId(),
						grille1.getColumns().get(0).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 19, DimensionType.ATTRIBUTE,
						"UOM", null, "UOM",
						materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "UOM").getId(),
						null, null));
		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(
				buildJoinConditionItem(JoinGridType.MATERIALIZED_GRID, DimensionType.ATTRIBUTE, null, null,
						materializedGrid.getId(), null, null,
						materializedGrid.getColumns().get(43).getId()),
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, null, "Q", null,
						null, grille1.getColumns().get(5).getDimensionId(), null),
				"NOT_EQUALS", 0));

		return join;
	}
	
	public static Join buildBILL210BillingEventGenerationDirectSchemeFee(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
	
		MaterializedGrid materializedGrid = buildMaterializeGridByName("INP002 MCI affiliate invoice", materializedGrilleService);

		materializedGrid.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(1, "INP002 MCI affiliate invoice", JoinGridType.MATERIALIZED_GRID, materializedGrid.getId()));
		
		
		join.setName("BILL210 Billing event generation - Direct scheme fee");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-04-18 11:01:47"));
		join.setModificationDate(Timestamp.valueOf("2023-05-31 07:49:58"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridId(90L);
		join.setPublicationGridName("");
		join.setRowType(GrilleRowType.NOT_RECONCILIATED);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 0, DimensionType.ATTRIBUTE,
						"Billing event ID", null, "Billing event ID",materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Billing event ID").getId()
						,
						null,
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 18,
				DimensionType.ATTRIBUTE, "Column1","Billing event description", "Billing event description", null,
				initiationService.getAttributeRepository().findByName("Billing event description").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Direct scheme cost"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 19,
				DimensionType.ATTRIBUTE, "Column1", "Billing event category ID", "Billing event category", null,
				initiationService.getAttributeRepository().findByName("Billing event category ID").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, 
						"Scheme fee"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 20,
				DimensionType.ATTRIBUTE, "Column1", "Billing event status", "Billing event status", null,
				initiationService.getAttributeRepository().findByName("Billing event status").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Draft"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 21, DimensionType.PERIOD,
						"Column1", "Billing event date", "Billing event date", null,
						initiationService.getAttributeRepository().findByName("Billing event date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.TODAY, Timestamp.valueOf("2023-04-18"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 22,
				DimensionType.MEASURE, "Column1", "Billing amount", "Billing amount", null,
				initiationService.getAttributeRepository().findByName("Billing amount").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 23, DimensionType.MEASURE,
						"Column2", "VAT rate", "VAT rate", null,
						initiationService.getAttributeRepository().findByName("VAT rate").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, 
								null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 24, DimensionType.MEASURE,
						"Column2", "Billing driver", "Billing driver",
						null,
						initiationService.getAttributeRepository().findByName("Billing driver").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 27, DimensionType.ATTRIBUTE,
						"Column1", "Client ID", "Client ID", null,
						initiationService.getAttributeRepository().findByName("Client ID").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 29, DimensionType.ATTRIBUTE,
						"Column1", "Billing event type", "Billing event type", null,
						initiationService.getAttributeRepository().findByName("Billing event type").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Invoice"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 30, DimensionType.ATTRIBUTE,
						"Column1", "Billing group 1", "Billing Group 1", null,
						initiationService.getAttributeRepository().findByName("Billing group 1").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Scheme fee"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 31, DimensionType.ATTRIBUTE,
						"Column1", "Billing group 2", "Billing Group 2", null,
						initiationService.getAttributeRepository().findByName("Billing group 2").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "MCI"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 25,
				DimensionType.MEASURE, "Column1", "Unit cost", "unit cost", null,
				initiationService.getAttributeRepository().findByName("Unit cost").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 32, DimensionType.PERIOD,
						"Column1", "Entry date", "Entry date", null,
						initiationService.getAttributeRepository().findByName("Entry date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 28, DimensionType.ATTRIBUTE,
						"Column1", null, "Member Bank ID",
						null,
						null,
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, Timestamp.valueOf("2023-05-31"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 1, DimensionType.ATTRIBUTE,
						"Billing event name", null, "Billing event name",
						materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Billing event name").getId(),
						null, null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 2, DimensionType.ATTRIBUTE,
						"Month", null, "Month",
						materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Month").getId(),
						null, null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 3, DimensionType.ATTRIBUTE,
						"Year", null, "Year",
						materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Year").getId(),
						null, null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 4, DimensionType.ATTRIBUTE,
						"D/C", null, "D/C",
						materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "D/C").getId(),
						null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 5, DimensionType.MEASURE,
				"Amount exl. Tax", null, "Amount exl. Tax",
				materializedGrid.getColumnByDimensionAndName(DimensionType.MEASURE, "Amount exl. Tax").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 6, DimensionType.MEASURE,
				"Driver", null, "Driver",
				materializedGrid.getColumnByDimensionAndName(DimensionType.MEASURE, "Driver").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 7, DimensionType.MEASURE,
				"Rate0", null, "Rate0",
				materializedGrid.getColumnByDimensionAndName(DimensionType.MEASURE, "Rate0").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 8, DimensionType.ATTRIBUTE,
				"Brand", null, "Brand",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Brand").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 9, DimensionType.ATTRIBUTE,
				"Broker ID", null, "Broker ID",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Broker ID").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 26, DimensionType.ATTRIBUTE,
				"AFFILIATE_CUSTOMER_ID", null, "AFFILIATE_CUSTOMER_ID",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "AFFILIATE_CUSTOMER_ID").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 12, DimensionType.ATTRIBUTE,
				"Billing event category ID", null, "Billing event category ID",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Billing event category ID").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 17, DimensionType.ATTRIBUTE,
				"Scheme", null, "Scheme",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Scheme").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 16, DimensionType.MEASURE,
				"Check", null, "Check",
				materializedGrid.getColumnByDimensionAndName(DimensionType.MEASURE, "Check").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 15, DimensionType.ATTRIBUTE,
				"Invoice type", null, "Invoice type",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Invoice type").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 14, DimensionType.PERIOD,
				"invoice date", null, "invoice date",
				materializedGrid.getColumnByDimensionAndName(DimensionType.PERIOD, "invoice date").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 13, DimensionType.ATTRIBUTE,
				"Invoice number", null, "Invoice number",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Invoice number").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 11, DimensionType.ATTRIBUTE,
				"Billing event category name", null, "Billing event category name",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Billing event category name").getId(),
				null, null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.MATERIALIZED_GRID, 10, DimensionType.ATTRIBUTE,
				"PML ID", null, "PML ID",
				materializedGrid.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "PML ID").getId(),
				null, null));

		return join;
	}
	
	public static Join buildGPH001PendingPerRecoTypePerSchemePlatform(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
	
		
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "SYS500 Reco R02", JoinGridType.JOIN, buildSYS500RecoR02(materializedGrilleService, initiationService).getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(1, "SYS502 Reco R03", JoinGridType.JOIN, buildSYS502RecoR03(materializedGrilleService, initiationService).getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(2, "SYS504 Reco R04", JoinGridType.JOIN, buildSYS504RecoR04(materializedGrilleService, initiationService).getId()));

		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 0, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 3L, 69L,
				JoinGridType.JOIN, buildSYS502RecoR03(materializedGrilleService, initiationService).getId(), 6L, 69L));
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 1, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 3L, 69L,
				JoinGridType.JOIN, buildSYS504RecoR04(materializedGrilleService, initiationService).getId(), 11L, 69L));
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 2, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 5L, 85L,
				JoinGridType.JOIN, buildSYS502RecoR03(materializedGrilleService, initiationService).getId(), 8L, 85L));
		
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 3, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 5L, 85L,
				JoinGridType.JOIN, buildSYS504RecoR04(materializedGrilleService, initiationService).getId(), 14L, 85L));
		
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.MEASURE, 4, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 4L, 4L,
				JoinGridType.JOIN, buildSYS502RecoR03(materializedGrilleService, initiationService).getId(), 7L, 4L));
		
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.MEASURE, 5, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 4L, 4L,
				JoinGridType.JOIN, buildSYS502RecoR03(materializedGrilleService, initiationService).getId(), 7L, 4L));
		
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.PERIOD, 6, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 10L, 1L,
				JoinGridType.JOIN, buildSYS502RecoR03(materializedGrilleService, initiationService).getId(), 9L, 1L));
		
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.PERIOD, 7, JoinGridType.JOIN,  buildSYS500RecoR02(materializedGrilleService, initiationService).getId(), 10L, 1L,
				JoinGridType.JOIN, buildSYS502RecoR03(materializedGrilleService, initiationService).getId(), 13L, 1L));
		
		join.setName("GPH001 Pending per reco type per scheme platform ");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-03-26 14:12:33"));
		join.setModificationDate(Timestamp.valueOf("2023-05-31 14:18:56"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.NEW_GRID);
		join.setPublicationGridId(null);
		join.setPublicationGridName(null);
		join.setRowType(null);
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.JOIN, 0, DimensionType.ATTRIBUTE,
				"Scheme Platform ID", "Scheme Platform ID", "Scheme Platform ID",buildSYS500RecoR02(materializedGrilleService, initiationService).getColumns().get(0).getColumnId()
				,
				buildSYS500RecoR02(materializedGrilleService, initiationService).getColumns().get(0).getDimensionId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));


		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.JOIN, 2, DimensionType.MEASURE,
				"Count", "Count", "Count",buildSYS500RecoR02(materializedGrilleService, initiationService).getColumns().get(1).getColumnId()
				,
				buildSYS500RecoR02(materializedGrilleService, initiationService).getColumns().get(1).getDimensionId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.JOIN, 1, DimensionType.ATTRIBUTE,
				"Reco name", "Reco name", "Reco name",buildSYS500RecoR02(materializedGrilleService, initiationService).getColumns().get(2).getColumnId()
				,
				buildSYS500RecoR02(materializedGrilleService, initiationService).getColumns().get(2).getDimensionId(),null));

		return join;
	}
	
	public static Join buildSYS400InputForEom(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
	
		Grille grille1 = ReportGridFactory.buildSYS001Financialmovements(initiationService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "SYS001 Financial movements", JoinGridType.REPORT_GRID, grille1.getId()));
		
		
		join.setName("SYS400 Input for eom");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-04-15 09:05:18"));
		join.setModificationDate(Timestamp.valueOf("2023-04-15 09:28:38"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.MATERIALIZED_GRID);
		join.setPublicationMethod(JoinPublicationMethod.NEW_GRID);
		join.setPublicationGridId(null);
		join.setPublicationGridName(null);
		join.setRowType(GrilleRowType.NOT_RECONCILIATED);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 14, DimensionType.ATTRIBUTE,
						"Counterpart Account N°", "Counterpart Account N°", "Counterpart Account N°",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Counterpart Account N°").getId()
						,
						grille1.getColumns().get(10).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 4,
				DimensionType.ATTRIBUTE, "Column1",null, "reco type", null,
				null,
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.CONDITION, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 5,
				DimensionType.ATTRIBUTE, "Column1", null, "Reco date unique", null,
				null,
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.CONDITION, 
						null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 0,
				DimensionType.PERIOD, "Value date", "Value date", "Value date", grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Value date").getId(),
				grille1.getColumns().get(0).getDimensionId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 13, DimensionType.ATTRIBUTE,
						"Column1",null, "Column1", null,null,
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.CONDITION, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 12,
				DimensionType.PERIOD, "Entry date", "Entry date", "Entry date", grille1.getColumnByDimensionAndName(DimensionType.PERIOD, "Entry date").getId(),
				grille1.getColumns().get(13).getDimensionId(),null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 11, DimensionType.PERIOD,
						"Reco date R4", "Reco date R4", "Reco date R4", grille1.getColumnByDimensionAndName(DimensionType.PERIOD, "Reco date R4").getId(),
						grille1.getColumns().get(9).getDimensionId(),null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 8, DimensionType.ATTRIBUTE,
						"R04", "R04", "R04",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "R04").getId(),
						grille1.getColumns().get(6).getDimensionId(),null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 3, DimensionType.MEASURE,
				"Financial amount", "Financial amount", "Financial amount",grille1.getColumnByDimensionAndName(DimensionType.MEASURE, "Financial amount").getId(),
				grille1.getColumns().get(2).getDimensionId(),null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 2, DimensionType.ATTRIBUTE,
				"D-C", "D-C", "D-C",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "D-C").getId(),
				grille1.getColumns().get(3).getDimensionId(),null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 1, DimensionType.ATTRIBUTE,
				"Bank Account N°", "Bank Account N°", "Bank Account N°",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Bank Account N°").getId(),
				grille1.getColumns().get(0).getDimensionId(),null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 9, DimensionType.PERIOD,
				"Reco date R1", "Reco date R1", "Reco date R1",grille1.getColumnByDimensionAndName(DimensionType.PERIOD , "Reco date R1").getId(),
				grille1.getColumns().get(7).getDimensionId(),null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 10, DimensionType.PERIOD,
				"Reco date R3", "Reco date R3", "Reco date R3",grille1.getColumnByDimensionAndName(DimensionType.PERIOD, "Reco date R3").getId(),
				grille1.getColumns().get(8).getDimensionId(),null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 6, DimensionType.ATTRIBUTE,
				"R01", "R01", "R01",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "R01").getId(),
				grille1.getColumns().get(4).getDimensionId(),null));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.REPORT_GRID, 7, DimensionType.ATTRIBUTE,
				"R03", "R03", "R03",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "R03").getId(),
				grille1.getColumns().get(5).getDimensionId(),null));

		return join;
	}
	
	public static Join buildSYS500RecoR02(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
	
		Grille grille1 = ReportGridFactory.buildSYS100Advisements(initiationService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "SYS100 Advisements", JoinGridType.REPORT_GRID, grille1.getId()));
		
		
		join.setName("SYS500 Reco R02");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-03-26 13:54:48"));
		join.setModificationDate(Timestamp.valueOf("2023-03-26 14:07:31"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.MATERIALIZED_GRID);
		join.setPublicationMethod(JoinPublicationMethod.NEW_GRID);
		join.setPublicationGridId(null);
		join.setPublicationGridName(null);
		join.setRowType(null);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 0, DimensionType.ATTRIBUTE,
						"Scheme Platform ID", "Scheme Platform ID", "Scheme Platform ID",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Scheme Platform ID").getId()
						,
						grille1.getColumns().get(2).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 2,
				DimensionType.ATTRIBUTE, null,"Reco name", "Reco name", null,
				initiationService.getAttributeRepository().findByName("Reco name").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "R2"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 3,
				DimensionType.PERIOD, "Value date", "Value date", "Value date", grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Value date").getId(),
				grille1.getColumns().get(0).getDimensionId(),null));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 1,
				DimensionType.MEASURE, "Count", "Count", "Count", grille1.getColumnByDimensionAndName(DimensionType.MEASURE, "Count").getId(),
				grille1.getColumns().get(3).getDimensionId(),null));

		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.MEASURE, "Remaining amount R2", null,
						grille1.getId(), null, grille1.getColumns().get(6).getDimensionId(),
				grille1.getColumnByDimensionAndName(DimensionType.MEASURE, "Remaining amount R2").getId()),
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.MEASURE, null, null, null,
						null, null, null),
				">", 0));
		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, "Advisement Account ID", null,
						grille1.getId(), null, grille1.getColumns().get(1).getDimensionId(),
				grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Advisement Account ID").getId()),
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, null, "Settlement advisement", null,
						null,grille1.getColumns().get(1).getDimensionId(), null),
				"EQUALS", 1));

		return join;
	}
	
	public static Join buildSYS502RecoR03(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
	
		Grille grille1 = ReportGridFactory.buildSYS100Advisements(initiationService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "SYS100 Advisements", JoinGridType.REPORT_GRID, grille1.getId()));
		
		
		join.setName("SYS502 Reco R03");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-03-26 14:04:49"));
		join.setModificationDate(Timestamp.valueOf("2023-03-26 14:07:04"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.MATERIALIZED_GRID);
		join.setPublicationMethod(JoinPublicationMethod.NEW_GRID);
		join.setPublicationGridId(null);
		join.setPublicationGridName(null);
		join.setRowType(null);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 0, DimensionType.ATTRIBUTE,
						"Scheme Platform ID", "Scheme Platform ID", "Scheme Platform ID",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Scheme Platform ID").getId()
						,
						grille1.getColumns().get(2).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 2,
				DimensionType.ATTRIBUTE, null,"Reco name", "Reco name", null,
				initiationService.getAttributeRepository().findByName("Reco name").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "R3"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 3,
				DimensionType.PERIOD, "Value date", "Value date", "Value date", grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Value date").getId(),
				grille1.getColumns().get(0).getDimensionId(),null));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 1,
				DimensionType.MEASURE, "Count", "Count", "Count", grille1.getColumnByDimensionAndName(DimensionType.MEASURE, "Count").getId(),
				grille1.getColumns().get(3).getDimensionId(),null));

		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, "Advisement Account ID", null,
						grille1.getId(), null, grille1.getColumns().get(1).getDimensionId(),
				grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Advisement Account ID").getId()),
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, null, "Member advisement", null,
						null, grille1.getColumns().get(1).getDimensionId(), null),
				"EQUALS", 0));
		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.MEASURE, "Remaining amount R3", null,
						grille1.getId(), null, grille1.getColumns().get(7).getDimensionId(),
				grille1.getColumnByDimensionAndName(DimensionType.MEASURE , "Remaining amount R3").getId()),
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.MEASURE, null, null, null,
						null,null, null),
				">", 1));

		return join;
	}
	
	public static Join buildSYS504RecoR04(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
	
		Grille grille1 = ReportGridFactory.buildSYS100Advisements(initiationService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "SYS100 Advisements", JoinGridType.REPORT_GRID, grille1.getId()));
		
		
		join.setName("SYS504 Reco R04");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-03-26 14:07:45"));
		join.setModificationDate(Timestamp.valueOf("2023-03-26 14:12:27"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.MATERIALIZED_GRID);
		join.setPublicationMethod(JoinPublicationMethod.NEW_GRID);
		join.setPublicationGridId(null);
		join.setPublicationGridName(null);
		join.setRowType(null);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 0, DimensionType.ATTRIBUTE,
						"Scheme Platform ID", "Scheme Platform ID", "Scheme Platform ID",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Scheme Platform ID").getId()
						,
						grille1.getColumns().get(2).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 2,
				DimensionType.ATTRIBUTE, null,"Reco name", "Reco name", null,
				initiationService.getAttributeRepository().findByName("Reco name").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "R4"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 3,
				DimensionType.PERIOD, "Value date", "Value date", "Value date", grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Value date").getId(),
				grille1.getColumns().get(0).getDimensionId(),null));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 1,
				DimensionType.MEASURE, "Count", "Count", "Count", grille1.getColumnByDimensionAndName(DimensionType.MEASURE, "Count").getId(),
				grille1.getColumns().get(3).getDimensionId(),null));

		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, "Advisement Account ID", null,
						grille1.getId(), null, grille1.getColumns().get(1).getDimensionId(),
				grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Advisement Account ID").getId()),
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, null, "Settlement advisement", null,
						null, grille1.getColumns().get(1).getDimensionId(), null),
				"EQUALS", 0));
		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.MEASURE, "Remaining amount R4", null,
						grille1.getId(), null, grille1.getColumns().get(8).getDimensionId(),
				grille1.getColumnByDimensionAndName(DimensionType.MEASURE , "Remaining amount R4").getId()),
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.MEASURE, null, null, null,
						null,null, null),
				">", 1));

		return join;
	}

	public static Join buildSYS504RECOR04SAVES(MaterializedGridService materializedGrilleService,
			InitiationService initiationService) {
		Join join = new Join();

		
	
		Grille grille1 = ReportGridFactory.buildSYS100Advisements(initiationService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "SYS100 Advisements", JoinGridType.REPORT_GRID, grille1.getId()));
		
		
		join.setName("SYS504 RECO R04 SAVES");
		join.setAddPublicationRunNbr(false);
		join.setCreationDate(Timestamp.valueOf("2023-05-09 17:23:25"));
		join.setModificationDate(Timestamp.valueOf("2023-05-09 17:24:03"));
		join.setRefreshGridsBeforePublication(false);
		join.setPublicationDataSourceType(DataSourceType.MATERIALIZED_GRID);
		join.setPublicationMethod(JoinPublicationMethod.NEW_GRID);
		join.setPublicationGridId(null);
		join.setPublicationGridName(null);
		join.setRowType(null);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 0, DimensionType.ATTRIBUTE,
						"Scheme Platform ID", "Scheme Platform ID", "Scheme Platform ID",grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Scheme Platform ID").getId()
						,
						grille1.getColumns().get(2).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 2,
				DimensionType.ATTRIBUTE, null,"Reco name", "Reco name", null,
				initiationService.getAttributeRepository().findByName("Reco name").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "R4"))));

		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 3,
				DimensionType.ATTRIBUTE, "Column1",null, "Column1", null,
				null,
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, null, null))));


		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 4,
				DimensionType.ATTRIBUTE, "Column2",null, "Column2", null,
				null,
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, null, null))));


		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 1,
				DimensionType.MEASURE, "Count", "Count", "Count", grille1.getColumnByDimensionAndName(DimensionType.MEASURE, "Count").getId(),
				grille1.getColumns().get(3).getDimensionId(),null));

		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(null,
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, null, "Settlement advisement", null,
						null, grille1.getColumns().get(1).getDimensionId(), null),
				"EQUALS", 0));
		join.getConditionListChangeHandler()
		.addNew(buildJoinCondition(null,
				buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.MEASURE, null, null, null,
						null,null, null),
				">", 1));

		return join;
	}
	
	public static Join buildTet(GrilleService gridService,
			InitiationService initiationService) {
		Join join = new Join();

		Grille grille1 = buildInputGridByName("ORD100 Order - Monthly fee", gridService);
		Grille grille2 = buildInputGridByName("PRI100 Monthly fee", gridService);

		grille1.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		grille2.getColumns().sort(Comparator.comparingInt(c -> c.getPosition()));
		
		join.getGridListChangeHandler().addNew(buildJoingrid(0, "ORD100 Order - Monthly fee", JoinGridType.GRID, grille1.getId()));
		join.getGridListChangeHandler().addNew(buildJoingrid(1, "PRI100 Monthly fee", JoinGridType.GRID, grille2.getId()));
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 1, JoinGridType.GRID, grille1.getId(), 1046L, 100L,
				JoinGridType.GRID, grille2.getId(), 1013L, 100L));
		join.getKeyListChangeHandler().addNew(buildJoinkey(DimensionType.ATTRIBUTE, 0, JoinGridType.GRID, grille1.getId(), 1045L, 68L,
				JoinGridType.GRID, grille2.getId(), 1012L, 68L));
		join.setName("tet");
		join.setAddPublicationRunNbr(true);
		join.setCreationDate(Timestamp.valueOf("2023-05-16 21:01:13"));
		join.setModificationDate(Timestamp.valueOf("2023-05-16 21:01:13"));
		join.setRefreshGridsBeforePublication(true);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridId(86L);
		join.setPublicationGridName("");
		join.setRowType(GrilleRowType.NOT_RECONCILIATED);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 0, DimensionType.ATTRIBUTE,
						"﻿Client ID", "Client ID", "Client ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "﻿Client ID").getId(),
						grille1.getColumns().get(0).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 8,
				DimensionType.ATTRIBUTE, "Column1", "Billing event type", "Billing event type", null,
				initiationService.getAttributeRepository().findByName("Billing event type").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Invoice"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 9,
				DimensionType.ATTRIBUTE, "Column1", "Billing event category", "Billing event category", null,
				initiationService.getAttributeRepository().findByName("Billing event category").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Issuing"))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 10,
				DimensionType.ATTRIBUTE, "Column1", "Billing group 1", "Billing group 1", null,
				initiationService.getAttributeRepository().findByName("Billing group 1").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Monthly fee"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 11, DimensionType.ATTRIBUTE,
						"Column1", "Billing group 2", "Billing group 2", null,
						initiationService.getAttributeRepository().findByName("Billing group 2").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 12,
				DimensionType.ATTRIBUTE, "Column1", "Billing event status", "Billing event status", null,
				initiationService.getAttributeRepository().findByName("Billing event status").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Draft"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 0, DimensionType.PERIOD,
						"Column1", "Billing event date", "Billing event date", null,
						initiationService.getAttributeRepository().findByName("Billing event date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.BEGIN_MONTH, Timestamp.valueOf("2023-04-01"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 13, DimensionType.ATTRIBUTE,
						"﻿Client ID", "Client ID", "Client ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "﻿Client ID").getId(),
						grille1.getColumns().get(0).getDimensionId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, null, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 14, DimensionType.MEASURE,
						"Column1", "Billing amount", "Billing amount", null,
						initiationService.getAttributeRepository().findByName("Billing amount").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 16, DimensionType.MEASURE,
						"Column1", "Billing driver", "Billing driver", null,
						initiationService.getAttributeRepository().findByName("Billing driver").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 15, DimensionType.MEASURE,
						"Column1", "Unit cost", "Unit cost", null,
						initiationService.getAttributeRepository().findByName("Unit cost").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.COPY, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 17, DimensionType.MEASURE,
						"Column1", "VAT rate", "VAT rate", null,
						initiationService.getAttributeRepository().findByName("VAT rate").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
								DimensionType.MEASURE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler().addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 18,
				DimensionType.ATTRIBUTE, "Column1", "Billing event description", "Billing event description", null,
				initiationService.getAttributeRepository().findByName("Billing event description").getId(),
				buildJoinColumnProperties(buildJoinColumnField(
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						DimensionType.ATTRIBUTE, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, "Monthly fee"))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.CUSTOM, JoinGridType.REPORT_GRID, 19, DimensionType.PERIOD,
						"Column1", "Entry date", "Entry date", null,
						initiationService.getAttributeRepository().findByName("Entry date").getId(),
						buildJoinColumnProperties(buildJoinColumnField(
								buildPeriodValue(PeriodOperator.TODAY, Timestamp.valueOf("2023-04-01"), "+", 0,
										PeriodGranularity.DAY, null),
								DimensionType.PERIOD, null, JoinGridType.REPORT_GRID, JoinColumnType.FREE, null))));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 1, DimensionType.ATTRIBUTE,
						"Member Bank ID", "Member Bank ID", "Member Bank ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Member Bank ID").getId(),
						grille1.getColumns().get(1).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 3, DimensionType.ATTRIBUTE,
						"Scheme ID", "Scheme ID", "Scheme ID",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Scheme ID").getId(),
						grille1.getColumns().get(3).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 2, DimensionType.ATTRIBUTE,
						"Member Bank Name NL", "Member bank name", "Member Bank Name NL",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Member Bank Name NL").getId(),
						grille1.getColumns().get(2).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 4, DimensionType.ATTRIBUTE,
						"PML Type", "PML Type", "PML Type",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "PML Type").getId(),
						grille1.getColumns().get(4).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 5, DimensionType.ATTRIBUTE,
						"Order status", "Order status", "Order status",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Order status").getId(),
						grille1.getColumns().get(5).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 7, DimensionType.MEASURE,
						"Fee amount", "Fee amount", "Fee amount",
						grille2.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Fee amount").getId(),
						grille2.getColumns().get(2).getDimensionId(), null));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(JoinColumnCategory.STANDARD, JoinGridType.GRID, 6, DimensionType.ATTRIBUTE,
						"Fee type", "Fee type", "Order type",
						grille1.getColumnByDimensionAndName(DimensionType.ATTRIBUTE, "Fee type").getId(),
						grille1.getColumns().get(6).getDimensionId(), null));
		join.getConditionListChangeHandler()
				.addNew(buildJoinCondition(
						buildJoinConditionItem(JoinGridType.GRID, DimensionType.ATTRIBUTE, "Order status", null,
								grille1.getId(), null, grille1.getColumns().get(5).getDimensionId(),
								grille1.getColumns().get(5).getId()),
						buildJoinConditionItem(JoinGridType.REPORT_GRID, DimensionType.ATTRIBUTE, null, "Active", null,
								null, grille1.getColumns().get(5).getDimensionId(), null),
						"EQUALS", 0));

		return join;
	}


	public static JoinGrid buildJoingrid(int position, String name, JoinGridType gridType, Long gridId) {
		JoinGrid joinGrid = new JoinGrid();
		joinGrid.setGridId(gridId);
		joinGrid.setGridType(gridType);
		joinGrid.setName(name);
		joinGrid.setPosition(position);
		return joinGrid;
	}
	public static JoinConditionItem buildJoinConditionItem(JoinGridType gridType,DimensionType type,String dimensionName,String stringValue,Long gridId,PeriodValue periodeValue, Long dimensionId, Long columnId) {
		JoinConditionItem item = new JoinConditionItem();
		item.setGridId(gridId);
		item.setGridType(gridType);
		item.setDimensionName(dimensionName);
		item.setDimensionId(dimensionId);
		item.setColumnId(columnId);
		item.setType(JoinConditionItemType.PARAMETER);
		item.setDimensionType(type);
		item.setPeriodValue(periodeValue);
		item.setStringValue(stringValue);
		
		return item;
	}
	
	public static PeriodValue buildPeriodValue(PeriodOperator dateOperator,Date dateValue,String dateSign,int dateNumber,PeriodGranularity dateGranularity,String variableName) {
		PeriodValue periodeValue = new PeriodValue();
		periodeValue.setDateOperator(dateOperator);
		periodeValue.setDateGranularity(dateGranularity);
		periodeValue.setDateNumber(dateNumber);
		periodeValue.setDateSign(dateSign);
		periodeValue.setDateValue(dateValue);
		periodeValue.setVariableName(variableName);
		return periodeValue;
	}

	public static JoinCondition buildJoinCondition(JoinConditionItem item1,JoinConditionItem item2,String comparator,int position) {
		JoinCondition joinCondition = new JoinCondition();
		joinCondition.setVerb("AND");
		joinCondition.setItem1(item1);
		joinCondition.setItem2(item2);
		joinCondition.setOpeningBracket("(");
		joinCondition.setComparator(comparator);
		joinCondition.setClosingBracket(")");
		joinCondition.setPosition(position);
		return joinCondition;
	}
	
	public static JoinColumnField buildJoinColumnField(PeriodValue dateValue,DimensionType dimensionType,BigDecimal decimalValue,JoinGridType gridType,JoinColumnType type,String stringValue) {
		JoinColumnField field = new JoinColumnField();
		field.setGridType(JoinGridType.REPORT_GRID);
		field.setDateValue(dateValue);
		field.setDimensionType(dimensionType);
		field.setDecimalValue(decimalValue);
		field.setGridType(gridType);
		field.setEndPosition(0);
		field.setStartPosition(0);
		field.setType(type);
		field.setStringValue(stringValue);
		return field;
	}
	
	public static JoinColumnProperties buildJoinColumnProperties(JoinColumnField field) {
		JoinColumnProperties properties = new JoinColumnProperties();
		properties.setField(field);;
		
		return properties;
	}

	public static JoinColumn buildColumn(JoinColumnCategory category,JoinGridType gridType,int position, DimensionType type,String columnName , String dimensionName,String name,
			 Long columnId,Long dimensionId,JoinColumnProperties properties) {
		JoinColumn column = new JoinColumn();
		column.setName(name);
		column.setCategory(category);
		column.setColumnName(columnName);
		column.setDimensionName(dimensionName);
		column.setAlignment(HorizontalAlignment.Center);
		column.setColumnId(columnId);
		column.setDimensionId(dimensionId);
		column.setPosition(position);
		column.setType(type);
		column.setGridType(gridType);
		column.setProperties(properties);;
		column.setUsedForPublication(true);
		column.setProperties(properties);
		column.setShow(true);
		return column;

	}

	public static JoinKey buildJoinkey(DimensionType dimensionType,int position,JoinGridType joinGridType1, Long gridType1, Long columId1,Long valueId1,
			JoinGridType joinGridType2, Long gridType2, Long columId2,Long valueId2) {
		JoinKey joinkey = new JoinKey();
		joinkey.setDimensionType(dimensionType);
		joinkey.setPosition(position);;
		joinkey.setGridType1(joinGridType1);
		joinkey.setGridId1(gridType1);
		joinkey.setColumnId1(columId1);
		joinkey.setGridType2(joinGridType2);
		joinkey.setGridId2(gridType2);
		joinkey.setColumnId2(columId2);
		joinkey.setValueId1(valueId1);
		joinkey.setValueId2(valueId2);
		return joinkey;
	}
	
	public static Grille buildInputGridByName(String name, GrilleService gridService) {
		assertThat(name).isNotNull();
		Grille grid = gridService.getByName(name);
		assertThat(grid).isNotNull();
		assertThat(grid.getId()).isNotNull();
		return grid;
	}
//	public static Long buildDimensionByName(String name, InitiationService initiationService,DimensionType dimensionType) {
//		assertThat(name).isNotNull();
//		Dimension dimension = initiationService.getDimension(dimensionType, name, true, null, null);
//		assertThat(dimension).isNotNull();
//		assertThat(dimension.getId()).isNotNull();
//		return dimension.getId();
//	}
	
	public static MaterializedGrid buildMaterializeGridByName(String name, MaterializedGridService materializedGrilleService) {
		assertThat(name).isNotNull();
		MaterializedGrid mat = materializedGrilleService.getByName(name);
		assertThat(mat).isNotNull();
		assertThat(mat.getId()).isNotNull();
		return mat;
	}

}
