//package com.cropdeal.usermanagement.model;
//
//import com.okta.sdk.authc.credentials.ClientCredentials;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class MyClientCredentials implements ClientCredentials {
//	private String clientId;
//	private String clientSecret;
//
//	public MyClientCredentials(String clientId, String clientSecret) {
//		this.clientId = clientId;
//		this.clientSecret = clientSecret;
//	}
//
//	public String getClientId() {
//		return clientId;
//	}
//
//	public String getClientSecret() {
//		return clientSecret;
//	}
//
//	@Override
//	public Map<String, String> getCredentials() {
//		Map<String, String> credentialsMap = new HashMap<>();
//		credentialsMap.put("clientId", clientId);
//		credentialsMap.put("clientSecret", clientSecret);
//		return credentialsMap;
//	}
//}
