package com.moriset.bcephal.config.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.Data;


@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
@Component
@Data
public class SharedUser{	
	@Value("${activemq.user.name:mksaf32ariouser45pok}")
	private String name;
	@Value("${activemq.user.password:mk+/sD(f4782*SrTo?User45kWq}")
	private String password;	
	private String clientId = "BCEPHAL__";
	
}
