/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "WriteOffUnionModel")
@Table(name = "BCP_WRITE_OFF_UNION_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class WriteOffUnionModel extends Persistent {

	private static final long serialVersionUID = -1420955887277957700L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "writeoff_union_model_seq")
	@SequenceGenerator(name = "writeoff_union_model_seq", sequenceName = "writeoff_union_model_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private Long writeOffGridId;
	
	@Enumerated(EnumType.STRING) 
	private ReconciliationModelSide writeOffSide;

	private Long writeOffMeasureId;

	private boolean useGridMeasure;

	private Long writeOffTypeColumnId;

	private String writeOffTypeValue;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<WriteOffUnionField> fields;

	@Transient
	private ListChangeHandler<WriteOffUnionField> fieldListChangeHandler;

	public WriteOffUnionModel() {
		this.fieldListChangeHandler = new ListChangeHandler<>();
	}

	public void setFields(List<WriteOffUnionField> fields) {
		this.fields = fields;
		fieldListChangeHandler.setOriginalList(fields);
	}

	@PostLoad
	public void initListChangeHandler() {
		fields.size();
		this.fieldListChangeHandler.setOriginalList(fields);
	}

	@JsonIgnore
	public List<WriteOffUnionField> getSortedFields() {
		List<WriteOffUnionField> conditions = getFieldListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<WriteOffUnionField>() {
			@Override
			public int compare(WriteOffUnionField item1, WriteOffUnionField item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}

	@JsonIgnore
	@Override
	public WriteOffUnionModel copy() {
		WriteOffUnionModel copy = new WriteOffUnionModel();
		copy.setWriteOffSide(writeOffSide);
		copy.setUseGridMeasure(useGridMeasure);
		copy.setWriteOffTypeValue(getWriteOffTypeValue());
		for (WriteOffUnionField field : getFieldListChangeHandler().getItems()) {
			copy.getFieldListChangeHandler().addNew(field.copy());
		}
		return copy;
	}
	
	public void RefreshColumnIdAfterCopy(WriteOffUnionModel original, ReconciliationUnionModelGrid src,ReconciliationUnionModelGrid dest) {
		setWriteOffMeasureId(getColumnId(src,dest, original.getWriteOffMeasureId()));
		setWriteOffTypeColumnId(getColumnId(src,dest, original.getWriteOffTypeColumnId()));
		setWriteOffGridId(dest.getGridId());
	}
	
	private Long getColumnId(ReconciliationUnionModelGrid original,ReconciliationUnionModelGrid dest, Long columnId) {
		if(original.getGrid() != null && columnId != null) {
			UnionGridColumn column_ = original.getGrid().getColumnById(columnId);
			if(column_ != null) {
				UnionGridColumn column_in = dest.getGrid().getColumnByDimensionAndName(column_.getType(), column_.getName());
				if(column_in != null) {
					return column_in.getId();
				}
			}
		}
		if(original.getGrille() != null && columnId != null) {
			GrilleColumn column_ = original.getGrille().getColumnById(columnId);
			if(column_ != null) {
				GrilleColumn column_in = dest.getGrille().getColumnByDimensionAndName(column_.getType(), column_.getName());
				if(column_in != null) {
					return column_in.getId();
				}
			}
		}
		return null;
	}

}
