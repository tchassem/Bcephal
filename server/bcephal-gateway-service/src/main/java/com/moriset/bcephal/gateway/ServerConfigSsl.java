package com.moriset.bcephal.gateway;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

//import org.apache.catalina.Context;
//import org.apache.catalina.connector.Connector;
//import org.apache.tomcat.util.descriptor.web.SecurityCollection;
//import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
public class ServerConfigSsl{
	
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

    
//	@Bean
//	@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(getHttpConnector());
//        return tomcat;
//    }
//	  
//	  private Connector getHttpConnector() {		  
//	    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//	    connector.setScheme("http");
//	    connector.setPort(port);
//	    connector.setSecure(false);
//	    connector.setRedirectPort(https_port);
//	    return connector;
//	  }
    
//	
//	@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
//	@Component
//	public class NettyWebServerFactorySslCustomizer  implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
//
//		@Value("${server.ssl.key-store}")
//		String keyStore;
//		@Value("${server.ssl.key-store-password}")
//		String keyStorePassword;
//		@Value("${server.ssl.key-password:}")
//		String keyPassword;
//		@Value("${server.ssl.keyStoreType:}")
//		String keyStoreType;
//		@Value("${server.ssl.key-alias}")
//		String alias;
//		
//	    @Override
//	    public void customize(NettyReactiveWebServerFactory serverFactory) {
//	        Ssl ssl = new Ssl();
//	        ssl.setEnabled(true);
//	        ssl.setKeyStore(keyStore);
//	        ssl.setKeyAlias(alias);
//	        ssl.setKeyStorePassword(keyStorePassword);
//	        
//	        if(!StringUtils.isBlank(keyPassword)) {
//	        	ssl.setKeyPassword(keyPassword);
//	        }
//	        
//	        Http2 http2 = new Http2();
//	        http2.setEnabled(false);
//	        serverFactory.addServerCustomizers(new SslServerCustomizer(ssl, http2, null));
//	        serverFactory.setPort(https_port);
//	    }
//	}
    
    
//		@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
//        @Configuration
//        public class HttpToHttpsRedirectConfig {
//
//            @PostConstruct
//            public void startRedirectServer() {
//                NettyReactiveWebServerFactory httpNettyReactiveWebServerFactory = new NettyReactiveWebServerFactory(port);
//                httpNettyReactiveWebServerFactory.getWebServer((request, response) -> {
//                    URI uri = request.getURI();
//                    URI httpsUri;
//                    try {
//                        httpsUri = new URI("https", uri.getUserInfo(), uri.getHost(), https_port, uri.getPath(), uri.getQuery(), uri.getFragment());
//                    } catch (URISyntaxException e) {
//                        return Mono.error(e);
//                    }
//                    response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//                    response.getHeaders().setLocation(httpsUri);
//                    return response.setComplete();
//                }).start();
//            }
//        }
        
    @ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
    @Bean
    WebFilter httpsRedirectFilter() {
		    return new WebFilter() {
//		        @Override
		        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		            URI originalUri = exchange.getRequest().getURI();

		            //here set your condition to http->https redirect
		            List<String> forwardedValues = exchange.getRequest().getHeaders().get("x-forwarded-proto");
		            if (forwardedValues != null && forwardedValues.contains("http")) {
		                try {
		                    URI mutatedUri = new URI("https",
		                            originalUri.getUserInfo(),
		                            originalUri.getHost(),
		                            https_port,
		                            originalUri.getPath(),
		                            originalUri.getQuery(),
		                            originalUri.getFragment());
		                    ServerHttpResponse response = exchange.getResponse();
		                    response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		                    response.getHeaders().setLocation(mutatedUri);
		                    return Mono.empty();
		                } catch (URISyntaxException e) {
		                    throw new IllegalStateException(e.getMessage(), e);
		                }
		            }
		            return chain.filter(exchange);
		        }
		    };
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
