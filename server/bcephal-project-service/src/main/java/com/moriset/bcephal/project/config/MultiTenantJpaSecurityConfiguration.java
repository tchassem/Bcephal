package com.moriset.bcephal.project.config;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.moriset.bcephal.multitenant.jpa.CurrentTenantIdentifierResolverImpl;
import com.moriset.bcephal.multitenant.jpa.DataBaseUtils;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.SimpleArchive;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.security.repository" }, entityManagerFactoryRef = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER_FACTORY, transactionManagerRef = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
@Slf4j
public class MultiTenantJpaSecurityConfiguration
		extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	public static final String SECURITY_TRANSACTION_MANAGER = "secondaryTransactionManager";
	protected static final String SECURITY_TRANSACTION_MANAGER_FACTORY = "lcontainerEntityManagerFactoryBean";
	protected static final String SECURITY_MANAGER_FACTORY = "sEntityManagerFactory";
	
	

	@Autowired
	private DataSourceProperties properties;
	
//	@Autowired
//	private Map<String, DataSource> bcephalDataSources;

	@Override
	public String[] getPackagesToScan() {
		return new String[] {};
	}

//	@Override
//	protected DataSource buildDefaultDataSource() {
//		return null;
//	}

	public String[] getPackagesToScanForSecurity() {
		return new String[] { Project.class.getPackage().getName(), SimpleArchive.class.getPackage().getName() };
	}

	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		log.debug("Try to build data source for tenant : {}", CurrentTenantIdentifierResolverImpl.SECURITY_TENANT);
		try {
			DataSource Defalutsource = datasources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
			if (Defalutsource == null) {
				throw new BcephalException("The default connection is null!");
			}
			DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, getHikari());
			String dbname = CurrentTenantIdentifierResolverImpl.SECURITY_TENANT.toLowerCase();
			utils.createsDbIfNotExists(dbname);
			DataSource source = utils.buildDataSource(dbname);
			utils.updateDbSchema(source, "classpath:sql_security");
			log.debug("Data base {} : data source successfully builded!",
					CurrentTenantIdentifierResolverImpl.SECURITY_TENANT);
			return source;
		} catch (SQLException e) {
			log.error("Unable to build data source for tenant: {}", CurrentTenantIdentifierResolverImpl.SECURITY_TENANT,
					e);
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			log.error("Unable to build data source for tenant: {}", CurrentTenantIdentifierResolverImpl.SECURITY_TENANT,
					e);
			throw e;
		}
	}

    @PersistenceContext(unitName = "secondary")
    @Bean(MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER_FACTORY)
    LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactoryBean() throws Exception {
		Map<String, Object> hibernateProps = new LinkedHashMap<>();
		hibernateProps.putAll(this.getJpaProperties().getProperties());
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan(getPackagesToScanForSecurity());
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaPropertyMap(hibernateProps);

		if(datasources.isEmpty()) {
			buildDataSources();
		}
		// set data source
		//em.setDataSource(buildSecurityDataSource());
		em.setDataSource(datasources.get(CurrentTenantIdentifierResolverImpl.SECURITY_TENANT));

		return em;
	}

    @Bean(SECURITY_MANAGER_FACTORY)
    EntityManagerFactory secondaryEntityManagerFactory(
             @Qualifier(SECURITY_TRANSACTION_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		return entityManagerFactoryBean.getObject();
	}

    @Bean(SECURITY_TRANSACTION_MANAGER)
    PlatformTransactionManager secondaryTxManager(
             @Qualifier(SECURITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpa = new JpaTransactionManager();
		jpa.setEntityManagerFactory(entityManagerFactory);
		return jpa;
	}
}
