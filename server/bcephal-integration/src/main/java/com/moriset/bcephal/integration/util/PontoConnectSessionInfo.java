package com.moriset.bcephal.integration.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ibanity.apis.client.products.ponto_connect.models.FinancialInstitution;
import com.ibanity.apis.client.products.xs2a.models.AccountInformationAccessRequest;
import com.ibanity.apis.client.products.xs2a.models.AccountInformationAccessRequestAuthorization;
import com.ibanity.apis.client.products.xs2a.models.CustomerAccessToken;
import com.ibanity.apis.client.products.xs2a.models.FinancialInstitutionCountry;

import lombok.Data;

@Component
@Data
public class PontoConnectSessionInfo {
	private AccountInformationAccessRequest accountInformationAccessRequest;
	private CustomerAccessToken customerAccessToken;
	private List<FinancialInstitution> financialInstitutions;
	private List<FinancialInstitutionCountry> financialInstitutionCountries;
	private AccountInformationAccessRequestAuthorization authorization;

}
