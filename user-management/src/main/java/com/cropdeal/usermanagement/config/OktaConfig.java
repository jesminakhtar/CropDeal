//package com.cropdeal.usermanagement.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.cropdeal.usermanagement.model.MyClientCredentials;
//import com.okta.sdk.client.Client;
//import com.okta.sdk.client.Clients;
//
//@Configuration
//public class OktaConfig {
//
//    @Value("${okta.oauth2.issuer}")
//    private String orgUrl;
//
//    @Value("${okta.oauth2.client-id}")
//    private String clientId;
//    
//    @Value("${okta.oauth2.client-secret}")
//    private String clientSecret;
//
//    @Value("${okta.oauth2.api-token}")
//    private String apiToken;
//    
//    Logger logger = LoggerFactory.getLogger(OktaConfig.class);
//    
//    
//    
//    @Bean
//    public Client oktaClient() {
//        return Clients.builder()
//                .setOrgUrl(orgUrl)
//                .setClientCredentials( new MyClientCredentials(clientId, clientSecret))
//                .build();
//    }
//    
//
////    @Bean
////    public Client oktaClient() {
////    	logger.info(" issuer : " + orgUrl);
////    	
////        ClientBuilder clientBuilder = Clients.builder()
////                .setOrgUrl(orgUrl)
////                .setClientId(clientId)
////                .setClientCredentials(new TokenClientCredentials(apiToken));
////
////        return clientBuilder.build();
////    }
//}
