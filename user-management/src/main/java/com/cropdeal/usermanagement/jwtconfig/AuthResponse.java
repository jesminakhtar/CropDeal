package com.cropdeal.usermanagement.jwtconfig;

import java.util.Collection;

public class AuthResponse {

	private String accessToken;
	private long expireAt;
	private Collection<String> authorities;

	public AuthResponse() {
	}

	public AuthResponse(String accessToken, long expireAt, Collection<String> authorities) {
		this.accessToken = accessToken;
		this.expireAt = expireAt;
		this.authorities = authorities;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(long expireAt) {
		this.expireAt = expireAt;
	}

	public Collection<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<String> authorities) {
		this.authorities = authorities;
	}
}
