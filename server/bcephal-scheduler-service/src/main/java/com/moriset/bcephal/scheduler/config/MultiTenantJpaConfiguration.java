package com.moriset.bcephal.scheduler.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.multitenant.jpa.CurrentTenantIdentifierResolverImpl;
import com.moriset.bcephal.multitenant.jpa.DataBaseUtils;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.script.Script;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.scheduler.domain.Project;
import com.moriset.bcephal.scheduler.domain.SchedulerLog;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLogItem;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.scheduler.repository",
		"com.moriset.bcephal.loader.repository",
		"com.moriset.bcephal.alarm.repository",
		"com.moriset.bcephal.reconciliation.repository",
		"com.moriset.bcephal.grid.repository",
		"com.moriset.bcephal.task.repository",
		"com.moriset.bcephal.join.repository",
		"com.moriset.bcephal.billing.repository",
		"com.moriset.bcephal.planification.repository",
		"com.moriset.bcephal.dashboard.repository",
		"com.moriset.bcephal.repository",}, 
transactionManagerRef = "transactionManager")
@Slf4j
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	
	@Override
	public String[] getPackagesToScan() {
		return new String[] { Persistent.class.getPackage().getName(), IPersistent.class.getPackage().getName(),
				Alarm.class.getPackage().getName(), FileLoader.class.getPackage().getName(),
				AutoReco.class.getPackage().getName(), SchedulerLog.class.getPackage().getName(),
				Dimension.class.getPackage().getName(), GrilleColumn.class.getPackage().getName(),
				Task.class.getPackage().getName(), DashboardReport.class.getPackage().getName(),
				Join.class.getPackage().getName(), BillingModel.class.getPackage().getName(),
				TransformationRoutine.class.getPackage().getName(),
				Script.class.getPackage().getName(),
				SchedulerPlannerLogItem.class.getPackage().getName()};
	}
	
	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		log.debug("Try to build data source for tenant : {}", CurrentTenantIdentifierResolverImpl.SECURITY_TENANT);
		try {			
			DataSource Defalutsource = buildDefaultDataSource();
			if(Defalutsource == null) {
				throw new BcephalException("The default connection is null!");
			}
			DataBaseUtils utils = new DataBaseUtils(getProperties(), Defalutsource, getHikari());
			String dbname = CurrentTenantIdentifierResolverImpl.SECURITY_TENANT.toLowerCase();
			utils.createsDbIfNotExists(dbname);
			DataSource source = utils.buildDataSource(dbname);
			utils.updateDbSchema(source, "classpath:sql_security");
			log.debug("Data base {} : data source successfully builded!", CurrentTenantIdentifierResolverImpl.SECURITY_TENANT);
			return source;
		}
		catch (SQLException e) {
			log.error("Unable to build data source for tenant: {}", CurrentTenantIdentifierResolverImpl.SECURITY_TENANT, e);
			throw new Exception(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unable to build data source for tenant: {}", CurrentTenantIdentifierResolverImpl.SECURITY_TENANT, e);
			throw e;
		}
	}

	@Bean
	List<Project> projects(){
		return projects;
	}
	
		
	
	protected List<Project> projects;
	
	@Override
	public Map<String, DataSource> bcephalDataSources() throws Exception {
		projects = new ArrayList<>(0);
		Map<String, DataSource> dataSources = super.bcephalDataSources();
		DataSource Defalutsource = dataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
		if(Defalutsource == null) {
			throw new BcephalException("The default connection is null!");
		}
		DataBaseUtils utils = new DataBaseUtils(getProperties(), Defalutsource, getHikari());
		DataSource securityDataSources = dataSources.get(CurrentTenantIdentifierResolverImpl.SECURITY_TENANT);
		Connection conn = securityDataSources.getConnection();
		if (conn != null && !conn.isClosed()) {
			String sql = "SELECT code,name,subscriptionId FROM BCP_SEC_PROJECT";
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				try {
					do {
						String projectCode = result.getString("code");
						String projectName = result.getString("name");
						Long clientId = result.getLong("subscriptionId");
						DataSource source = utils.buildDataSource(projectCode);
						dataSources.put(projectCode, source);
						projects.add(new Project(projectCode, projectName, clientId));
					} while (result.next());
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		return dataSources;
	}
	
	
}
