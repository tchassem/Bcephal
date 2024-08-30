package com.moriset.bcephal.config.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConditionalOnBean(value = MessagingHandler.class)
@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
@Component
@Data
public class KeystoreClient{
		@Value("${activemq.client.keystore.trustPassword:mok145oloKMDYBG@1*24}589658klUFdc32}")
		private String trustPassword;
		@Value("${activemq.client.keystore.trustPassword:mok145oloWNMTGD@k*412578rbdg@QMBV45DFTS}")
		private String keyStorePassword;	
		
		
		@Value("${activemq.client.keystore:classpath:client.keystore}")
		private String keyStorePath;
		
		@Value("${activemq.client.trust:classpath:client.ts}")
		private String trustPath;
}
