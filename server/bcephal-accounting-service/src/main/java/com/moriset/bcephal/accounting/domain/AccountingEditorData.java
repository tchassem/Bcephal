/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.grid.domain.Grille;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AccountingEditorData extends EditorData<Grille> {

	private Long measureId;
	private Long signId;
	private String creditValue;
	private String debitValue;
	
}
