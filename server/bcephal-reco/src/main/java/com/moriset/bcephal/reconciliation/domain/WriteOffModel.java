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

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "WriteOffModel")
@Table(name = "BCP_WRITE_OFF_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class WriteOffModel extends Persistent {

	private static final long serialVersionUID = -1420955887277957700L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "writeoff_model_seq")
	@SequenceGenerator(name = "writeoff_model_seq", sequenceName = "writeoff_model_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING) 
	private ReconciliationModelSide writeOffMeasureSide;
	
	private Long writeOffMeasureId;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<WriteOffField> fields;

	@Transient 
	private ListChangeHandler<WriteOffField> fieldListChangeHandler;
	
	
	public WriteOffModel() {
		this.fieldListChangeHandler = new ListChangeHandler<WriteOffField>();
	}

	public void setFields(List<WriteOffField> fields) {
		this.fields = fields;
		fieldListChangeHandler.setOriginalList(fields);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		fields.size();
		this.fieldListChangeHandler.setOriginalList(fields);
	}

	@JsonIgnore
	public List<WriteOffField> getSortedFields() {
		List<WriteOffField> conditions = getFieldListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<WriteOffField>() {
			@Override
			public int compare(WriteOffField item1, WriteOffField item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}

	@JsonIgnore
	@Override
	public WriteOffModel copy() {
		WriteOffModel copy = new WriteOffModel();
		copy.setWriteOffMeasureSide(writeOffMeasureSide);		
		copy.setWriteOffMeasureId(writeOffMeasureId);
		for(WriteOffField field : getFieldListChangeHandler().getItems()) {
			copy.getFieldListChangeHandler().addNew(field.copy());
		}
		return copy;
	}
	
	
}
