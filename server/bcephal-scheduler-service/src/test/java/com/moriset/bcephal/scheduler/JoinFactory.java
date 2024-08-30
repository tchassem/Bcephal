package com.moriset.bcephal.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinCondition;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.JoinKey;
import com.moriset.bcephal.grid.domain.JoinPublicationMethod;

public class JoinFactory {
	

	
	
	public static Join buildBIL010MAVolumePerClientPerInvoicePivot() {
		Join join = new Join();
		List<JoinGrid> grids = new ArrayList<>();
		List<JoinKey> keys = new ArrayList<>();
		
		keys.add(buildJoinkey(JoinGridType.REPORT_GRID, ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getId(), 6L,
				JoinGridType.REPORT_GRID, ReportGridFactory.buildSYS235MATotalForMembershipVol().getId(), 5L));
		grids.add(buildJoingrid(1, ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getName(), JoinGridType.REPORT_GRID, 54L));
		grids.add(buildJoingrid(2,ReportGridFactory.buildSYS235MATotalForMembershipVol().getName() , JoinGridType.REPORT_GRID, 55L));
		join.setName("BIL010 MA volume per client per invoice pivot");
		join.setGrids(grids);
		join.setKeys(keys);
		join.setVisibleInShortcut(true);
		join.getColumnListChangeHandler().addNew(buildColumn(1,ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getId(),
				DimensionType.ATTRIBUTE , ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getName(), 6L));
		join.getColumnListChangeHandler().addNew(buildColumn( 2,ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getId(),
				DimensionType.ATTRIBUTE , ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getName(), 4L));
		join.getColumnListChangeHandler().addNew(buildColumn( 3	,ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getId(),
				DimensionType.MEASURE , ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getName(), 5L));
		join.getColumnListChangeHandler().addNew(buildColumn( 4,ReportGridFactory.buildSYS235MATotalForMembershipVol().getId(),
				DimensionType.MEASURE , ReportGridFactory.buildSYS235MATotalForMembershipVol().getName(), 4L));
		join.getColumnListChangeHandler().addNew(buildColumn( 5,null,DimensionType.MEASURE , "Check", null));
		join.getColumnListChangeHandler().addNew(buildColumn( 6,ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getId(),
				DimensionType.ATTRIBUTE , ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getName(), 3L));
		join.getColumnListChangeHandler().addNew(buildColumn( 7,ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getId(),
				DimensionType.ATTRIBUTE , ReportGridFactory.buildSYS230MAPerClientForMembershipVolumeFeeBilling().getName(), 1L));
		join.getColumnListChangeHandler().addNew(buildColumn( 8,null,DimensionType.PERIOD , "Reference month start date",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 9,null,DimensionType.PERIOD , "Entry date",null));
		join.setRefreshGridsBeforePublication(true);
		join.setPublicationDataSourceType(DataSourceType.MATERIALIZED_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridName("BIL010 MA volume per client per invoice pivot");
		join.setAddPublicationRunNbr(true);
		join.setConditionListChangeHandler(null);
		return join;
		
	}
	
	public static Join buildBIL100MembershipFeeYearlyFee() throws Exception {
		Join join = new Join();
		List<JoinGrid> grids = new ArrayList<>();
		List<JoinKey> keys = new ArrayList<>();

		keys.add(buildJoinkey(JoinGridType.MATERIALIZED_GRID,
				MaterializedGilleFactory.buildFEE900ClientOrders().getId(), 3L,
				JoinGridType.MATERIALIZED_GRID, MaterializedGilleFactory.buildPRI001PricingGridMembershipYearlyFee().getId(), 2L));
		grids.add(buildJoingrid(1, MaterializedGilleFactory.buildFEE900ClientOrders().getName(),
				JoinGridType.MATERIALIZED_GRID, MaterializedGilleFactory.buildMaterializedGrilles().get(3).getId()));
		grids.add(buildJoingrid(2, MaterializedGilleFactory.buildPRI001PricingGridMembershipYearlyFee().getName(),
				JoinGridType.MATERIALIZED_GRID, MaterializedGilleFactory.buildMaterializedGrilles().get(23).getId()));
		join.setName("BIL100 Membership fee - yearly fee (month basis)");
		join.setGrids(grids);
		join.setKeys(keys);
		join.setVisibleInShortcut(true);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(1, MaterializedGilleFactory.buildPRI001PricingGridMembershipYearlyFee().getId(),
						DimensionType.ATTRIBUTE,
						MaterializedGilleFactory.buildPRI001PricingGridMembershipYearlyFee().getName(), 0L));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(2, MaterializedGilleFactory.buildFEE900ClientOrders().getId(),
						DimensionType.ATTRIBUTE,
						MaterializedGilleFactory.buildFEE900ClientOrders().getName(), 0L));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(3, MaterializedGilleFactory.buildFEE900ClientOrders().getId(),
						DimensionType.ATTRIBUTE,
						MaterializedGilleFactory.buildFEE900ClientOrders().getName(), 1L));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(4, MaterializedGilleFactory.buildFEE900ClientOrders().getId(),
						DimensionType.MEASURE, MaterializedGilleFactory.buildFEE900ClientOrders().getName(), 3L));
		join.getColumnListChangeHandler().addNew(buildColumn(5,  MaterializedGilleFactory.buildFEE900ClientOrders().getId(),
				DimensionType.ATTRIBUTE, MaterializedGilleFactory.buildFEE900ClientOrders().getName(), 4L));
		join.getColumnListChangeHandler().addNew(buildColumn( 6,null,DimensionType.PERIOD , "Billing event date",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 7,null,DimensionType.ATTRIBUTE , "Billing event status",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 8,null,DimensionType.ATTRIBUTE , "Billing event nature",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 9,null,DimensionType.ATTRIBUTE , "Billing event category",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 10,null,DimensionType.MEASURE , "Billing driver",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 11,null,DimensionType.ATTRIBUTE , "Billing event description",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 12,null,DimensionType.MEASURE , "Check",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 13,null,DimensionType.ATTRIBUTE , "Data source",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 14,null,DimensionType.ATTRIBUTE , "Billing event type",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 15,null,DimensionType.ATTRIBUTE , "Billing group 1",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 16,null,DimensionType.ATTRIBUTE , "Billing group 2",null));
		join.getColumnListChangeHandler().addNew(buildColumn(17,  MaterializedGilleFactory.buildPRI001PricingGridMembershipYearlyFee().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildPRI001PricingGridMembershipYearlyFee().getName(), 3L));
		join.getColumnListChangeHandler().addNew(buildColumn( 18,null,DimensionType.MEASURE , "Billing amount",null));
		join.setRefreshGridsBeforePublication(true);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridName("BIL100 Membership yearly fee - billing events");
		join.setAddPublicationRunNbr(true);
		join.setConditionListChangeHandler(null);
		return join;

	}
	
	public static Join buildBIL200MembershipFeeVolumeFee() throws Exception {
		Join join = new Join();
		List<JoinGrid> grids = new ArrayList<>();
		List<JoinKey> keys = new ArrayList<>();

		keys.add(buildJoinkey(JoinGridType.MATERIALIZED_GRID,
				MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getId(), 0L,
				JoinGridType.MATERIALIZED_GRID, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(), 0L));
		grids.add(buildJoingrid(1, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getName(),
				JoinGridType.MATERIALIZED_GRID, MaterializedGilleFactory.buildMaterializedGrilles().get(0).getId()));
		grids.add(buildJoingrid(2, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(),
				JoinGridType.MATERIALIZED_GRID, MaterializedGilleFactory.buildMaterializedGrilles().get(24).getId()));
		join.setName("BIL200 Membership fee - volume fee");
		join.setGrids(grids);
		join.setKeys(keys);
		join.setVisibleInShortcut(true);
		join.getColumnListChangeHandler()
				.addNew(buildColumn(1, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getId(),
						DimensionType.ATTRIBUTE,
						MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getName(), 0L));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(2, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getId(),
						DimensionType.ATTRIBUTE,
						MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getName(), 6L));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(3, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getId(),
						DimensionType.ATTRIBUTE,
						MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getName(), 1L));
		join.getColumnListChangeHandler()
				.addNew(buildColumn(4, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getId(),
						DimensionType.ATTRIBUTE, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getName(), 5L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(5, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getName(), 2L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(6, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildBIL010MAVolumePerClientPerInvoicePivot().getName(), 3L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(7, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(),
				DimensionType.ATTRIBUTE, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(), 2L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(8, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(), 3L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(9, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(), 4L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(10, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(), 5L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(11, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(), 6L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(12, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(), 7L));
		join.getColumnListChangeHandler()
		.addNew(buildColumn(13, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getId(),
				DimensionType.MEASURE, MaterializedGilleFactory.buildPRI002PricingGridMembershipVolumeFee().getName(), 10L));
		join.getColumnListChangeHandler().addNew(buildColumn( 14,null,DimensionType.MEASURE , "Effective rate - step 1",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 15,null,DimensionType.MEASURE , "Effective rate - step 2",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 16,null,DimensionType.MEASURE , "Effective rate - step 3",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 17,null,DimensionType.MEASURE , "Effective rate - step 4",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 18,null,DimensionType.MEASURE , "Unit cost",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 19,null,DimensionType.MEASURE , "Billing driver",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 20,null,DimensionType.PERIOD , "Billing event date",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 21,null,DimensionType.ATTRIBUTE , "Billing event status",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 22,null,DimensionType.ATTRIBUTE , "Billing event nature",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 23,null,DimensionType.ATTRIBUTE , "Billing event description",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 24,null,DimensionType.ATTRIBUTE , "Billing event category",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 25,null,DimensionType.ATTRIBUTE , "Data source",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 26,null,DimensionType.ATTRIBUTE , "Billing event type",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 27,null,DimensionType.ATTRIBUTE , "Billing group 1",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 28,null,DimensionType.ATTRIBUTE , "Billing group 2",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 29,null,DimensionType.PERIOD , "Entry date",null));
		join.getColumnListChangeHandler().addNew(buildColumn( 30,null,DimensionType.MEASURE , "Billing amount",null));
		join.setRefreshGridsBeforePublication(true);
		join.setPublicationDataSourceType(DataSourceType.INPUT_GRID);
		join.setPublicationMethod(JoinPublicationMethod.APPEND);
		join.setPublicationGridName("BIL200 Membership volume fee - billing event generation");
		join.setConditionListChangeHandler(null);
		return join;
	}
	
	
	
	public static JoinGrid buildJoingrid(int position,String name,JoinGridType gridType,Long gridId) {
		JoinGrid joinGrid = new JoinGrid();
		joinGrid.setGridId(gridId);
		joinGrid.setGridType(gridType);
		joinGrid.setName(name);
		joinGrid.setPosition(position);
		return joinGrid;
	}
	
	public static JoinCondition buildJoinCondition() {
		JoinCondition joinCondition = new JoinCondition() ;
		joinCondition.setVerb("And");
		joinCondition.setOpeningBracket("(");
		return joinCondition;
	}
	
	public static JoinColumn buildColumn(int position,Long gridId,DimensionType type, String dimensionName, Long columnId) {
		JoinColumn column = new JoinColumn();
		column.setName(dimensionName);
		column.setColumnId(columnId);
		column.setColumnName(dimensionName);
		column.setDimensionName(dimensionName);
		column.setDimensionId(columnId);
		column.setPosition(position);
		column.setType(type);
		column.setUsedForPublication(true);
		column.setPublicationDimensionId(columnId);
		column.setPublicationDimensionName(dimensionName);
		column.setGridId(gridId);
		return column;
		
	}
	
	public static JoinKey buildJoinkey(JoinGridType joinGridType1, Long gridType1,Long columId1,JoinGridType joinGridType2, Long gridType2,Long columId2) {
		JoinKey joinkey = new JoinKey();
		joinkey.setGridType1(joinGridType1);
		joinkey.setGridId1(gridType1);
		joinkey.setColumnId1(columId1);
		joinkey.setGridType2(joinGridType2);
		joinkey.setGridId2(gridType2);
		joinkey.setColumnId2(columId2);
		return joinkey;
	}

}
