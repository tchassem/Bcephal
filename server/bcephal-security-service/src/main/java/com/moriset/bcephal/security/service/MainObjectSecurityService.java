/**
 * 
 */
package com.moriset.bcephal.security.service;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.security.domain.MainObject;
import com.moriset.bcephal.security.domain.auth2.Auth2AdminUser;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author MORISET-004
 *
 */
@Service
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class MainObjectSecurityService<P extends MainObject, B> extends MainObjectService<P, B> {

	@Value("${keycloak.auth-server-url}")
	protected String authServerUrl;
	
	@Value("${oauth2.admin-url}")
	protected String authAdminServerUrl;
	
	@Value("${oauth2.use-admin-url:false}")
	protected boolean useAdminUrl;

	@Value("${keycloak.realm}")
	protected String realm;

	@Value("${gateway-host:localhost}")
	protected String origin;
	
	@Autowired
	Auth2AdminUser auth2AdminUser;
	
	public Keycloak getKeycloak() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		cm.setMaxTotal(20); // Increase max total connection to 200
		cm.setDefaultMaxPerRoute(20); // Increase default max connection per route to 20
		ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient);
		String url = useAdminUrl ? authAdminServerUrl : authServerUrl;
		ResteasyClient client = ((ResteasyClientBuilder) ResteasyClientBuilder.newBuilder()).httpEngine(engine).build();
		Keycloak keycloak = KeycloakBuilder.builder()
				.serverUrl(url)
				.grantType(OAuth2Constants.PASSWORD)
				.realm("master")
				.clientId("admin-cli")
				.username(auth2AdminUser.getUserName())
				.password(auth2AdminUser.getUserPassword())
				.resteasyClient(client)
				.build();
		keycloak.tokenManager().getAccessToken();
		return keycloak;
	}
}
