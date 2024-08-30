package com.moriset.bcephal.oauth2.config;

//@Configuration
//public class RefreshTokenFilterConfig {
//
//    @Bean("refreshTokenFilter")
//    GenericFilterBean refreshTokenFilter(OAuth2AuthorizedClientService clientService,ClientRegistrationRepository clientRegistrationRepository) {
//        return new GenericFilterBean() {
//            @Override
//            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                if (authentication != null && authentication instanceof JwtAuthenticationToken) {
//                	JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
//                	Jwt jwt = (Jwt) token.getPrincipal();
//                    OAuth2AuthorizedClient client =
//                            clientService.loadAuthorizedClient(
//                                    "keycloak",
//                                    jwt.getSubject());
//                    OAuth2AccessToken accessToken = client.getAccessToken();
//                    if (accessToken.getExpiresAt().isBefore(Instant.now())) {
//                        SecurityContextHolder.getContext().setAuthentication(null);
//                    }
//                }
//                filterChain.doFilter(servletRequest, servletResponse);
//            }
//        };
//    }
//}