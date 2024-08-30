/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

/**
 * @author Joseph Wambo
 *
 */
public class BillingModelEditorData extends EditorData<BillingModel> {

	public List<String> variables;
	
	public List<String> locales;
	
	public List<Nameable> sequences;
	
	public List<Nameable> companies;
	
	public BillingModelEditorData() {
		super();
	}
	
	public BillingModelEditorData(EditorData<BillingModel> data) {
		super(data);
	}
	
}
