package com.moriset.bcephal.multitenant.jpa;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {

	private static ThreadLocal<String> currentTenant = new ThreadLocal<>();
	

	public static void setCurrentTenant(String tenantId) {
		currentTenant.set(tenantId);
		log.debug("Set Tenant {} from TenantContext",tenantId);
	}

	public static String getCurrentTenant() {
		
		return currentTenant.get();
	}

	public static void clear() {
		log.debug("Clear Tenant {} from TenantContext");
    	currentTenant.remove();
    }
}
