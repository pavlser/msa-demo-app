package com.homework.auth.security;

import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authManager;

	private JwtConfig jwtConfig;
	
	private ObjectMapper mapper = new ObjectMapper();

	public JwtAuthenticationFilter(AuthenticationManager authManager, JwtConfig jwtConfig) {
		this.authManager = authManager;
		this.jwtConfig = jwtConfig;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(), "POST"));
	}

	@Override
	public Authentication attemptAuthentication(
		HttpServletRequest request, 
		HttpServletResponse response)
	throws AuthenticationException {
		try {
			UserCredentials creds = mapper.readValue(request.getInputStream(), UserCredentials.class);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				creds.getUsername(),
				creds.getPassword(), 
				Collections.emptyList());
			return authManager.authenticate(authToken);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(
		HttpServletRequest request, 
		HttpServletResponse response, 
		FilterChain chain, 
		Authentication auth) 
	throws IOException, ServletException {
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
			.setSubject(auth.getName())
			.claim("authorities", auth.getAuthorities()
				.stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList()))
			.setIssuedAt(new Date(now))
			.setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))
			.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
			.compact();
		response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
	}
	
}

class UserCredentials {
	
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
