package com.moriset.bcephal.gateway;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

public class CustomTokenResponseConverter implements  Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
    @SuppressWarnings("unused")
	private final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream.of(
        OAuth2ParameterNames.ACCESS_TOKEN, 
        OAuth2ParameterNames.TOKEN_TYPE, 
        OAuth2ParameterNames.EXPIRES_IN, 
        OAuth2ParameterNames.REFRESH_TOKEN, 
        OAuth2ParameterNames.SCOPE).collect(Collectors.toSet());

    public CustomTokenResponseConverter() {
	}
    
  
	
    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
    	
    	
        String accessToken = (String)tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
        @SuppressWarnings("unused")
		String accessTokenType_ = (String)tokenResponseParameters.get(OAuth2ParameterNames.TOKEN_TYPE);
        Integer expiresIn = (Integer)tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN);
        String refreshToken = (String)tokenResponseParameters.get(OAuth2ParameterNames.REFRESH_TOKEN);
        TokenType accessTokenType = TokenType.BEARER;

        Set<String> scopes = Collections.emptySet();
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
            String scope = (String)tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, ","))
                .collect(Collectors.toSet());
        }
        Map<String, Object> tokenResponseParameters_ = tokenResponseParameters;
        		
        return OAuth2AccessTokenResponse.withToken(accessToken)
          .tokenType(accessTokenType)
          .expiresIn(Long.valueOf(expiresIn))
          .scopes(scopes)
          .refreshToken(refreshToken)
          .additionalParameters(tokenResponseParameters_)
          .build();
    }


}
