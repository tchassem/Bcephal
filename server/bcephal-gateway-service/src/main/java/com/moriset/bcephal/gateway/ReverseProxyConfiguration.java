//package com.moriset.bcephal.gateway;
//
//import org.apache.catalina.connector.Connector;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.stereotype.Component;
//
//@ConditionalOnProperty(prefix = "bcephal.enable", name = "proxy",  havingValue="false")
//@ConfigurationProperties(prefix = "server.tomcat" )
//@Component
//public class ReverseProxyConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>, TomcatConnectorCustomizer {
//
//    private String proxyName;
//    
//    private int proxyPort;
//
//    @Override
//    public void customize(final TomcatServletWebServerFactory factory) {
//    	if(proxyName != null && !proxyName.isBlank()) {
//    		factory.addConnectorCustomizers(this);
//    	}
//    }
//
//    @Override
//    public void customize(final Connector connector) {
//    	if(proxyName != null && !proxyName.isBlank()) {
//    		connector.setProxyName(this.proxyName);
//            connector.setProxyPort(this.proxyPort);
//    	}
//    }
//
//	public String getProxyName() {
//		return proxyName;
//	}
//
//	public void setProxyName(String proxyName) {
//		this.proxyName = proxyName;
//	}
//
//	public int getProxyPort() {
//		return proxyPort;
//	}
//
//	public void setProxyPort(String proxyPort) {
//		if(proxyPort != null && !proxyPort.isBlank()) {
//			this.proxyPort = Integer.valueOf(proxyPort.trim());
//		}
//	}
//    
//    
//}
