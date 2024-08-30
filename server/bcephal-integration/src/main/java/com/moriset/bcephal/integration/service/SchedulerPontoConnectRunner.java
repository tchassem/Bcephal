package com.moriset.bcephal.integration.service;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Locale;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibanity.apis.client.products.ponto_connect.models.Token;
import com.ibanity.apis.client.products.ponto_connect.models.refresh.TokenRefreshQuery;
import com.ibanity.apis.client.services.IbanityService;
import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.integration.domain.ConnectEntity;
import com.moriset.bcephal.integration.domain.PontoConnectEntity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author MORISET-004
 *
 */

//@Component
@Service
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class SchedulerPontoConnectRunner {

	@Autowired
	PontoConnectManagerService pontoConnectManagerService;

	@Autowired
	PontoConnectEntityService pontoConnectEntityService;
	
	
	private ConnectEntity entity;
	
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER,propagation = Propagation.REQUIRED)
	public void run() throws CertificateException, IOException, OperatorCreationException, PKCSException {		
			log.info("Try to Refresh Transactions from entity : {} auth2 {} , AuthUrl: {}", entity, entity.getOauth2(),entity.getAuthorizationUrl());
			IbanityService ibanityService = pontoConnectManagerService.getIbanityService((PontoConnectEntity) entity);
			Token token = ibanityService.pontoConnectService().tokenService()
					.refresh(TokenRefreshQuery.builder()
					.clientSecret(entity.getOauth2().getClientSecret())
					.refreshToken(entity.getRefreshToken())
					.build());
			
			entity.setAccessToken(token.getAccessToken());
			entity.setRefreshToken(token.getRefreshToken());
			entity.setTokenType(token.getTokenType());
			entity.setExpiresIn(token.getExpiresIn());
			entity.setScope(token.getScope());

			pontoConnectEntityService.save((PontoConnectEntity) entity, Locale.ENGLISH);
			int count = pontoConnectManagerService.buildTransactions((PontoConnectEntity) entity, ibanityService);
			log.info("{} Transaction(s) found", count);
	}


}
