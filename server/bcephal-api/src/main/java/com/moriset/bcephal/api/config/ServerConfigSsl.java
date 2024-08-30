package com.moriset.bcephal.api.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
public class ServerConfigSsl {

	@Value("${http.port}")
	int port;

	@Value("${server.port}")
	int https_port;

//	@Bean
//	@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
//    public ServletWebServerFactory undertowFactory() {
//        UndertowServletWebServerFactory undertowFactory = new UndertowServletWebServerFactory();
//        undertowFactory.addBuilderCustomizers(builder -> {
//            builder.addHttpListener(port, "0.0.0.0");
//            // Open HTTP2
//            builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
//        });
//        undertowFactory.addDeploymentInfoCustomizers(deploymentInfo -> {
//            // HTTP Redirect to HTTPS 
//            deploymentInfo.addSecurityConstraint(new SecurityConstraint()
//                    .addWebResourceCollection(new WebResourceCollection().addUrlPattern("/*"))
//                    .setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
//                    .setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT))
//                    .setConfidentialPortManager(exchange -> https_port);
//        });
//        return undertowFactory;
//    }

	@Bean
	@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
	protected ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(getHttpConnector());
		return tomcat;
	}

	private Connector getHttpConnector() {
		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setScheme("http");
		connector.setPort(port);
		connector.setSecure(false);
		connector.setRedirectPort(https_port);
		return connector;
	}

//	  @Bean
//	  @ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
//	  @ConditionalOnClass(HttpConnectionFactory.class)
//	  public ConfigurableServletWebServerFactory webServerFactory() {
//	      UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
//	      factory.addServerCustomizers(new JettyServerCustomizer() {
//	          @Override
//	          public void customize(Server server) {
//	              final HttpConnectionFactory httpConnectionFactory = server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);
//
//	              final ServerConnector httpConnector = new ServerConnector(server, httpConnectionFactory);
//	              httpConnector.setPort(80 /* HTTP */);
//	              server.addConnector(httpConnector);
//
//	              final HandlerList handlerList = new HandlerList();
//	              handlerList.addHandler(new SecuredRedirectHandler());
//	              for(Handler handler : server.getHandlers())
//	                  handlerList.addHandler(handler);
//	              server.setHandler(handlerList);
//	          }
//	      });
//	      return factory;
//	  }
}
