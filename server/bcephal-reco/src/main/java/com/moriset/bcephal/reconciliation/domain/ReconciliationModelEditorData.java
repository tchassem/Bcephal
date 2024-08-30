/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ReconciliationModelEditorData extends EditorData<ReconciliationModel> {

	private List<Nameable> sequences;
	
	private Long debitCreditAttributeId;

	private String debitValue;

	private String creditValue;
	
	public List<Nameable> matGrids;
	
	public ReconciliationModelEditorData() {
		sequences = new ArrayList<>(0);
		matGrids = new ArrayList<>(0);
	}
	
}
