package com.moriset.bcephal.multitenant.jpa;

import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {

	private static final long serialVersionUID = 1L;

	@Autowired
	private Map<String, DataSource> bcephalDataSources;
	
	@Override
	protected DataSource selectAnyDataSource() {
		if(this.bcephalDataSources.size() > 0) {
			return this.bcephalDataSources.values().iterator().next();
		}
		return null;
	}

	@Override
	protected DataSource selectDataSource(String tenantId) {
		return this.bcephalDataSources.get(tenantId);
	}

}
