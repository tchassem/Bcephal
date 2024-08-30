package com.moriset.bcephal.reconciliation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "AutoRecoRankingItem")
@Table(name = "BCP_AUTO_RECO_RANKING_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class AutoRecoRankingItem  extends Persistent {

	private static final long serialVersionUID = 6121582397659967890L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auto_reco_ranking_item_seq")
	@SequenceGenerator(name = "auto_reco_ranking_item_seq", sequenceName = "auto_reco_ranking_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "autoRecoId")
	private AutoReco autoRecoId;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	private Long dimensionId;
	
	private boolean descending;
	
	@Enumerated(EnumType.STRING)
	private ReconciliationModelSide side;
	
	private int position;
	
	
	@JsonIgnore
	public boolean isLeft() {
		return this.side != null && this.side.isLeft();
	}
	
	@JsonIgnore
	public boolean isRight() {
		return this.side != null && this.side.isRight();
	}
	
	@JsonIgnore
	public boolean isAttribute() {
		return dimensionType != null && dimensionType.isAttribute();
	}
	
	@JsonIgnore
	public boolean isMeasure() {
		return dimensionType != null && dimensionType.isMeasure();
	}
	
	@JsonIgnore
	public boolean isPeriod() {
		return dimensionType != null && dimensionType.isPeriod();
	}
	
	@JsonIgnore
	public String getUniverseTableColumnName() {
		String col = null;
		if(isAttribute()) {
			col = new Attribute(dimensionId).getUniverseTableColumnName();						
		}
		else if(isPeriod()) {
			col = new Period(dimensionId).getUniverseTableColumnName();	
		}
		else if(isMeasure()) {
			col = new Measure(dimensionId).getUniverseTableColumnName();				
		}		
		return col;
	}
	
	@JsonIgnore
	public String getSql() {
		String sql = "";
		String col = getUniverseTableColumnName();
		if(col != null) {
			sql = col + " " + (isDescending() ? "DESC" : "ASC");			
		}
		return sql;
	}
	

	@Override
	public Persistent copy() {
		AutoRecoRankingItem copy = new AutoRecoRankingItem();
		copy.setDimensionType(dimensionType);
		copy.setSide(side);
		copy.setDimensionId(dimensionId);
		copy.setDescending(descending);
		copy.setPosition(position);	
		return copy;
	}

}
