package com.moriset.bcephal.multitenant.jpa;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Data
//@ConfigurationProperties(prefix = "spring.datasource")
public class MultiTenantInterceptor implements HandlerInterceptor/*,Filter*/ {

	public static final String TENANT_HEADER_NAME = "Bcephal-Project";
	public static final String CUSTOM_HEADER_NAME = "CustomHeaders";
	public static final String CUSTOM_SESSION_NAME = "CustomSessionHeaders";
	public static final String httpHeaderName = "http-header";
	public static final String  BC_CLIENT = "BC-CLIENT";
	public static final String  BC_PROFILE = "BC-PROFILE";
	public static final String  LANGUAGE = "Accept-Language";
	
	public Map<String, DataSource> bcephalDataSources;
	DataSourceProperties properties;
	private HikariProperties hikari;
	private String baseDbUrl;
	
	Environment environment;
	
	
	public MultiTenantInterceptor(Map<String, DataSource> bcephalDataSources, DataSourceProperties properties, HikariProperties hikari, Environment environment) {
		this.bcephalDataSources = bcephalDataSources;
		this.properties = properties;
		this.hikari = hikari;
		this.environment = environment;
	    this.baseDbUrl = this.properties.getUrl();
		if(this.baseDbUrl == null|| this.baseDbUrl.isBlank()) {
			this.baseDbUrl = "jdbc:postgresql://localhost:5433/";
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String tenantId = request.getHeader(TENANT_HEADER_NAME);
		if(StringUtils.hasLength(tenantId)) {
			buildDataSource(tenantId);
		}else {
			TenantContext.setCurrentTenant(tenantId);			
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model) throws Exception {
		boolean found = false;
		for (String profile : environment.getActiveProfiles()) {
			if (profile.equalsIgnoreCase("test")) {
				found = true;
				break;
			}
		}
		if (!found) {
			TenantContext.clear();
		}
	}
	
	private void buildDataSource(String tenantId) throws Exception {	
		log.debug("Try to build data source for tenant : {}", tenantId);		
		try {	
			if(!this.bcephalDataSources.containsKey(tenantId)) {
				DataSource Defalutsource = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
				DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
				String dbname = tenantId;
				if(utils.isDbExists(dbname)) {
				DataSource source = utils.buildDataSource(dbname);
				utils.updateDbSchema(source, "classpath:sql");
				this.bcephalDataSources.put(tenantId, source);
				}else {
					log.error("Error Data base {} of Project Not Found!", tenantId);
					throw new Exception("Error Data base of Project Not Found!");
				}
			}
			TenantContext.setCurrentTenant(tenantId);
	        log.debug("Data base {} : data source successfully builded!", tenantId);
		}
		catch (Exception e) {
			log.error("Unable to build data source for tenant: {}", tenantId, e);
			throw e;
		}
	}
	
	public void setTenant(ServerHttpRequest request) throws Exception{
		String tenantId = request.getHeaders().getFirst(TENANT_HEADER_NAME);
		if(tenantId == null && request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest req = (ServletServerHttpRequest)request;
			tenantId = req.getServletRequest().getParameter(TENANT_HEADER_NAME);
		}
		if(StringUtils.hasLength(tenantId)) {
			buildDataSource(tenantId);
		}else {
			TenantContext.setCurrentTenant(tenantId);			
		}
	}
	
	public void setTenantForServiceTest(String tenantId) throws Exception {
		buildDataSource(tenantId);
	}

//	@Override
//	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
//			throws IOException, ServletException {
//		 HttpServletRequest request = (HttpServletRequest) servletRequest;
//		    String tenantId = request.getHeader(TENANT_HEADER_NAME);
//		    try {
//		      this.tenantStore.setTenantId(tenantId);
//		      chain.doFilter(servletRequest, servletResponse);
//		    } finally {
//		      this.tenantStore.clear();
//		    }
//	}
}
