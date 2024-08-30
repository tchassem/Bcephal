package com.moriset.bcephal.integration.domain;

import com.moriset.bcephal.domain.BrowserData;

public class PontoConnectBrowserData extends BrowserData {

	public PontoConnectBrowserData(PontoConnectEntity entity) {
		super(entity.getId(), entity.getName(), entity.getCreationDate(), entity.getModificationDate());
		
	}
}
