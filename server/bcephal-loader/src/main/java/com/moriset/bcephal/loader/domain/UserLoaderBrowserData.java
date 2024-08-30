package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserLoaderBrowserData extends BrowserData {

	private UserLoaderTreatment treatment;
	
	public UserLoaderBrowserData(UserLoader userLoader) {
		super(userLoader);
		treatment = userLoader.getTreatment();
	}
	
}
