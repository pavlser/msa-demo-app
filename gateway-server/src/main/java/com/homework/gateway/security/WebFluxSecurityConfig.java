package com.homework.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class WebFluxSecurityConfig {
	
	@Value("${gateway.jwt.secret}")
	private String jwtSecret;
	
	@Bean
	public JwtTokenService tokenService() {
		return new JwtTokenService(jwtSecret);
	}
	
	public class JwtAuthManager implements ReactiveAuthenticationManager {
		@Override
		public Mono<Authentication> authenticate(Authentication authentication) {
			return Mono.just(authentication);
		}
	}
	
	public class JwtAuthConverter implements ServerAuthenticationConverter {
		@Override
		public Mono<Authentication> convert(ServerWebExchange exchange) {
			return Mono.justOrEmpty(exchange)
				.flatMap(tokenService()::getAuthHeader)
				.flatMap(tokenService()::check)
				.flatMap(tokenService()::authenticate).log();
		}
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public MapReactiveUserDetailsService userDetailsRepository() {
		System.out.println("4 ------------> " + jwtSecret);
		return new MapReactiveUserDetailsService(
			User.withUsername("user")
				.password(passwordEncoder().encode("user"))
				.roles("USER")
				.build(),
			User.withUsername("admin")
				.password(passwordEncoder().encode("admin"))
				.roles("USER", "ADMIN")
				.build());
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http.csrf().disable()
			.authorizeExchange().pathMatchers("/actuator", "/actuator/**").permitAll().and()							// make visible for health service
			.authorizeExchange().pathMatchers("/login", "/").authenticated().and()						// ask basic login on root context
			.addFilterAt(basicAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
			.authorizeExchange().pathMatchers("/api/accounts").permitAll().and()						// visible context by task query
			.authorizeExchange().pathMatchers("/api/bank/**", "/api/account/**").authenticated().and()	// protect with JWT
			.addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
		return http.build();
	}
	
	@Bean
	public ReactiveAuthenticationManager reactiveAuthManager() {
		return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsRepository());
	}

	// Filter for basic login with user/pass
	private AuthenticationWebFilter basicAuthenticationFilter() {
		AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthManager());
		filter.setAuthenticationSuccessHandler(new BasicAuthenticationSuccessHandler(tokenService()));
		return filter;
	}

	// Filter for JWT token
	private AuthenticationWebFilter bearerAuthenticationFilter() {
		AuthenticationWebFilter filter = new AuthenticationWebFilter(new JwtAuthManager());
		filter.setServerAuthenticationConverter(new JwtAuthConverter());
		return filter;
	}
	
}
