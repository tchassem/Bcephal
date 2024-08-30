/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.SchedulableObject;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.routine.RoutineExecutor;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridColumn;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "AutoReco")
@Table(name = "BCP_AUTO_RECO")
@Data
@EqualsAndHashCode(callSuper = false)
public class AutoReco extends SchedulableObject {

	private static final long serialVersionUID = 249997687446171725L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auto_reco_seq")
	@SequenceGenerator(name = "auto_reco_seq", sequenceName = "auto_reco_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
		
	private Long recoId;
	
	@Transient @JsonIgnore
	private ReconciliationModel model;
	
	@Transient @JsonIgnore
	private ReconciliationUnionModel unionModel;
		
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "filter")
	private UniverseFilter leftFilter;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "rightFilter")
	private UniverseFilter rightFilter;
	
	@Enumerated(EnumType.STRING) 
	private AutoRecoMethod method;
		
	private boolean useCombinations;
		
	private int maxDurationPerLine;
	
	private boolean buildCombinationsAsc;
		
	@Enumerated(EnumType.STRING) 
	private AutoRecoCondition condition;
		
	private BigDecimal conditionMinValue;
	
	private BigDecimal conditionMaxValue;
	
	private String neutralizationValue;
	
	private int batchChunk;
	
	private boolean refreshAmounts;
	
	private Boolean forModel;
	
	private boolean confirmAction;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "autoRecoId")
	private List<ReconciliationCondition> conditions;

	@Transient
	private ListChangeHandler<ReconciliationCondition> conditionListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "autoRecoId")
	private List<AutoRecoRankingItem> rankingItems;

	@Transient
	private ListChangeHandler<AutoRecoRankingItem> rankingItemListChangeHandler;
	
	
	@Transient
	private ListChangeHandler<RoutineExecutor> routineListChangeHandler;
		
	
	public AutoReco() {
		this.batchChunk = 100;
		this.refreshAmounts = true;
		this.conditions = new ArrayList<ReconciliationCondition>();		
		this.conditionListChangeHandler = new ListChangeHandler<ReconciliationCondition>();	
		this.rankingItems = new ArrayList<AutoRecoRankingItem>();		
		this.rankingItemListChangeHandler = new ListChangeHandler<AutoRecoRankingItem>();	
		this.routineListChangeHandler = new ListChangeHandler<RoutineExecutor>();
	}

	public void setConditions(List<ReconciliationCondition> conditions) {
		this.conditions = conditions;
		conditionListChangeHandler.setOriginalList(conditions);
	}
	
	public void setRankingItems(List<AutoRecoRankingItem> rankingItems) {
		this.rankingItems = rankingItems;
		rankingItemListChangeHandler.setOriginalList(rankingItems);
	}
	
	public boolean getForModel() {
		if(forModel == null) {
			forModel = false;
		}
		return forModel;
	}
	
	public BigDecimal getConditionMinValue(){
		if(conditionMinValue == null) {
			conditionMinValue = BigDecimal.ZERO;
		}
		return conditionMinValue;
	}
	
	public BigDecimal getConditionMaxValue(){
		if(conditionMaxValue == null) {
			conditionMaxValue = BigDecimal.ZERO;
		}
		return conditionMaxValue;
	}
	
	@JsonIgnore
	public boolean isZeroAount() {
		return getMethod() == AutoRecoMethod.ZERO_AMOUNT;
	}
	
	@JsonIgnore
	public boolean isOneOnOne() {
		return getMethod() == null || getMethod() == AutoRecoMethod.ONE_ON_ONE;
	}
	
	@JsonIgnore
	public boolean isCumulatedLeft() {
		return getMethod() == AutoRecoMethod.CUMULATED_LEFT;
	}
	
	@JsonIgnore
	public boolean isCumulatedRight() {
		return getMethod() == AutoRecoMethod.CUMULATED_RIGHT;
	}
	
	@JsonIgnore
	public boolean isBothCumulated() {
		return getMethod() == AutoRecoMethod.BOTH_CUMULATED;
	}
	
	@JsonIgnore
	public boolean isLeftSide() {
		return getMethod() == AutoRecoMethod.LEFT_SIDE;
	}
	
	@JsonIgnore
	public boolean isRightSide() {
		return getMethod() == AutoRecoMethod.RIGHT_SIDE;
	}
	
	@JsonIgnore
	public boolean isNeutralization() {
		return getMethod() == AutoRecoMethod.NEUTRALIZATION;
	}
	
	
	@JsonIgnore
	public boolean isBalanceIsZero() {
		return getCondition() == null || getCondition() == AutoRecoCondition.BALANCE_IS_ZERO;
	}
	
	@JsonIgnore
	public boolean isBalanceIsAmountInterval() {
		return getCondition() == null || getCondition() == AutoRecoCondition.BALANCE_AMOUNT_ITERVAL;
	}
	
	@JsonIgnore
	public boolean isBalanceIsAmountPercentage() {
		return getCondition() == null || getCondition() == AutoRecoCondition.BALANCE_AMOUNT_PERCENTAGE;
	}
	
	@JsonIgnore
	public boolean isPartialReco() {
		return getCondition() != null && getCondition() == AutoRecoCondition.PARTIAL_RECO;
	}
	
	
	@JsonIgnore
	public Grille getPrimaryGrid() {
		if(this.isCumulatedLeft()) {
			return getModel().getRigthGrid();
		}
		else {
			return getModel().getLeftGrid();
		}
	}
	
	@JsonIgnore
	public ReconciliationUnionModelGrid getPrimaryUnionModelGrid() {
		if(this.isCumulatedLeft()) {
			return getUnionModel().getRigthGrid();
		}
		else {
			return getUnionModel().getLeftGrid();
		}
	}
	
	@JsonIgnore
	public boolean isLeftGridPrimary() {
		return getForModel() ? getPrimaryUnionModelGrid().getId().equals(getUnionModel().getLeftGrid().getId())
				: getPrimaryGrid().getId().equals(getModel().getLeftGrid().getId());
	}
	
	@JsonIgnore
	public Long getPrimaryCdAttributeId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getRigthGrid().getDebitCreditColumnId();
			}
			else {
				return getUnionModel().getLeftGrid().getDebitCreditColumnId();
			}
		}
		else {
			return null;
		}
	}
	
	@JsonIgnore
	public Long getPrimaryMeasureId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getRigthGrid().getMeasureColumnId();
			}
			else {
				return getUnionModel().getLeftGrid().getMeasureColumnId();
			}
		}
		else {
			if(this.isCumulatedLeft()) {
				return getModel().getRigthMeasureId();
			}
			else {
				return getModel().getLeftMeasureId();
			}
		}
	}
	
	@JsonIgnore
	public Measure getPrimaryMeasure() {
		Long id = getPrimaryMeasureId();
		if(getForModel()) {
//			ReconciliationUnionModelGrid grid = getPrimaryUnionModelGrid();
//			return new Measure(id, null, grid.getGrid().getMainGrid().getGridType().GetDataSource(), grid.getGrid().getMainGrid().getId());
			return null;
		}
		else {
			return new Measure(id);
		}
	}
	
	
	@JsonIgnore
	public Long getPrimaryRemainningMeasureId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getRigthGrid().getRemainningMeasureColumnId();
			}
			else {
				return getUnionModel().getLeftGrid().getRemainningMeasureColumnId();
			}
		}
		else {
			return getModel().getRemainningMeasureId();
		}
	}
	
	@JsonIgnore
	public Measure getPrimaryRemainningMeasure() {
		Long id = getPrimaryRemainningMeasureId();
		if(getForModel()) {
//			ReconciliationUnionModelGrid grid = getPrimaryUnionModelGrid();
//			return new Measure(id, null, grid.getGrid().getMainGrid().getGridType().GetDataSource(), grid.getGrid().getMainGrid().getId());
			return null;
		}
		else {
			return new Measure(id);
		}
	}
	
	
	@JsonIgnore
	public Long getPrimaryReconciliatedMeasureId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getRigthGrid().getReconciliatedMeasureColumnId();
			}
			else {
				return getUnionModel().getLeftGrid().getReconciliatedMeasureColumnId();
			}
		}
		else {
			return getModel().getReconciliatedMeasureId();
		}
	}
	
	@JsonIgnore
	public Measure getPrimaryReconciliatedMeasure() {
		Long id = getPrimaryReconciliatedMeasureId();
		if(getForModel()) {
//			ReconciliationUnionModelGrid grid = getPrimaryUnionModelGrid();
//			return new Measure(id, null, grid.getGrid().getMainGrid().getGridType().GetDataSource(), grid.getGrid().getMainGrid().getId());
			return null;
		}
		else {
			return new Measure(id);
		}
	}
	
	@JsonIgnore
	public ReconciliationModelSide getPrimarySide() {
		if(this.isLeftGridPrimary()) {
			return ReconciliationModelSide.LEFT;
		}
		else {
			return ReconciliationModelSide.RIGHT;
		}
	}
	
	@JsonIgnore
	public Grille getSecondaryGrid() {
		if(this.isCumulatedLeft()) {
			return getModel().getLeftGrid();
		}
		else {
			return getModel().getRigthGrid();
		}
	}
	
	@JsonIgnore
	public ReconciliationUnionModelGrid getSecondaryUnionModelGrid() {
		if(this.isCumulatedLeft()) {
			return getUnionModel().getLeftGrid();
		}
		else {
			return getUnionModel().getRigthGrid();
		}
	}
	
	@JsonIgnore
	public Long getSecondaryCdAttributeId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getLeftGrid().getDebitCreditColumnId();
			}
			else {
				return getUnionModel().getRigthGrid().getDebitCreditColumnId();
			}
		}
		else {
			return null;
		}
	}
	
	@JsonIgnore
	public Long getSecondaryMeasureId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getLeftGrid().getMeasureColumnId();
			}
			else {
				return getUnionModel().getRigthGrid().getMeasureColumnId();
			}
		}
		else {
			if(this.isCumulatedLeft()) {
				return getModel().getLeftMeasureId();
			}
			else {
				return getModel().getRigthMeasureId();
			}
		}
	}
	
	@JsonIgnore
	public Measure getSecondaryMeasure() {
		Long id = getSecondaryMeasureId();
		if(getForModel()) {
//			ReconciliationUnionModelGrid grid = getSecondaryUnionModelGrid();
//			return new Measure(id, null, grid.getGrid().getMainGrid().getGridType().GetDataSource(), grid.getGrid().getMainGrid().getId());
			return null;
		}
		else {
			return new Measure(id);
		}
	}
	
	@JsonIgnore
	public Long getSecondaryRemainningMeasureId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getLeftGrid().getRemainningMeasureColumnId();
			}
			else {
				return getUnionModel().getRigthGrid().getRemainningMeasureColumnId();
			}
		}
		else {
			return getModel().getRemainningMeasureId();
		}
	}
	
	@JsonIgnore
	public Measure getSecondaryRemainningMeasure() {
		Long id = getSecondaryRemainningMeasureId();
		if(getForModel()) {
//			ReconciliationUnionModelGrid grid = getSecondaryUnionModelGrid();
//			return new Measure(id, null, grid.getGrid().getMainGrid().getGridType().GetDataSource(), grid.getGrid().getMainGrid().getId());
			return null;
		}
		else {
			return new Measure(id);
		}
	}
	
	
	@JsonIgnore
	public Long getSecondaryReconciliatedMeasureId() {
		if(getForModel()) {
			if(this.isCumulatedLeft()) {
				return getUnionModel().getLeftGrid().getReconciliatedMeasureColumnId();
			}
			else {
				return getUnionModel().getRigthGrid().getReconciliatedMeasureColumnId();
			}
		}
		else {
			return getModel().getReconciliatedMeasureId();
		}
	}
	
	@JsonIgnore
	public Measure getSecondaryReconciliatedMeasure() {
		Long id = getSecondaryReconciliatedMeasureId();
		if(getForModel()) {
//			ReconciliationUnionModelGrid grid = getSecondaryUnionModelGrid();
//			return new Measure(id, null, grid.getGrid().getMainGrid().getGridType().GetDataSource(), grid.getGrid().getMainGrid().getId());
			return null;
		}
		else {
			return new Measure(id);
		}
	}
	
	
	@JsonIgnore
	public ReconciliationModelSide getSecondarySide() {
		if(this.isLeftGridPrimary()) {
			return ReconciliationModelSide.RIGHT;
		}
		else {
			return ReconciliationModelSide.LEFT;
		}
	}
		
	public List<GrilleColumn> getConditionColumns(ReconciliationModelSide side) {
		List<ReconciliationCondition> conditions = getSortedConditions();
		List<GrilleColumn> columns = new ArrayList<GrilleColumn>();
		Grille grid = side.isLeft() ? this.model.getLeftGrid() : this.model.getRigthGrid();
		for(ReconciliationCondition item : conditions) {
			GrilleColumn column = item.getSide1() == side ? grid.getColumnById(item.getColumnId1())
					: grid.getColumnById(item.getColumnId2());
			if(column != null) {
				columns.add(column);
			}
		}
		return columns;
	}
	
	public List<GrilleColumn> getConditionColumnsForUnion(ReconciliationModelSide side) {
		List<ReconciliationCondition> conditions = getSortedConditions();
		List<GrilleColumn> columns = new ArrayList<>();
		Grille grid = side.isLeft() ? this.unionModel.getLeftGrid().getGrille() : this.unionModel.getRigthGrid().getGrille();
		if(grid != null) {
			for(ReconciliationCondition item : conditions) {
				GrilleColumn column = item.getSide1() == side ? grid.getColumnById(item.getColumnId1())
						: grid.getColumnById(item.getColumnId2());
				if(column != null) {
					columns.add(column);
				}
			}
		}
		return columns;
	}
	
	public List<UnionGridColumn> getUnionConditionColumns(ReconciliationModelSide side) {
		List<ReconciliationCondition> conditions = getSortedConditions();
		List<UnionGridColumn> columns = new ArrayList<>();
		UnionGrid grid = side.isLeft() ? this.unionModel.getLeftGrid().getGrid() : this.unionModel.getRigthGrid().getGrid();
		if(grid != null) {
			for(ReconciliationCondition item : conditions) {
				UnionGridColumn column = item.getSide1() == side ? grid.getColumnById(item.getColumnId1())
						: grid.getColumnById(item.getColumnId2());
				if(column != null) {
					columns.add(column);
				}
			}
		}
		return columns;
	}
	
	@JsonIgnore
	public List<ReconciliationCondition> getSortedConditions() {
		List<ReconciliationCondition> conditions = getConditionListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<ReconciliationCondition>() {
			@Override
			public int compare(ReconciliationCondition item1, ReconciliationCondition item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}
	
	@JsonIgnore
	public List<AutoRecoRankingItem> getSortedRankingItems() {
		List<AutoRecoRankingItem> items = getRankingItemListChangeHandler().getItems();
		Collections.sort(items, new Comparator<AutoRecoRankingItem>() {
			@Override
			public int compare(AutoRecoRankingItem item1, AutoRecoRankingItem item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return items;
	}
	
	
	@PostLoad
	public void initListChangeHandler() {
		conditions.size();
		this.conditionListChangeHandler.setOriginalList(conditions);
		rankingItems.size();
		this.rankingItemListChangeHandler.setOriginalList(rankingItems);
	}
	
	@JsonIgnore
	@Override
	public AutoReco copy() {
		AutoReco copy = new AutoReco();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setRecoId(recoId);
		copy.setLeftFilter(leftFilter != null ? leftFilter.copy() : null);
		copy.setRightFilter(rightFilter != null ? rightFilter.copy() : null);
		copy.setMethod(method);
		copy.setUseCombinations(useCombinations);
		copy.setMaxDurationPerLine(maxDurationPerLine);
		copy.setBuildCombinationsAsc(buildCombinationsAsc);
		copy.setCondition(condition);
		copy.setConditionMinValue(conditionMinValue);
		copy.setConditionMaxValue(conditionMaxValue);
		copy.setNeutralizationValue(neutralizationValue);		
		for(ReconciliationCondition condition : getConditionListChangeHandler().getItems()) {
			copy.getConditionListChangeHandler().addNew(condition.copy());
		}
		return copy;
	}
	
}
