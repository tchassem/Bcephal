package com.moriset.bcephal.multitenant.jpa;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

//import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Configuration
@EnableConfigurationProperties({ JpaProperties.class })
@EnableTransactionManagement(proxyTargetClass = true)
public abstract class MultiTenantJpaConfiguration {

	@Autowired
	protected DataSourceProperties properties;

	@Autowired
	protected JpaProperties jpaProperties;
		
	@Autowired
	protected HikariProperties hikari;
	
	protected static Map<String, DataSource> datasources = new HashMap<>();
	

	@Autowired	
	public void securityDbName(@Value("${bcephal.security.db.name:BCEPHAL_SECURITY}") String securityDbName) {
		CurrentTenantIdentifierResolverImpl.SECURITY_TENANT = securityDbName;
	}

	
	@Bean
	protected MultiTenantConnectionProvider<String> multiTenantConnectionProvider() {
		return new DataSourceBasedMultiTenantConnectionProviderImpl();
	}
	
	@Bean
	protected CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver() {
		return new CurrentTenantIdentifierResolverImpl();
	}

	@Bean(name = "bcephalDataSources" )
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	protected Map<String, DataSource> bcephalDataSources() throws Exception {
		if(datasources.isEmpty()) {
			buildDataSources();
		}
		
		return datasources;
	}
	
	protected void buildDataSources() throws Exception {
		if(datasources.isEmpty()) {
			DataSource defaultDataSource = buildDefaultDataSource();
			if(defaultDataSource != null) {
				datasources.put(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT, defaultDataSource);
			}
			defaultDataSource = buildSecurityDataSource();
			if(defaultDataSource != null) {
				datasources.put(CurrentTenantIdentifierResolverImpl.SECURITY_TENANT, defaultDataSource);
			}
		}
	}
	
	protected DataSource buildDefaultDataSource() {		
		try {
			DataBaseUtils utils = new DataBaseUtils(properties, null, getHikari());
			String dbname = "postgres";
			DataSource source = utils.buildDataSource(dbname);
			return source;
		} 
		catch (Exception e) {
			log.error("Unable to build defaul data source", e);
		}
		return null;
	}
	
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

	@PersistenceContext
	@Primary
	@Bean
	protected LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
			MultiTenantConnectionProvider<String> multiTenantConnectionProvider,
			CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver) {

		Map<String, Object> hibernateProps = new LinkedHashMap<>();
		hibernateProps.putAll(this.jpaProperties.getProperties());
		hibernateProps.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		hibernateProps.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan(getPackagesToScan());
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaPropertyMap(hibernateProps);

		return em;
	}
	
	
		
	public abstract String[] getPackagesToScan();
	
	

	@Bean
	protected EntityManagerFactory entityManagerFactory(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		return entityManagerFactoryBean.getObject();
	}
	
	
	@Bean(name = "transactionManager")
	@Primary
	protected PlatformTransactionManager txManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpa = new JpaTransactionManager();
		jpa.setEntityManagerFactory(entityManagerFactory);
		return jpa;
	}
	
	
	
	
}
