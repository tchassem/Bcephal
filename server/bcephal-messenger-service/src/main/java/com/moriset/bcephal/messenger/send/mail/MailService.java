package com.moriset.bcephal.messenger.send.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@PropertySource({ "${applications.properties}" })
@ConfigurationProperties(prefix = "bcephal.notification")
@Component
public class MailService extends AbstractMailService{

}
