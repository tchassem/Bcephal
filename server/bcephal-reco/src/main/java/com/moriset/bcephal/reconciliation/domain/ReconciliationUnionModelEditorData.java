/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ReconciliationUnionModelEditorData extends EditorData<ReconciliationUnionModel> {

	private List<Nameable> sequences;
	
	private Long debitCreditAttributeId;

	private String debitValue;

	private String creditValue;
	
	public List<Nameable> matGrids;
	
	private List<AbstractSmartGrid<?>> grids;
	
	public ReconciliationUnionModelEditorData() {
		sequences = new ArrayList<>(0);
		matGrids = new ArrayList<>(0);
		grids = new ArrayList<>(0);
	}
	
}
