package com.gtl.MCPServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    
   
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Set timeouts to 5 minutes
        factory.setConnectTimeout(300000); 
        factory.setReadTimeout(300000);
    
        RestTemplate restTemplate = new RestTemplate(factory);

        
        return restTemplate;
    }
}
