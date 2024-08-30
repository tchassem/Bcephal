package com.moriset.bcephal.reconciliation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

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

@Entity(name = "ReconciliationUnionModelButtonColumn")
@Table(name = "BCP_RECONCILIATION_UNION_MODEL_BUTTON_COLUMN")
@Data
@EqualsAndHashCode(callSuper = false)
public class ReconciliationUnionModelButtonColumn extends Persistent{
	
	private static final long serialVersionUID = 648091651112053558L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_union_model_button_col_seq")
	@SequenceGenerator(name = "reconciliation_union_model_button_col_seq", sequenceName = "reconciliation_union_model_button_col_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "model")
	private ReconciliationUnionModel model;
	
	private Long columnId;
		
	private String name;
	
	private Integer backgroundColor;

	private Integer foregroundColor;
	
	private Integer width;	

	@Enumerated(EnumType.STRING) 
	private ReconciliationModelSide side;

	private int position;

	@Override
	public ReconciliationUnionModelButtonColumn copy() {	
		ReconciliationUnionModelButtonColumn nColumn = new ReconciliationUnionModelButtonColumn();
		nColumn.setColumnId(getColumnId());
		nColumn.setBackgroundColor(backgroundColor);
		nColumn.setForegroundColor(getForegroundColor());
		nColumn.setName(getName());
		nColumn.setWidth(getWidth());
		nColumn.setSide(getSide());
		return nColumn;
	}

}
