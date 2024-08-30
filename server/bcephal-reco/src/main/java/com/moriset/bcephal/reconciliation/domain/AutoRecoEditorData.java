/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Joseph Wambo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class AutoRecoEditorData extends EditorData<AutoReco> {

	private List<Nameable> reconciliationModels;
	
	private List<Nameable> reconciliationJoinModels;
	
	private List<Nameable> routines = new ArrayList<Nameable>();
	
}
