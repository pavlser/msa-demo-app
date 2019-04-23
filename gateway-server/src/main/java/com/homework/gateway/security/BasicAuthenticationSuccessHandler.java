package com.homework.gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class BasicAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
	
	JwtTokenService tokenService;
	
	public BasicAuthenticationSuccessHandler(JwtTokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
		ServerWebExchange exchange = webFilterExchange.getExchange();
		exchange.getResponse().getHeaders().add(HttpHeaders.AUTHORIZATION, getHttpAuthHeaderValue(authentication));
		return webFilterExchange.getChain().filter(exchange);
	}

	private String getHttpAuthHeaderValue(Authentication authentication) {
		String token = String.join(" ", "Bearer", tokenFromAuthentication(authentication));
		System.out.println("------> Issued token: '" + token + "'");
		return token;
	}

	private String tokenFromAuthentication(Authentication authentication) {
		return tokenService.generateToken(
			authentication.getName(),
			authentication.getCredentials(),
			authentication.getAuthorities());
	}
	
}
