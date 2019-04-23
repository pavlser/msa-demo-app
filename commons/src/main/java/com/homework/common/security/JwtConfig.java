package com.homework.common.security;

public class JwtConfig {

	private String Uri;
	private String header;
	private String prefix;
	private int expiration;
	private String secret;

	public String getUri() {
		return Uri;
	}

	public String getHeader() {
		return header;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getExpiration() {
		return expiration;
	}

	public String getSecret() {
		return secret;
	}

}