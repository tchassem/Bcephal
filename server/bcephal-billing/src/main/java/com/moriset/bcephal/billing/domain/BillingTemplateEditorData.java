package com.moriset.bcephal.billing.domain;

import java.util.List;

import com.moriset.bcephal.domain.EditorData;

public class BillingTemplateEditorData extends EditorData<BillTemplate> {

	public List<String> variables;
	
	public List<String> locales;
	
	public BillingTemplateEditorData() {
		super();
	}
	
	public BillingTemplateEditorData(EditorData<BillTemplate> data) {
		super(data);
	}

}
