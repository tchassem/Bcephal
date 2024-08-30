package com.moriset.bcephal.multitenant.jpa;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;


public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String> {

	public static String DEFAULT_TENANT = "DEFAULT";
	public static String SECURITY_TENANT = "BCEPHAL_SECURITY";
	
	@Override
	public String resolveCurrentTenantIdentifier() {		
		String currentTenant = TenantContext.getCurrentTenant();
		return currentTenant != null ? currentTenant : DEFAULT_TENANT;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
