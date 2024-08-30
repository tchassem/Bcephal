/**
 * 
 */
package com.moriset.bcephal.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.moriset.bcephal.manager.service.AlfrescoService;
import com.moriset.bcephal.manager.service.BaseService;
import com.moriset.bcephal.manager.service.GitService;
import com.moriset.bcephal.manager.service.LocalSystemService;
import com.moriset.bcephal.manager.service.microsoft.MicrosoftSharePointService;
import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;

import lombok.extern.slf4j.Slf4j;
/**
 * @author Joseph Wambo
 *
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration extends AbstractSecurityConfiguration{

	@Override
	protected List<String> getAuthorizePathMathers() {
		return new ArrayList<>(Arrays.asList("/file-manager/download/**"));
	}
	
	@Bean("baseService")
	protected BaseService configBaseService(AlfrescoService alfrescoService, LocalSystemService localSystemService,
			MicrosoftSharePointService microsoftSharePointService, @Autowired RestTemplate restTemplate,
			ResourceLoader resourceLoader) {

		if (localSystemService.manager.isAlfresco()) {
			alfrescoService.manager.init();
			restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(alfrescoService.manager.getBaseUrl()));			
			return alfrescoService;
		} else if (localSystemService.manager.isGit()) {
			return new GitService();
		} else if (localSystemService.manager.isLocalSystem()) {
			localSystemService.manager.init();
			return localSystemService;
		} else {
			try {
				microsoftSharePointService.httpClientForSharePoint.init();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("{}", e);
			}
			return microsoftSharePointService;
		}
	}
}


