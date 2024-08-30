package com.moriset.bcephal.reconciliation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridCondition;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "ReconciliationUnionModelGrid")
@Table(name = "BCP_RECONCILIATION_UNION_MODEL_GRID")
@Data
@EqualsAndHashCode(callSuper = false)
public class ReconciliationUnionModelGrid extends Persistent {

	private static final long serialVersionUID = 5120025772404154299L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_union_model_grid_seq")
	@SequenceGenerator(name = "reconciliation_union_model_grid_seq", sequenceName = "reconciliation_union_model_grid_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "grid")
	private UnionGrid grid;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "grille")
	private Grille grille;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType type;
	
	private Long measureColumnId;
	private Long recoTypeColumnId;
	
	private Long partialRecoTypeColumnId;
	private Long reconciliatedMeasureColumnId;
	private Long remainningMeasureColumnId;	

	private Long freezeTypeColumnId;	
	private Long neutralizationTypeColumnId;
	private Long noteTypeColumnId;
	
	private Long recoDateColumnId;
	
	private Long debitCreditColumnId;
	
	private Long userColumnId;
	
	private Long modeColumnId;
	
	
	@JsonIgnore @Transient
	private UnionGridColumn measureJoinColumn;
	@JsonIgnore @Transient
	private UnionGridColumn recoTypeJoinColumn;
	@JsonIgnore @Transient	
	private UnionGridColumn partialRecoTypeJoinColumn;
	@JsonIgnore @Transient
	private UnionGridColumn reconciliatedMeasureJoinColumn;
	@JsonIgnore @Transient
	private UnionGridColumn remainningMeasureJoinColumn;
	@JsonIgnore @Transient
	private UnionGridColumn freezeTypeJoinColumn;
	@JsonIgnore @Transient
	private UnionGridColumn neutralizationTypeJoinColumn;
	@JsonIgnore @Transient
	private UnionGridColumn noteTypeJoinColumn;
	
	
	public void initializeJoinColumns() {
		measureJoinColumn = getUnionGridColumn(measureColumnId, DimensionType.MEASURE);
		recoTypeJoinColumn = getUnionGridColumn(recoTypeColumnId, DimensionType.ATTRIBUTE);
		partialRecoTypeJoinColumn = getUnionGridColumn(partialRecoTypeColumnId, DimensionType.ATTRIBUTE);
		reconciliatedMeasureJoinColumn = getUnionGridColumn(reconciliatedMeasureColumnId, DimensionType.MEASURE);
		remainningMeasureJoinColumn = getUnionGridColumn(remainningMeasureColumnId, DimensionType.MEASURE);
		freezeTypeJoinColumn = getUnionGridColumn(freezeTypeColumnId, DimensionType.ATTRIBUTE);
		neutralizationTypeJoinColumn = getUnionGridColumn(neutralizationTypeColumnId, DimensionType.ATTRIBUTE);
		noteTypeJoinColumn = getUnionGridColumn(noteTypeColumnId, DimensionType.ATTRIBUTE);
	}
	
	@JsonIgnore
	public boolean isUniverse() {
		return type == JoinGridType.GRID;
	}
	
	@JsonIgnore
	public boolean isUnion() {
		return type == JoinGridType.UNION_GRID;
	}
	
	@JsonIgnore
	public UnionGridColumn getUnionGridColumn(Long mainGridColumnId, DimensionType type) {
		if(grid == null || mainGridColumnId == null) {
			return null;
		}
		return grid.getColumnById(mainGridColumnId);
	}
	
	@JsonIgnore
	public Long getGridId() {
		return isUnion() && getGrid() != null ? getGrid().getId() : isUniverse() && getGrille() != null ? getGrille().getId() : null;
	}
	
	public void sort() {
		if(getGrid() != null) getGrid().sort();
	}

	@Override
	public ReconciliationUnionModelGrid copy() {
		ReconciliationUnionModelGrid copy = new ReconciliationUnionModelGrid();
		copy.setGrid(grid != null ? grid.copy() : null);
		copy.setGrille(grille != null ? grille.copy() : null);
		copy.setType(getType());
		return copy;
	}
	
	public void RefreshColumnIdAfterCopy(ReconciliationUnionModelGrid original) {
		setMeasureColumnId(getColumnId(original, original.getMeasureColumnId()));
		setRecoTypeColumnId(getColumnId(original, original.getRecoTypeColumnId()));
		setPartialRecoTypeColumnId(getColumnId(original, original.getPartialRecoTypeColumnId()));
		setReconciliatedMeasureColumnId(getColumnId(original, original.getReconciliatedMeasureColumnId()));
		setRemainningMeasureColumnId(getColumnId(original, original.getRemainningMeasureColumnId()));
		setFreezeTypeColumnId(getColumnId(original, original.getFreezeTypeColumnId()));
		setNeutralizationTypeColumnId(getColumnId(original, original.getNeutralizationTypeColumnId()));
		setNoteTypeColumnId(getColumnId(original, original.getNoteTypeColumnId()));
		setRecoDateColumnId(getColumnId(original, original.getRecoDateColumnId()));
		setDebitCreditColumnId(getColumnId(original, original.getDebitCreditColumnId()));
		setUserColumnId(getColumnId(original, original.getUserColumnId()));
		setModeColumnId(getColumnId(original, original.getModeColumnId()));
		RefreshConditionColumnIdAfterCopy(original);
	}
	

	public void RefreshConditionColumnIdAfterCopy(ReconciliationUnionModelGrid original) {
		
		if( original.getGrid() != null) {
			for(UnionGridColumn column : grid.getColumnListChangeHandler().getItems()) {
				if(column != null && column.getColumnIds() != null) {
				//column.setColumns(null);
				}
			}
			for(UnionGridCondition condition : grid.getConditionListChangeHandler().getItems()) {
				if(condition.getItem1() != null && condition.getItem1().getColumnId() != null) {
					condition.getItem1().setColumnId(getColumnId(original,condition.getItem1().getColumnId()));
				}
				if(condition.getItem2() != null && condition.getItem2().getColumnId() != null) {
					condition.getItem2().setColumnId(getColumnId(original,condition.getItem2().getColumnId()));
				}
			}
		}else
			if(original.getGrille() != null) {
				
			}
		
	}
	
	private Long getColumnId(ReconciliationUnionModelGrid original, Long columnId) {
		if(original.getGrid() != null && columnId != null) {
			UnionGridColumn column_ = original.getGrid().getColumnById(columnId);
			if(column_ != null) {
				UnionGridColumn column_in = getGrid().getColumnByDimensionAndName(column_.getType(), column_.getName());
				if(column_in != null) {
					return column_in.getId();
				}
			}
		}
		if(original.getGrille() != null && columnId != null) {
			GrilleColumn column_ = original.getGrille().getColumnById(columnId);
			if(column_ != null) {
				GrilleColumn column_in = getGrille().getColumnByDimensionAndName(column_.getType(), column_.getName());
				if(column_in != null) {
					return column_in.getId();
				}
			}
		}
		return null;
	}

	
}
