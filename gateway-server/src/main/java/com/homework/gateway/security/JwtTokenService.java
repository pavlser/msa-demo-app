package com.homework.gateway.security;

import java.text.ParseException;
import java.time.Instant;
import java.time.Period;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import reactor.core.publisher.Mono;

public class JwtTokenService {

	private JWSVerifier jwsVerifier;
	private JWSSigner signer;

	public JwtTokenService(String jwtSecret) {
		if (jwtSecret != null) {
			try {
				jwsVerifier = new MACVerifier(jwtSecret);
				signer = new MACSigner(jwtSecret);
			} catch (Exception e) {
			}			
		}
	}
	
	public JWSSigner getSigner() {
		return signer;
	}
	
	public JWSVerifier getVerifier() {
		return jwsVerifier;
	}
	
	public String generateToken(String subject, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		SignedJWT signedJWT;
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.subject(subject)
				.issuer("gateway")
				.expirationTime(new Date(getExpiration()))
				.claim("roles", authorities.stream().map(GrantedAuthority.class::cast)
						.map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
				.build();
		signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
		try {
			signedJWT.sign(signer);
		} catch (JOSEException e) {
			e.printStackTrace();
		}
		return signedJWT.serialize();
	}
	
	private Predicate<SignedJWT> isNotExpired = token -> getExpirationDate(token).after(Date.from(Instant.now()));

	private Predicate<SignedJWT> validSignature = token -> {
		try {
			return token.verify(this.jwsVerifier);
		} catch (JOSEException e) {
			return false;
		}
	};

	public Mono<SignedJWT> check(String token) {
		return Mono.justOrEmpty(parseToken(token)).filter(isNotExpired).filter(validSignature);
	}

	private Date getExpirationDate(SignedJWT token) {
		try {
			return token.getJWTClaimsSet().getExpirationTime();
		} catch (ParseException e) {
			return null;
		}
	}

	private SignedJWT parseToken(String token) {
		try {
			return SignedJWT.parse(token);
		} catch (ParseException e) {
			return null;
		}
	}

	public long getExpiration() {
		return new Date().toInstant().plus(Period.ofDays(1)).toEpochMilli();
	}
	
	public Mono<Authentication> authenticate(SignedJWT signedJWT) {
		String subject;
		String auths;
		List<GrantedAuthority> authorities;
		try {
			subject = signedJWT.getJWTClaimsSet().getSubject();
			auths = (String) signedJWT.getJWTClaimsSet().getClaim("roles");
		} catch (ParseException e) {
			return Mono.empty();
		}
		authorities = Stream.of(auths.split(","))
			.map(a -> new SimpleGrantedAuthority(a))
			.collect(Collectors.toList());
		return  Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(subject, null, authorities));
    }
	
	public Mono<String> getAuthHeader(ServerWebExchange exchange) {
		return Mono.justOrEmpty(exchange.getRequest()
			.getHeaders()
			.getFirst(HttpHeaders.AUTHORIZATION));
    }

}
