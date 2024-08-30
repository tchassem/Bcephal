/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.AttributeValue;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.EnrichmentValue;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.AbstractSmartGridColumn;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class ReconciliationUnionData {

	private Long reconciliationId;
	private List<String> leftids;
	private List<String> rightids;
	
	private boolean selectAllRowsInLeftGrid;
	private UnionGridFilter leftBrowserDataFilter;
	
	private boolean selectAllRowsInRightGrid;
	private UnionGridFilter rightBrowserDataFilter;
	
	@JsonIgnore
	private UnionGrid leftGrid;
	@JsonIgnore
	private Grille leftGrille;
	private JoinGridType leftGridType;
	private Long leftGridId;
    private Long leftRecoTypeId;
    private Long leftMeasureId;	
    private Long leftPartialRecoAttributeId;
    private Long leftPartialRecoSequenceId;
    private Long leftReconciliatedMeasureId;
    private Long leftRemainningMeasureId;
    private Long leftFreezeAttributeId;
    private Long leftNeutralizationAttributeId;
    private Long leftNoteAttributeId;
    private Long leftRecoDateId;    
    private Long leftDCId;
    private Long leftUserColumnId;
	private Long leftModeColumnId;

    @JsonIgnore
	private UnionGrid rightGrid;
    @JsonIgnore
	private Grille rightGrille;
    private JoinGridType rightGridType;
    private Long rightGridId;
    private Long rightRecoTypeId;
    private Long rightMeasureId;
    private Long rightPartialRecoAttributeId;
    private Long rightPartialRecoSequenceId;
    private Long rightReconciliatedMeasureId;
    private Long rightRemainningMeasureId;
    private Long rightFreezeAttributeId;
    private Long rightNeutralizationAttributeId;
    private Long rightNoteAttributeId;
    private Long rightRecoDateId;
    private Long rightDCId;
    private Long rightUserColumnId;
	private Long rightModeColumnId;
	
	private boolean useDebitCredit;	
	private boolean performPartialReco;
    private boolean allowPartialReco;
    private boolean allowFreeze;
    private boolean performNeutralization;
    private boolean allowNeutralization;
    private boolean addRecoDate;	
	private boolean addUser;	
	private boolean addNote;	
	private boolean mandatoryNote;
	private boolean addAutomaticManual;	

	private Long recoSequenceId;
	private Long partialRecoSequenceId;
	private Long freezeSequenceId;
    private Long neutralizationSequenceId;
    
    private String neutralizationValue;
    private String note;	
    
	private BigDecimal writeOffAmount;
	private BigDecimal leftAmount;
	private BigDecimal rigthAmount;	
	private BigDecimal balanceAmount;
	private BigDecimal reconciliatedAmount;
	@Enumerated(EnumType.STRING) 
	private ReconciliationModelSide writeOffSide;
	private boolean useGridMeasureForWriteOff;
	private Long writeOffGridId;
    private Long writeOffMeasureId;
	private Long writeOffTypeColumnId;
	private String writeOffTypeValue;
	private List<WriteOffUnionField> writeOffFields;
	
	private boolean allowReconciliatedAmountLog;	
	private Long reconciliatedAmountLogGridId;
	
	public List<PartialRecoItem> partialRecoItems;
		
	private Attribute autoManualAttribute;
	private AttributeValue automaticValue;
	private AttributeValue manualValue;
	private Attribute userAttribute;
	
	private Integer defaultRecoValue = null;
	
	private String primaryView;
	private String SecondaryView;
	private List<String> primaryids;
	private List<String> secondaryids;
	
	private List<EnrichmentValue> enrichmentItemDatas;
	
	
	public ReconciliationUnionData() {
		leftids = new ArrayList<>(0);
		rightids = new ArrayList<>(0);
		writeOffAmount = BigDecimal.ZERO;
		leftAmount = BigDecimal.ZERO;
		rigthAmount = BigDecimal.ZERO;	
		balanceAmount = BigDecimal.ZERO;
		reconciliatedAmount = BigDecimal.ZERO;
		primaryids = new ArrayList<>(0);
		secondaryids = new ArrayList<>(0);
		writeOffFields = new ArrayList<>(0);
		enrichmentItemDatas = new ArrayList<>(0);
		partialRecoItems = new ArrayList<>(0);
	}
	
	public List<EnrichmentValue> getEnrichmentsBySide(ReconciliationModelSide side) {
		List<EnrichmentValue> values = new ArrayList<>();
		if(getEnrichmentItemDatas() != null) {
			int position = 1;
			for(EnrichmentValue enrichmentValue : getEnrichmentItemDatas()) {
				if(!enrichmentValue.isValid()) {
					throw new BcephalException("Invalid enrichement at : " + position);
				}
				position++;			
				if(side.name().equalsIgnoreCase(enrichmentValue.getSide())) {
					values.add(enrichmentValue);
				}
			}
		}
		return values;
	}
	
	public List<String> buildReconciliationSql(boolean forLeft, String username, RunModes mode) {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionReconciliationSql(leftGrid, forLeft, username, mode);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionReconciliationSql(rightGrid, forLeft, username, mode);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseReconciliationSql(leftGrille, forLeft, username, mode);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseReconciliationSql(rightGrille, forLeft, username, mode);
		}
		return new ArrayList<>();
	}
	
	protected List<String> buildUniverseReconciliationSql(Grille grid, boolean forLeft, String username, RunModes mode) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		if(ids.size() > 0 && grid != null) {
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
			String col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, true);
			String sql = "UPDATE " + gridDbName + " SET " + col + " = :recoNumber";
					
			if(isAllowPartialReco()) {
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, true);				
				sql += ", " + col + " = :partialRecoNumber";
				if(isPerformPartialReco() && mode == RunModes.A) {
					col = getUniverseDbColumnName(forLeft, forLeft ? getLeftMeasureId() : getRightMeasureId(), "Reco measure", DimensionType.MEASURE, grid, true);
					String reconciliated = getUniverseDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, true);
					String remaining = getUniverseDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, true);			
					if(mode == RunModes.A) {					
						sql += ", " + reconciliated + " = " + col;
						sql += ", " + remaining + " = 0";						
					}					
				}
			}
			if(isPerformNeutralization()) {	
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNeutralizationAttributeId() : getRightNeutralizationAttributeId(), "Neutralization type", DimensionType.ATTRIBUTE, grid, true);	
				sql += ", " + col + " = :neutralizationNumber";
			}	
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = :date";
			}
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getRightNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddNote() && col != null && StringUtils.hasText(getNote())) {
				sql += ", " + col + " = :note";
			}	
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddUser() && username != null && col != null) {
				sql += ", " + col + " = :username";
			}
						
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddAutomaticManual() && mode != null && col != null) {
				sql += ", " + col + " = :mode";
			}
		
			List<Number> result = buildUniverseIds(ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " WHERE ";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}	
			}			
			sqls.add(sql);
		}
		return sqls;
	}
	
	protected List<String> buildUnionReconciliationSql(UnionGrid grid, boolean forLeft, String username, RunModes mode) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		if(ids.size() > 0 && grid != null) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
				String sql = buildReconciliationSql(grid, item, ids, forLeft, username, mode);
				if(StringUtils.hasText(sql)) {
					sqls.add(sql);
				}
			}
		}
		return sqls;
	}
	
	private String buildReconciliationSql(UnionGrid grid, UnionGridItem item, List<String> ids, boolean forLeft, String username, RunModes mode) {
		String sql = null;
		if(item.getGrid() != null) {
			String gridDbName = item.getGrid().getDbTableName();
			String col = getDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, item, true);
			sql = "UPDATE " + gridDbName + " SET " + col + " = :recoNumber";
					
			if(isAllowPartialReco()) {
				col = getDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, item, true);				
				sql += ", " + col + " = :partialRecoNumber";
				if(isPerformPartialReco() && mode == RunModes.A) {
					col = getDbColumnName(forLeft, forLeft ? getLeftMeasureId() : getRightMeasureId(), "Reco measure", DimensionType.MEASURE, grid, item, true);
					String reconciliated = getDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, item, true);
					String remaining = getDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, item, true);			
					if(mode == RunModes.A) {					
						sql += ", " + reconciliated + " = " + col;
						sql += ", " + remaining + " = 0";						
					}					
				}
			}
			if(isPerformNeutralization()) {	
				col = getDbColumnName(forLeft, forLeft ? getLeftNeutralizationAttributeId() : getRightNeutralizationAttributeId(), "Neutralization type", DimensionType.ATTRIBUTE, grid, item, true);	
				sql += ", " + col + " = :neutralizationNumber";
			}	
			col = getDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = :date";
			}
			col = getDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getRightNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddNote() && col != null && StringUtils.hasText(getNote())) {
				sql += ", " + col + " = :note";
			}	
			col = getDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddUser() && username != null && col != null) {
				sql += ", " + col + " = :username";
			}
						
			col = getDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddAutomaticManual() && mode != null && col != null) {
				sql += ", " + col + " = :mode";
			}
		
			List<Number> result = buildIds(item.getGrid().getId(), ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " WHERE ";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}	
			}
			
		}
		return sql;
	}
	
	
	
	
	public List<String> buildFreezeSql(boolean forLeft, String username, RunModes mode) {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionFreezeSql(leftGrid, forLeft, username, mode);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionFreezeSql(rightGrid, forLeft, username, mode);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseFreezeSql(leftGrille, forLeft, username, mode);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseFreezeSql(rightGrille, forLeft, username, mode);
		}
		return new ArrayList<>();
	}
	
	protected List<String> buildUniverseFreezeSql(Grille grid, boolean forLeft, String username, RunModes mode) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		if(ids.size() > 0 && grid != null) {
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
			String col = getUniverseDbColumnName(forLeft, forLeft ? getLeftFreezeAttributeId() : getRightFreezeAttributeId(), "Freeze type", DimensionType.ATTRIBUTE, grid, true);
			String sql = "UPDATE " + gridDbName + " SET " + col + " = :freezeNumber";
					
			if(isAllowPartialReco()) {
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, true);				
				sql += ", " + col + " = :partialRecoNumber";
				if(isPerformPartialReco() && mode == RunModes.A) {
					col = getUniverseDbColumnName(forLeft, forLeft ? getLeftMeasureId() : getRightMeasureId(), "Reco measure", DimensionType.MEASURE, grid, true);
					String reconciliated = getUniverseDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, true);
					String remaining = getUniverseDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, true);			
					if(mode == RunModes.A) {					
						sql += ", " + reconciliated + " = " + col;
						sql += ", " + remaining + " = 0";						
					}					
				}
			}
			if(isPerformNeutralization()) {	
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNeutralizationAttributeId() : getRightNeutralizationAttributeId(), "Neutralization type", DimensionType.ATTRIBUTE, grid, true);	
				sql += ", " + col + " = :neutralizationNumber";
			}	
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = :date";
			}
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getRightNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddNote() && col != null && StringUtils.hasText(getNote())) {
				sql += ", " + col + " = :note";
			}	
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddUser() && username != null && col != null) {
				sql += ", " + col + " = :username";
			}
						
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddAutomaticManual() && mode != null && col != null) {
				sql += ", " + col + " = :mode";
			}
		
			List<Number> result = buildUniverseIds(ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " WHERE ";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}	
			}
			sqls.add(sql);
		}
		return sqls;
	}
	
	protected List<String> buildUnionFreezeSql(UnionGrid grid, boolean forLeft, String username, RunModes mode) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		if(ids.size() > 0 && grid != null) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
				String sql = buildFreezeSql(grid, item, ids, forLeft, username, mode);
				if(StringUtils.hasText(sql)) {
					sqls.add(sql);
				}
			}
		}
		return sqls;
	}
	
	private String buildFreezeSql(UnionGrid grid, UnionGridItem item, List<String> ids, boolean forLeft, String username, RunModes mode) {
		String sql = null;
		if(item.getGrid() != null) {
			String gridDbName = item.getGrid().getDbTableName();
			String col = getDbColumnName(forLeft, forLeft ? getLeftFreezeAttributeId() : getRightFreezeAttributeId(), "Freeze type", DimensionType.ATTRIBUTE, grid, item, true);
			sql = "UPDATE " + gridDbName + " SET " + col + " = :freezeNumber";
					
			if(isAllowPartialReco()) {
				col = getDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, item, true);				
				sql += ", " + col + " = :partialRecoNumber";
				if(isPerformPartialReco() && mode == RunModes.A) {
					col = getDbColumnName(forLeft, forLeft ? getLeftMeasureId() : getRightMeasureId(), "Reco measure", DimensionType.MEASURE, grid, item, true);
					String reconciliated = getDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, item, true);
					String remaining = getDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, item, true);			
					if(mode == RunModes.A) {					
						sql += ", " + reconciliated + " = " + col;
						sql += ", " + remaining + " = 0";						
					}					
				}
			}
			if(isPerformNeutralization()) {	
				col = getDbColumnName(forLeft, forLeft ? getLeftNeutralizationAttributeId() : getRightNeutralizationAttributeId(), "Neutralization type", DimensionType.ATTRIBUTE, grid, item, true);	
				sql += ", " + col + " = :neutralizationNumber";
			}	
			col = getDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = :date";
			}
			col = getDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getRightNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddNote() && col != null && StringUtils.hasText(getNote())) {
				sql += ", " + col + " = :note";
			}	
			col = getDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddUser() && username != null && col != null) {
				sql += ", " + col + " = :username";
			}
						
			col = getDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddAutomaticManual() && mode != null && col != null) {
				sql += ", " + col + " = :mode";
			}
		
			List<Number> result = buildIds(item.getGrid().getId(), ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " WHERE (";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}
				sql += ")";
			}
			
		}
		return sql;
	}
	
	
	public void setIdAndGridId(PartialRecoItem item, JoinGridType type, String id) {
		if(type.isUnionGrid()) {
			String[] val = id.split("_");
			if(val.length > 1) {
				item.setGridId(Long.valueOf(val[1]));
				item.setId(Long.valueOf(val[0]));
			}
		}
		else {
			String[] val = id.split("_");
			if(val.length > 0 && val[0] != null) {
				item.setId(Long.valueOf(val[0]));
			}
		}
	}
	
	
	public List<Number> buildIds(Long gridId, List<String> ids){
		List<Number> result = new ArrayList<>();
		for (String id : ids) {
			String[] val = id.split("_");
			if(val.length > 0 && val[1] != null && gridId.equals(Long.valueOf(val[1]))) {
				result.add(Long.valueOf(val[0]));
			}
		}	
		return result;
	}
	
	public List<Number> buildUniverseIds(List<String> ids){
		List<Number> result = new ArrayList<>();
		for (String id : ids) {
			String[] val = id.split("_");
			if(val.length > 0 && val[0] != null) {
				result.add(Long.valueOf(val[0]));
			}
		}	
		return result;
	}
	
	public HashMap<Long,String> buildReconciliationPartialSqlFormManualMode(boolean forLeft, boolean forPublication) {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildReconciliationPartialSqlFormManualModeForUnion(leftGrid, forLeft);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildReconciliationPartialSqlFormManualModeForUnion(rightGrid, forLeft);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildReconciliationPartialSqlFormManualModeForUniverse(leftGrille, forLeft);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildReconciliationPartialSqlFormManualModeForUniverse(rightGrille, forLeft);
		}		
		return new HashMap<>();
	}	
	
	private HashMap<Long,String> buildReconciliationPartialSqlFormManualModeForUniverse(Grille grid, boolean forLeft) {
		HashMap<Long, String> sqls = new HashMap<>();	
		if(getPartialRecoItems().size() > 0 && grid != null && isAllowPartialReco() && isPerformPartialReco()) {
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
			String reconciliated = getUniverseDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, true);
			String remaining = getUniverseDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, true);
			String sql = "UPDATE " + gridDbName + " SET "
					+ reconciliated + " = :reconciliatedAmount"
					+ ", " + remaining + " = :remainingAmount"
					+ " WHERE id = :id";
			sqls.put(grid.getId(), sql);
		}
		return sqls;
	}
		
	private HashMap<Long,String> buildReconciliationPartialSqlFormManualModeForUnion(UnionGrid grid, boolean forLeft) {
		HashMap<Long, String> sqls = new HashMap<>();	
		if(getPartialRecoItems().size() > 0 && grid != null && isAllowPartialReco() && isPerformPartialReco()) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {			
				if(item.getGrid() != null) {
					String gridDbName = item.getGrid().getDbTableName();
					String reconciliated = getDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, item, true);
					String remaining = getDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, item, true);
					String sql = "UPDATE " + gridDbName + " SET "
							+ reconciliated + " = :reconciliatedAmount"
							+ ", " + remaining + " = :remainingAmount"
							+ " WHERE id = :id";
					sqls.put(item.getGrid().getId(), sql);
				}
			}
		}
		return sqls;
	}
	
	
	public List<String> buildEnrichmentSql(boolean forLeft, ReconciliationUnionData data) throws Exception {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionEnrichmentSql(leftGrid, forLeft, data);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionEnrichmentSql(rightGrid, forLeft, data);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseEnrichmentSql(leftGrille, forLeft, data);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseEnrichmentSql(rightGrille, forLeft, data);
		}
		return new ArrayList<>();
	}
	
	protected List<String> buildUniverseEnrichmentSql(Grille grid, boolean forLeft, ReconciliationUnionData data) throws Exception {
		parameters = new ArrayList<>(0);
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		
		ReconciliationModelSide side = forLeft ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT;
		List<EnrichmentValue> values = getEnrichmentsBySide(side);
		int count = values.size();
		String sql = null;
		if(count > 0 && ids.size() > 0 && grid != null) {				
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
			sql = "UPDATE " + gridDbName + " SET ";	
			int i = 1;	
			String coma = "";
			for(EnrichmentValue enrichmentValue : values) {
				String col = getUniverseDbColumnName(forLeft, enrichmentValue.getDimensionId(), "Enrichment", enrichmentValue.getDimensionType(), grid, true);
				if(enrichmentValue.getValue() != null) {
					sql += coma + col  + " = :enrichmentItemData" + i++;
				}
				else {
					sql += coma + col  + " = NULL";
				}
				coma = ",";			
				parameters.add(enrichmentValue.getValue());				
			}
			List<Number> result = buildUniverseIds(ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " WHERE ";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}	
			}
			sqls.add(sql);
		}
		return sqls;
	}
	
	protected List<String> buildUnionEnrichmentSql(UnionGrid grid, boolean forLeft, ReconciliationUnionData data) throws Exception {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		if(ids.size() > 0 && grid != null) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
				String sql = buildUnionEnrichmentSql(grid, item, ids, forLeft, data);
				if(StringUtils.hasText(sql)) {
					sqls.add(sql);
				}
			}
		}
		return sqls;
	}
	
	private String buildUnionEnrichmentSql(UnionGrid grid, UnionGridItem item, List<String> ids, boolean forLeft, ReconciliationUnionData data) throws Exception {
		parameters = new ArrayList<>(0);
		ReconciliationModelSide side = forLeft ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT;
		List<EnrichmentValue> values = getEnrichmentsBySide(side);
		int count = values.size();
		String sql = null;
		if(count > 0) {				
			String gridDbName = item.getGrid().getDbTableName();			
			sql = "UPDATE " + gridDbName + " SET ";	
			int i = 1;	
			String coma = "";
			for(EnrichmentValue enrichmentValue : values) {
				String col = getDbColumnName(forLeft, enrichmentValue.getDimensionId(), "Enrichment", enrichmentValue.getDimensionType(), grid, item, true);
				if(enrichmentValue.getValue() != null) {
					sql += coma + col  + " = :enrichmentItemData" + i++;
				}
				else {
					sql += coma + col  + " = NULL";
				}
				coma = ",";			
				parameters.add(enrichmentValue.getValue());				
			}
			List<Number> result = buildIds(item.getGrid().getId(), ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " WHERE ";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}	
			}
		}
		return sql;
	}
	
	
	
	public List<String> buildResetEnrichmentSql(boolean forLeft, ReconciliationUnionData data, List<Object[]> numbers, List<Object[]> partialnumbers) throws Exception {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionResetEnrichmentSql(leftGrid, forLeft, data, numbers, partialnumbers);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionResetEnrichmentSql(rightGrid, forLeft, data, numbers, partialnumbers);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseResetEnrichmentSql(leftGrille, forLeft, data, numbers, partialnumbers);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseResetEnrichmentSql(rightGrille, forLeft, data, numbers, partialnumbers);
		}
		return new ArrayList<>();
	}
		
	protected List<String> buildUniverseResetEnrichmentSql(Grille grid, boolean forLeft, ReconciliationUnionData data, List<Object[]> numbers, List<Object[]> partialnumbers) throws Exception {
		parameters = new ArrayList<>(0);
		List<String> sqls = new ArrayList<>();
		ReconciliationModelSide side = forLeft ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT;
		List<EnrichmentValue> values = getEnrichmentsBySide(side);
		int count = values.size();
		String sql = null;
		if(count > 0 && numbers.size() > 0 && grid != null) {	
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
			sql = "UPDATE " + gridDbName + " SET ";	
			String coma = "";
			for(EnrichmentValue enrichmentValue : values) {
				String col = getUniverseDbColumnName(forLeft, enrichmentValue.getDimensionId(), "Enrichment", enrichmentValue.getDimensionType(), grid, true);
				sql += coma + col  + " = NULL";
				coma = ",";					
			}
						
			String col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, true);
			sql += " WHERE (" + col + " IN (";
			for (Object object : numbers) {					
				sql += coma + "'" +  object + "'";
				coma = ", ";				
			}				
			sql += ")";
			
			if(data.isAllowPartialReco() && !partialnumbers.isEmpty()) {
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial Reco type", DimensionType.ATTRIBUTE, grid, true);
				sql += " OR " + col + " IN (";
				coma = "";
				for (Object object : numbers) {		
					sql += coma + "'" + object + "'";
					coma = ", ";
				}
				sql += ")";
			}
			sql +=  ")";	
			sqls.add(sql);
		}
		return sqls;
	}
	
	protected List<String> buildUnionResetEnrichmentSql(UnionGrid grid, boolean forLeft, ReconciliationUnionData data, List<Object[]> numbers, List<Object[]> partialnumbers) throws Exception {
		List<String> sqls = new ArrayList<>();		
		if(numbers.size() > 0 && grid != null) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
				String sql = buildUnionResetEnrichmentSql(grid, item, forLeft, data, numbers, partialnumbers);
				if(StringUtils.hasText(sql)) {
					sqls.add(sql);
				}
			}
		}
		return sqls;
	}
	
	private String buildUnionResetEnrichmentSql(UnionGrid grid, UnionGridItem item, boolean forLeft, ReconciliationUnionData data, List<Object[]> numbers, List<Object[]> partialnumbers) throws Exception {
		parameters = new ArrayList<>(0);
		ReconciliationModelSide side = forLeft ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT;
		List<EnrichmentValue> values = getEnrichmentsBySide(side);
		int count = values.size();
		String sql = null;
		if(count > 0 && numbers.size() > 0 && grid != null) {
			String gridDbName = item.getGrid().getDbTableName();			
			sql = "UPDATE " + gridDbName + " SET ";	
			String coma = "";
			for(EnrichmentValue enrichmentValue : values) {
				String col = getDbColumnName(forLeft, enrichmentValue.getDimensionId(), "Enrichment", enrichmentValue.getDimensionType(), grid, item, true);
				sql += coma + col  + " = NULL";
				coma = ",";				
			}
			String col = getDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, item, true);
			sql += " WHERE (" + col + " IN (";
			for (Object object : numbers) {					
				sql += coma + "'" +  object + "'";
				coma = ", ";				
			}				
			sql += ")";
			
			if(data.isAllowPartialReco() && !partialnumbers.isEmpty()) {
				col = getDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial Reco type", DimensionType.ATTRIBUTE, grid, item, true);
				sql += " OR " + col + " IN (";
				coma = "";
				for (Object object : numbers) {		
					sql += coma + "'" + object + "'";
					coma = ", ";
				}
				sql += ")";
			}
			sql +=  ")";	
		}		
		return sql;
	}
	
	
	
	
	
	List<Object> parameters = new ArrayList<>(0);
	
	public String buildWriteOffSql(String recoNumber, Date recoDate, String username, RunModes mode) {	
		if (getWriteOffSide() == null || getWriteOffAmount() == null || getWriteOffAmount().compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		if(getWriteOffSide().isLeft() && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionWriteOffSql(leftGrid, recoNumber, recoDate, username, mode);
		}
		else if(getWriteOffSide().isRight() && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionWriteOffSql(rightGrid, recoNumber, recoDate, username, mode);
		}
		
		else if(getWriteOffSide().isLeft() && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseWriteOffSql(leftGrille, recoNumber, recoDate, username, mode);
		}
		else if(getWriteOffSide().isRight() && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseWriteOffSql(rightGrille, recoNumber, recoDate, username, mode);
		}
		return null;
	}
	
	public String buildUniverseWriteOffSql(Grille grid, String recoNumber, Date recoDate, String username, RunModes mode) {		
		boolean forLeft = getWriteOffSide() == ReconciliationModelSide.LEFT;
		String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
		String sql = null;		
		String coma = "";
		String stringQuery = "INSERT INTO " + gridDbName + " (ID";
		String valueQuery = "values(DEFAULT";
		parameters = new ArrayList<>(0);
		coma = ", ";
		
		String col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, true);
		if (StringUtils.hasText(col)) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(recoNumber);
		}
		if( getWriteOffTypeColumnId() == null) {
			String massage = "The Write off type is not setted!";
			throw new BcephalException(massage);
		}
		col = getUniverseDbColumnName(forLeft, getWriteOffTypeColumnId(), "Write off type", DimensionType.ATTRIBUTE, grid, true);
		if (StringUtils.hasText(col)) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			String value = StringUtils.hasText(getWriteOffTypeValue()) ? getWriteOffTypeValue() : "WO";
			parameters.add(value);
		}
		
		Long colId = getWriteOffMeasureId();
		if(isUseGridMeasureForWriteOff()) {
			colId = forLeft ? getLeftMeasureId() : getRightMeasureId();
		}
		if(colId == null) {
			throw new BcephalException("The writeoff measure is undefined!");
		}		
		col = getUniverseDbColumnName(forLeft, colId, "Write off measure", DimensionType.MEASURE, grid, true);
		if(!StringUtils.hasText(col)) {
			throw new BcephalException("Target table does'nt contains the writeoff measure column!");
		}
		if (getWriteOffAmount() != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(getWriteOffAmount());
		}
		
		col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, false);	
		if(isAddRecoDate() && recoDate != null && col != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(recoDate);
		}
		col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getRightNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, false);	
		if(isAddNote() && col != null && StringUtils.hasText(getNote())) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(getNote());
		}	
		col = getUniverseDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, false);	
		if (isAddUser() && username != null && col != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(username);
		}					
		col = getUniverseDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, false);	
		if (isAddAutomaticManual() && mode != null && col != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(mode.name());
		}
		
		stringQuery += coma + UniverseParameters.SOURCE_TYPE;			
		valueQuery += coma + "?";
		parameters.add(UniverseSourceType.WRITEOFF.toString());
				
		stringQuery += coma + UniverseParameters.USERNAME;			
		valueQuery += coma + "?";
		parameters.add(username);
			
			
		for(WriteOffUnionField field : getWriteOffFields()){
			if(field.getDimensionId() != null) {
				col = getUniverseDbColumnName(forLeft, field.getDimensionId(), "Enrichment", field.getDimensionType(), grid, true);				
				stringQuery += coma + col;			
				valueQuery += coma + "?";
				if(field.getDimensionType().isAttribute()){
					parameters.add(field.getStringValue());
				}
				else if(field.getDimensionType().isMeasure()){
					parameters.add(field.getDecimalValue());
				}
				else if(field.getDimensionType().isPeriod()){
					parameters.add(field.getDateValue().buildDynamicDate());
				}				
			}			
		}
		sql = stringQuery + ")" + " " + valueQuery + ")";
		return sql;	
	}
	
	public String buildUnionWriteOffSql(UnionGrid grid, String recoNumber, Date recoDate, String username, RunModes mode) {
		boolean forLeft = getWriteOffSide() == ReconciliationModelSide.LEFT;
		if(getWriteOffGridId() == null) {
			throw new BcephalException("Write off grid is not setted!");
		}
		UnionGridItem item = null;
		for(UnionGridItem elt : grid.getItemListChangeHandler().getItems()) {			
			if(elt.getGrid() != null && getWriteOffGridId().equals(elt.getGrid().getId())) {
				item = elt;
				break;
			}
		}
		if(item == null) {
			throw new BcephalException("Write off grid not found : " + getWriteOffGridId());
		}
		
		String gridDbName = item.getGrid().getDbTableName();
		String sql = null;		
		String coma = "";
		String stringQuery = "INSERT INTO " + gridDbName + " (ID";
		String valueQuery = "values(DEFAULT";
		parameters = new ArrayList<>(0);
		coma = ", ";
		
		String col = getDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, item, true);
		if (StringUtils.hasText(col)) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(recoNumber);
		}
		
		col = getDbColumnName(forLeft, getWriteOffTypeColumnId(), "Write off type", DimensionType.ATTRIBUTE, grid, item, true);
		if (StringUtils.hasText(col)) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			String value = StringUtils.hasText(getWriteOffTypeValue()) ? getWriteOffTypeValue() : "WO";
			parameters.add(value);
		}
		
		Long colId = getWriteOffMeasureId();
		if(isUseGridMeasureForWriteOff()) {
			colId = forLeft ? getLeftMeasureId() : getRightMeasureId();
		}
		if(colId == null) {
			throw new BcephalException("The writeoff measure is undefined!");
		}		
		col = getDbColumnName(forLeft, colId, "Write off measure", DimensionType.MEASURE, grid, item, true);
		if(!StringUtils.hasText(col)) {
			throw new BcephalException("Target table does'nt contains the writeoff measure column!");
		}
		if (getWriteOffAmount() != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(getWriteOffAmount());
		}
		
		col = getDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, item, false);	
		if(isAddRecoDate() && recoDate != null && col != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(recoDate);
		}
		col = getDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getRightNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, item, false);	
		if(isAddNote() && col != null && StringUtils.hasText(getNote())) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(getNote());
		}	
		col = getDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, item, false);	
		if (isAddUser() && username != null && col != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(username);
		}					
		col = getDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, item, false);	
		if (isAddAutomaticManual() && mode != null && col != null) {
			stringQuery += coma + col;			
			valueQuery += coma + "?";
			parameters.add(mode.name());
		}
			
		for(WriteOffUnionField field : getWriteOffFields()){
			if(field.getDimensionId() != null) {
				col = getDbColumnName(forLeft, field.getDimensionId(), "Enrichment", field.getDimensionType(), grid, item, true);				
				stringQuery += coma + col;			
				valueQuery += coma + "?";
				if(field.getDimensionType().isAttribute()){
					parameters.add(field.getStringValue());
				}
				else if(field.getDimensionType().isMeasure()){
					parameters.add(field.getDecimalValue());
				}
				else if(field.getDimensionType().isPeriod()){
					parameters.add(field.getDateValue().buildDynamicDate());
				}				
			}			
		}
		sql = stringQuery + ")" + " " + valueQuery + ")";
		return sql;	
	}
		
	public List<String> buildResetRecoSql(boolean forLeft, List<Object[]> numbers, List<Object[]> partialNumbers) {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionResetRecoSql(leftGrid, forLeft, numbers, partialNumbers);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionResetRecoSql(rightGrid, forLeft, numbers, partialNumbers);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseResetRecoSql(leftGrille, forLeft, numbers, partialNumbers);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseResetRecoSql(rightGrille, forLeft, numbers, partialNumbers);
		}
		return new ArrayList<>();
	}
	
	protected List<String> buildUniverseResetRecoSql(Grille grid, boolean forLeft, List<Object[]> numbers, List<Object[]> partialNumbers) {
		List<String> sqls = new ArrayList<>();		
		if(grid != null) {
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
			String recoCol = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, true);
			String col = recoCol;
			String sql = "UPDATE " + gridDbName + " SET " + col + " = NULL";
			
			if(isAllowPartialReco()) {
				
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, true);				
				sql += ", " + col + " = NULL";
				
				String measureCol = getUniverseDbColumnName(forLeft, forLeft ? getLeftMeasureId() : getRightMeasureId(), "Reco measure", DimensionType.MEASURE, grid, true);				
				
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, true);					
				sql += ", " + col + " = 0";
				
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, true);					
				sql += ", " + col + " = " + measureCol;
					
			}
			if(isPerformNeutralization()) {	
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNeutralizationAttributeId() : getRightNeutralizationAttributeId(), "Neutralization type", DimensionType.ATTRIBUTE, grid, true);	
				sql += ", " + col + " = NULL";
			}	
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = NULL";
			}
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getRightNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddNote() && col != null) {
				sql += ", " + col + " = NULL";
			}	
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddUser() && col != null) {
				sql += ", " + col + " = NULL";
			}
						
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddAutomaticManual() && col != null) {
				sql += ", " + col + " = NULL";
			}
			
			sql += " WHERE (" + recoCol + " IN (";
			
			String coma = "";
			for (Object object : numbers) {					
				sql += coma + "'" +  object + "'";
				coma = ", ";			
			}			
			sql += ")";
					
			if(isAllowPartialReco() && partialNumbers.size() > 0) {
				col = getUniverseDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, true);
				sql += " OR " + col + " IN (";
				coma = "";
				for (Object object : partialNumbers) {		
					sql += coma + "'" + object + "'";
					coma = ", ";
				}
				sql += ")";
			}
			sql += ")";			
			sqls.add(sql);
		}
		return sqls;
	}
	
	protected List<String> buildUnionResetRecoSql(UnionGrid grid, boolean forLeft, List<Object[]> numbers, List<Object[]> partialNumbers) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		if(grid != null) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
				String sql = buildResetRecoSql(grid, item, ids, forLeft, numbers, partialNumbers);
				if(StringUtils.hasText(sql)) {
					sqls.add(sql);
				}
			}
		}
		return sqls;
	}
		
	public String buildResetRecoSql(UnionGrid grid, UnionGridItem item, List<String> ids, boolean forLeft, List<Object[]> numbers, List<Object[]> partialNumbers) {
		String sql = null;
		if(item.getGrid() != null) {
			String gridDbName = item.getGrid().getDbTableName();
			String recoCol = getDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, item, true);
			String col = recoCol;
			sql = "UPDATE " + gridDbName + " SET " + col + " = NULL";
			
			if(isAllowPartialReco()) {
				
				col = getDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, item, true);				
				sql += ", " + col + " = NULL";
				
				String measureCol = getDbColumnName(forLeft, forLeft ? getLeftMeasureId() : getRightMeasureId(), "Reco measure", DimensionType.MEASURE, grid, item, true);				
				
				col = getDbColumnName(forLeft, forLeft ? getLeftReconciliatedMeasureId() : getRightReconciliatedMeasureId(), "Reconciliated measure", DimensionType.MEASURE, grid, item, true);					
				sql += ", " + col + " = 0";
				
				col = getDbColumnName(forLeft, forLeft ? getLeftRemainningMeasureId() : getRightRemainningMeasureId(), "Remaining measure", DimensionType.MEASURE, grid, item, true);					
				sql += ", " + col + " = " + measureCol;
					
			}
			if(isPerformNeutralization()) {	
				col = getDbColumnName(forLeft, forLeft ? getLeftNeutralizationAttributeId() : getRightNeutralizationAttributeId(), "Neutralization type", DimensionType.ATTRIBUTE, grid, item, true);	
				sql += ", " + col + " = NULL";
			}	
			col = getDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = NULL";
			}
			col = getDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getLeftNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddNote() && col != null) {
				sql += ", " + col + " = NULL";
			}	
			col = getDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddUser() && col != null) {
				sql += ", " + col + " = NULL";
			}
						
			col = getDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddAutomaticManual() && col != null) {
				sql += ", " + col + " = NULL";
			}
			
			sql += " WHERE (" + recoCol + " IN (";
			
			String coma = "";
			for (Object object : numbers) {					
				sql += coma + "'" +  object + "'";
				coma = ", ";			
			}			
			sql += ")";
					
			if(isAllowPartialReco() && partialNumbers.size() > 0) {
				col = getDbColumnName(forLeft, forLeft ? getLeftPartialRecoAttributeId() : getRightPartialRecoAttributeId(), "Partial reco type", DimensionType.ATTRIBUTE, grid, item, true);
				sql += " OR " + col + " IN (";
				coma = "";
				for (Object object : partialNumbers) {		
					sql += coma + "'" + object + "'";
					coma = ", ";
				}
				sql += ")";
			}
			sql += ")";			
			
		}
		return sql;
	}
	
	
	
	public String buildResetWriteOffSql(List<Object[]> numbers) {
		if (getWriteOffSide() == null || numbers.size() == 0) {
			return null;
		}
		if(getWriteOffSide().isLeft() && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionResetWriteOffSql(leftGrid, numbers);
		}
		else if(getWriteOffSide().isRight() && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionResetWriteOffSql(rightGrid, numbers);
		}
		
		else if(getWriteOffSide().isLeft() && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseResetWriteOffSql(leftGrille, numbers);
		}
		else if(getWriteOffSide().isRight() && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseResetWriteOffSql(rightGrille, numbers);
		}
		return null;
	}
	
	protected String buildUniverseResetWriteOffSql(Grille grid, List<Object[]> numbers) {		
		boolean forLeft = getWriteOffSide() == ReconciliationModelSide.LEFT;
		String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
		String sql = null;	
		parameters = new ArrayList<>(0);		
		String recoCol = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, true);
		if (StringUtils.hasText(recoCol)) {
			String woCol = getUniverseDbColumnName(forLeft, getWriteOffTypeColumnId(), "Write off type", DimensionType.ATTRIBUTE, grid, true);
			if (StringUtils.hasText(woCol)) {
				sql = "DELETE FROM " + gridDbName 
						+ " WHERE " + woCol + " = ?"
						+ " AND " + recoCol + " IN (";	
				String value = StringUtils.hasText(getWriteOffTypeValue()) ? getWriteOffTypeValue() : "WO";
				parameters.add(value);
				String coma = "";
				for (Object object : numbers) {					
					sql += coma + "'" +  object + "'";
					coma = ", ";			
				}			
				sql += ")";
			}
		}
		return sql;
	}
	
	public String buildUnionResetWriteOffSql(UnionGrid grid, List<Object[]> numbers) {
		boolean forLeft = getWriteOffSide() == ReconciliationModelSide.LEFT;
		if(getWriteOffGridId() == null) {
			return null;
		}
		UnionGridItem item = null;
		for(UnionGridItem elt : grid.getItemListChangeHandler().getItems()) {
			if(getWriteOffGridId().equals(elt.getGrid().getId())) {
				item = elt;
				break;
			}
		}
		if(item == null) {
			throw new BcephalException("Write off grid not found : " + getWriteOffGridId());
		}
		String sql = null;	
		String gridDbName = item.getGrid().getDbTableName();
				
		parameters = new ArrayList<>(0);		
		String recoCol = getDbColumnName(forLeft, forLeft ? getLeftRecoTypeId() : getRightRecoTypeId(), "Reco type", DimensionType.ATTRIBUTE, grid, item, true);
		if (StringUtils.hasText(recoCol)) {
			String woCol = getDbColumnName(forLeft, getWriteOffTypeColumnId(), "Write off type", DimensionType.ATTRIBUTE, grid, item, true);
			if (StringUtils.hasText(woCol)) {
				sql = "DELETE FROM " + gridDbName 
						+ " WHERE " + woCol + " = ?"
						+ " AND " + recoCol + " IN (";	
				String value = StringUtils.hasText(getWriteOffTypeValue()) ? getWriteOffTypeValue() : "WO";
				parameters.add(value);
				String coma = "";
				for (Object object : numbers) {					
					sql += coma + "'" +  object + "'";
					coma = ", ";			
				}			
				sql += ")";
			}
		}
		return sql;
	}
		
	
	
	public List<String> buildUnfreezeSql(boolean forLeft, List<Object[]> numbers) {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionUnfreezeSql(leftGrid, forLeft, numbers);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionUnfreezeSql(rightGrid, forLeft, numbers);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseUnfreezeSql(leftGrille, forLeft, numbers);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseUnfreezeSql(rightGrille, forLeft, numbers);
		}
		return new ArrayList<>();
	}
	
	protected List<String> buildUniverseUnfreezeSql(Grille grid, boolean forLeft, List<Object[]> numbers){
		List<String> sqls = new ArrayList<>();	
		if(grid != null) {
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;;
			String recoCol = getUniverseDbColumnName(forLeft, forLeft ? getLeftFreezeAttributeId() : getRightFreezeAttributeId(), "Freeze type", DimensionType.ATTRIBUTE, grid, true);
			String col = recoCol;
			String sql = "UPDATE " + gridDbName + " SET " + col + " = NULL";
			
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = NULL";
			}
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getLeftNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, false);	
			if(isAddNote() && col != null) {
				sql += ", " + col + " = NULL";
			}	
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddUser() && col != null) {
				sql += ", " + col + " = NULL";
			}
						
			col = getUniverseDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, false);	
			if (isAddAutomaticManual() && col != null) {
				sql += ", " + col + " = NULL";
			}
			
			sql += " WHERE " + recoCol + " IN (";
			
			String coma = "";
			for (Object object : numbers) {					
				sql += coma + "'" +  object + "'";
				coma = ", ";			
			}			
			sql += ")";
			sqls.add(sql);
		}
		return sqls;
	}
	
	protected List<String> buildUnionUnfreezeSql(UnionGrid grid, boolean forLeft, List<Object[]> numbers) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		List<String> sqls = new ArrayList<>();		
		if(grid != null) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
				String sql = buildUnfreezeSql(grid, item, ids, forLeft, numbers);
				if(StringUtils.hasText(sql)) {
					sqls.add(sql);
				}
			}
		}
		return sqls;
	}
	
	
	public String buildUnfreezeSql(UnionGrid grid, UnionGridItem item, List<String> ids, boolean forLeft, List<Object[]> numbers) {
		String sql = null;
		if(item.getGrid() != null) {
			String gridDbName = item.getGrid().getDbTableName();
			String recoCol = getDbColumnName(forLeft, forLeft ? getLeftFreezeAttributeId() : getRightFreezeAttributeId(), "Freeze type", DimensionType.ATTRIBUTE, grid, item, true);
			String col = recoCol;
			sql = "UPDATE " + gridDbName + " SET " + col + " = NULL";
			
			col = getDbColumnName(forLeft, forLeft ? getLeftRecoDateId() : getRightRecoDateId(), "Reco date", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddRecoDate() && col != null) {
				sql += ", " + col + " = NULL";
			}
			col = getDbColumnName(forLeft, forLeft ? getLeftNoteAttributeId() : getLeftNoteAttributeId(), "Note", DimensionType.ATTRIBUTE, grid, item, false);	
			if(isAddNote() && col != null) {
				sql += ", " + col + " = NULL";
			}	
			col = getDbColumnName(forLeft, forLeft ? getLeftUserColumnId() : getRightUserColumnId(), "User", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddUser() && col != null) {
				sql += ", " + col + " = NULL";
			}
						
			col = getDbColumnName(forLeft, forLeft ? getLeftModeColumnId() : getRightModeColumnId(), "Mode", DimensionType.ATTRIBUTE, grid, item, false);	
			if (isAddAutomaticManual() && col != null) {
				sql += ", " + col + " = NULL";
			}
			
			sql += " WHERE " + recoCol + " IN (";
			
			String coma = "";
			for (Object object : numbers) {					
				sql += coma + "'" +  object + "'";
				coma = ", ";			
			}			
			sql += ")";
			
		}
		return sql;
	}
	
	
	
	public String buildGetColumnValuesSql(boolean forLeft, Long columnId) {
		if(forLeft && leftGridType.isUnionGrid() && leftGrid != null) {
			return buildUnionGetColumnValuesSql(leftGrid, forLeft, columnId);
		}
		else if(!forLeft && rightGridType.isUnionGrid() && rightGrid != null) {
			return buildUnionGetColumnValuesSql(rightGrid, forLeft, columnId);
		}
		
		else if(forLeft && !leftGridType.isUnionGrid() && leftGrille != null) {
			return buildUniverseGetColumnValuesSql(leftGrille, forLeft, columnId);
		}
		else if(!forLeft && !rightGridType.isUnionGrid() && rightGrille != null) {
			return buildUniverseGetColumnValuesSql(rightGrille, forLeft, columnId);
		}
		return null;
	}
		
	protected String buildUniverseGetColumnValuesSql(Grille grid, boolean forLeft, Long columnId) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		String sql = "";
		if(ids.size() > 0 && grid != null) {
			String gridDbName = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
			String col = getUniverseDbColumnName(forLeft, columnId, "Reco type", DimensionType.ATTRIBUTE, grid, true);			
			sql = "SELECT DISTINCT " + col + " AS reco FROM " + gridDbName
					+ " WHERE " + col + " IS NOT NULL AND " + col + " != ''";				
		
			List<Number> result = buildUniverseIds(ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " AND (";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}
				sql += ")";
			}
		}
		return sql;
	}
	
	protected String buildUnionGetColumnValuesSql(UnionGrid grid, boolean forLeft, Long columnId) {
		List<String> ids = forLeft ? getLeftids() : getRightids();
		String sqls = "";		
		String union = "";
		if(ids.size() > 0 && grid != null) {
			for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
				String sql = buildGetColumnValuesSql(grid, item, ids, forLeft, columnId);
				if(StringUtils.hasText(sql)) {
					sqls += union + sql;
					union = " UNION ";
				}
			}
		}
		return sqls;
	}
	
	private String buildGetColumnValuesSql(UnionGrid grid, UnionGridItem item, List<String> ids, boolean forLeft, Long columnId) {
		String sql = null;
		if(item.getGrid() != null) {
			String gridDbName = item.getGrid().getDbTableName();
			String col = getDbColumnName(forLeft, columnId/*forLeft ? getLeftRecoTypeId() : getRightRecoTypeId()*/, "Reco type", DimensionType.ATTRIBUTE, grid, item, true);			
			sql = "SELECT DISTINCT " + col + " AS reco FROM " + gridDbName
					+ " WHERE " + col + " IS NOT NULL AND " + col + " != ''";				
		
			List<Number> result = buildIds(item.getGrid().getId(), ids);
			if(result.isEmpty()) {
				sql = null;
			}
			else {
				String or = " AND (";
				for (Number id : result) {
					sql += or + " ID = " + id;
					or = " OR ";
				}
				sql += ")";
			}			
		}
		return sql;
	}
	
	@JsonIgnore
	public String getLeftGridDbName(boolean forPublication) {
		return getGridDbName(leftGridType, leftGridId, forPublication);
	}
	
	@JsonIgnore
	public String getRightGridDbName(boolean forPublication) {
		return getGridDbName(rightGridType, rightGridId, forPublication);
	}
	
	@JsonIgnore
	private String getGridDbName(JoinGridType type, Long id, boolean forPublication) {
		if(forPublication) {
			return new Grille(id).getPublicationTableName();
		}
		return type == JoinGridType.MATERIALIZED_GRID ? new MaterializedGrid(id).getMaterializationTableName()
						: type == JoinGridType.JOIN ? new Join(id).getMaterializationTableName() 
						: UniverseParameters.UNIVERSE_TABLE_NAME;
	}
	
	@JsonIgnore
	private String getDbColumnName(AbstractSmartGridColumn column, boolean forUniverse) {
		return forUniverse ? column.getUniverseColumnName() : column.getDbColumnName();
	}
		
	@JsonIgnore
	private String getDbColumnName(boolean forLeft, Long columnId, String columnDescription, DimensionType type, UnionGrid grid, UnionGridItem item, boolean mandatory) {
		SmartMaterializedGridColumn column = item.getGridColumn(columnId);		
		if(column == null) {
			if(mandatory) {
				String massage = "The" + (forLeft ? " left " : " right ") + "grid (" + item.getGrid().getName() +") don't contains a column : '" + columnDescription + "' whith id : " + columnId;
				throw new BcephalException(massage);
			}
			return null;
		}
		return column.getDbColumnName();
	}
	
	@JsonIgnore
	private String getUniverseDbColumnName(boolean forLeft, Long columnId, String columnDescription, DimensionType type, Grille grid, boolean mandatory) {
		GrilleColumn column = grid.getColumnById(columnId);		
		if(column == null) {
			if(mandatory) {
				String massage = "The" + (forLeft ? " left " : " right ") + "grid don't contains a column : '" + columnDescription + "' whith id : " + columnId;
				throw new BcephalException(massage);
			}
			return null;
		}
		return column.getUniverseTableColumnName();
	}

	
}
