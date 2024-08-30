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

@Entity(name = "WriteOffJoinModel")
@Table(name = "BCP_WRITE_OFF_JOIN_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class WriteOffJoinModel extends Persistent {

	private static final long serialVersionUID = -1420955887277957700L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "writeoff_join_model_seq")
	@SequenceGenerator(name = "writeoff_join_model_seq", sequenceName = "writeoff_join_model_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING) 
	private ReconciliationModelSide writeOffSide;
	
	private Long writeOffMeasureId;
	
	private boolean useGridMeasure;
	
	private Long writeOffTypeColumnId;
	
	private String writeOffTypeValue;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<WriteOffJoinField> fields;

	@Transient 
	private ListChangeHandler<WriteOffJoinField> fieldListChangeHandler;
	
	
	public WriteOffJoinModel() {
		this.fieldListChangeHandler = new ListChangeHandler<WriteOffJoinField>();
	}

	public void setFields(List<WriteOffJoinField> fields) {
		this.fields = fields;
		fieldListChangeHandler.setOriginalList(fields);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		fields.size();
		this.fieldListChangeHandler.setOriginalList(fields);
	}

	@JsonIgnore
	public List<WriteOffJoinField> getSortedFields() {
		List<WriteOffJoinField> conditions = getFieldListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<WriteOffJoinField>() {
			@Override
			public int compare(WriteOffJoinField item1, WriteOffJoinField item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}

	@JsonIgnore
	@Override
	public WriteOffJoinModel copy() {
		WriteOffJoinModel copy = new WriteOffJoinModel();
		copy.setWriteOffSide(writeOffSide);	
		copy.setUseGridMeasure(useGridMeasure);
		copy.setWriteOffMeasureId(writeOffMeasureId);
		for(WriteOffJoinField field : getFieldListChangeHandler().getItems()) {
			copy.getFieldListChangeHandler().addNew(field.copy());
		}
		return copy;
	}
	
	
}
