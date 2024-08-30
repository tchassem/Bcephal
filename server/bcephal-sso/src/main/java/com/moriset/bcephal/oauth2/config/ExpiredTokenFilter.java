//package com.moriset.bcephal.oauth2.config;
//
//import java.io.IOException;
//import java.time.Clock;
//import java.time.Duration;
//import java.time.Instant;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
//import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
//import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.OAuth2Error;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
//import org.springframework.security.oauth2.core.oidc.OidcIdToken;
//import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
//import org.springframework.security.oauth2.jwt.JwtException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//@Component
//public class ExpiredTokenFilter extends OncePerRequestFilter {
//
//    private static final Logger log = LoggerFactory.getLogger(ExpiredTokenFilter.class);
//
//    private Duration accessTokenExpiresSkew = Duration.ofMillis(1000);
//
//    private Clock clock = Clock.systemUTC();
//
//    @Autowired
//    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
//
////    @Autowired
////    CustomOidcUserService userService;
//    
//    @Autowired
//    OidcUserService userService;
//
//    private DefaultRefreshTokenTokenResponseClient accessTokenResponseClient;
//
//    private JwtDecoderFactory<ClientRegistration> jwtDecoderFactory;
//
//    private static final String INVALID_ID_TOKEN_ERROR_CODE = "invalid_id_token";
//
//    public ExpiredTokenFilter() {
//        super();
//        this.accessTokenResponseClient = new DefaultRefreshTokenTokenResponseClient();
//        this.jwtDecoderFactory = new OidcIdTokenDecoderFactory();
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        log.debug("my custom filter called ");
//        /**
//         * check if authentication is done.
//         */
//        if (null != SecurityContextHolder.getContext().getAuthentication()) {
//            OAuth2AuthenticationToken currentUser = (OAuth2AuthenticationToken) SecurityContextHolder.getContext()
//                    .getAuthentication();
//            OAuth2AuthorizedClient authorizedClient = this.oAuth2AuthorizedClientService
//                    .loadAuthorizedClient(currentUser.getAuthorizedClientRegistrationId(), currentUser.getName());
//            /**
//             * Check if token existing token is expired.
//             */
//            if (isExpired(authorizedClient.getAccessToken())) {
//
//                /*
//                 * do something to get new access token
//                 */
//                log.debug(
//                        "=========================== Token Expired !! going to refresh ================================================");
//                ClientRegistration clientRegistration = authorizedClient.getClientRegistration();
//                /*
//                 * Call Auth server token endpoint to refresh token. 
//                 */
//                OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest = new OAuth2RefreshTokenGrantRequest(
//                        clientRegistration, authorizedClient.getAccessToken(), authorizedClient.getRefreshToken());
//                OAuth2AccessTokenResponse accessTokenResponse = this.accessTokenResponseClient
//                        .getTokenResponse(refreshTokenGrantRequest);
//                /*
//                 * Convert id_token to OidcToken.
//                 */
//                OidcIdToken idToken = createOidcToken(clientRegistration, accessTokenResponse);
//                /*
//                 * Since I have already implemented a custom OidcUserService, reuse existing
//                 * code to get new user. 
//                 */
//                OidcUser oidcUser = this.userService.loadUser(new OidcUserRequest(clientRegistration,
//                        accessTokenResponse.getAccessToken(), idToken, accessTokenResponse.getAdditionalParameters()));
//
//                log.debug(
//                        "=========================== Token Refresh Done !! ================================================");
//                /*
//                 * Print old and new id_token, just in case.
//                 */
//                DefaultOidcUser user = (DefaultOidcUser) currentUser.getPrincipal();
//                log.debug("new id token is " + oidcUser.getIdToken().getTokenValue());
//                log.debug("old id token was " + user.getIdToken().getTokenValue());
//                /*
//                 * Create new authentication(OAuth2AuthenticationToken).
//                 */
//                OAuth2AuthenticationToken updatedUser = new OAuth2AuthenticationToken(oidcUser,
//                        oidcUser.getAuthorities(), currentUser.getAuthorizedClientRegistrationId());
//                /*
//                 * Update access_token and refresh_token by saving new authorized client.
//                 */
//                OAuth2AuthorizedClient updatedAuthorizedClient = new OAuth2AuthorizedClient(clientRegistration,
//                        currentUser.getName(), accessTokenResponse.getAccessToken(),
//                        accessTokenResponse.getRefreshToken());
//                this.oAuth2AuthorizedClientService.saveAuthorizedClient(updatedAuthorizedClient, updatedUser);
//                /*
//                 * Set new authentication in SecurityContextHolder.
//                 */
//                SecurityContextHolder.getContext().setAuthentication(updatedUser);
//            }
//
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private Boolean isExpired(OAuth2AccessToken oAuth2AccessToken) {
//        Instant now = this.clock.instant();
//        Instant expiresAt = oAuth2AccessToken.getExpiresAt();
//        return now.isAfter(expiresAt.minus(this.accessTokenExpiresSkew));
//    }
//
//    private OidcIdToken createOidcToken(ClientRegistration clientRegistration,
//            OAuth2AccessTokenResponse accessTokenResponse) {
//        JwtDecoder jwtDecoder = this.jwtDecoderFactory.createDecoder(clientRegistration);
//        org.springframework.security.oauth2.jwt.Jwt jwt;
//        try {
//            jwt = jwtDecoder
//                    .decode((String) accessTokenResponse.getAdditionalParameters().get(OidcParameterNames.ID_TOKEN));
//        } catch (JwtException ex) {
//            OAuth2Error invalidIdTokenError = new OAuth2Error(INVALID_ID_TOKEN_ERROR_CODE, ex.getMessage(), null);
//            throw new OAuth2AuthenticationException(invalidIdTokenError, invalidIdTokenError.toString(), ex);
//        }
//        OidcIdToken idToken = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(),
//                jwt.getClaims());
//        return idToken;
//    }
//}
