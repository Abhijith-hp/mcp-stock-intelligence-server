package com.gtl.MCPServer.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtl.MCPServer.dto.StockRequest;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class APIServices {

    private final RestTemplate restTemplate;

    @Value("${flip.api.base-url}")
    private String baseUrl;

    @Value("${flip.api.getStockRatesBaseUrl}")
    private String getStockRatesBaseUrl;

    @Value("${flip.api.apiKey}")
    private String gateWayToFlipApiKey;

    @Value("${flip.api.getIntradayRatesEndpoint}")
    private String getIntradayRatesEndpoint;

    @Value("${flip.api.url}")
    private String flipApiUrl;

   

    public APIServices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    

    /**
     * Calls the external API with the given tradeCode and returns the JSON response as a String.
     * @param tradeCode the trade code to send
     * @return JSON response from the external API as String
     * @throws URISyntaxException 
     */
    public String getFromExternalApi(String tradecode, String endpoint) throws URISyntaxException {
        HttpHeaders headers = createJsonHeaders();
        headers.add("apiKey",gateWayToFlipApiKey);
         Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("tradecode", tradecode);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);
        log.info("Request to external API: " + requestMap + " at endpoint: " + baseUrl+endpoint);
        String apiUrl = baseUrl + endpoint;
        URI targetUrl = URI.create(apiUrl); 
        ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.POST, entity, String.class);
        log.info("Response from external API: {}", responseEntity.getBody());
        System.out.println("Response from external API: " + responseEntity.getBody());
        return responseEntity.getBody();
    }

    /**
     * Calls an external API using a full URL (GET request).
     * @param url the full URL to call
     * @return response body as String
     */
    public String getFromExternalApiRawUrl(String url) {
        try {
            log.info("Requesting external API (raw URL): {}", url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("Response from external API (raw URL): {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling external API (raw URL): {}", e.getMessage());
            throw new RuntimeException("Error calling external API: " + e.getMessage(), e);
        }
    }

    
 public String getAccessToken(String tokenUrl, String code, String userCode, String grantType, String clientId, String clientSecret) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("code", code);
        requestMap.add("userCode", userCode);
        requestMap.add("client_id", clientId);
        requestMap.add("grant_type", grantType);
        log.info("Requesting access token with code: {}, userCode: {}, clientId: {}, grantType: {}", code, userCode, clientId, grantType);
        log.info("Request body to access token: {}", requestMap);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestMap, headers);
        
        log.info("Requesting external API for token (URL): {}", tokenUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);
        log.info("Response from external API: {}", response.getBody());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.getBody());

        String accessToken = jsonNode.get("access_token").asText();
        log.info("Access token "+accessToken);
        return accessToken;

    } catch (Exception ex) {
        log.error("Error requesting access token: {}", ex.getMessage(), ex);
        throw new RuntimeException("Failed to retrieve access token", ex);
    }
}


    /**
     * Calls an external API using a full URL (GET request) and parses CSV response into a list of maps.
     * @param url the full URL to call
     * @return List of maps with keys: timestamp, open, high, low, close, volume, oi
     */
    public Map<String, Map<String, Object>> getRatesFromExternalApi(String securityCode, String venueCode, String venueScriptCode, String startDate, String endDate) {
    try {
        String apiUrl = getStockRatesBaseUrl +
           "symbol=" + securityCode + "_" + venueCode +
           "&venueCode=" + venueCode +
           "&scripCode=" + venueScriptCode +
           "&securityCode=" + securityCode +
           "&expDate=" +
           "&instType=CM" +
           "&series=" +
           "&strikePrice=" +
           "&callPut=" +
           "&style=" +
           "&remarks=" +
           "&counter=0" +
           "&startDate=" + startDate +
           "&endDate=" + endDate;
        log.info("Requesting external API (raw URL): {}", apiUrl);
        URI targetUrl = URI.create(apiUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(targetUrl, String.class);
        log.info("Response from external API (raw URL): {}", response.getBody());

        String historyData = response.getBody();
        Map<String, Map<String, Object>> historyMap = new LinkedHashMap<>();

        if (historyData != null) {
            String[] lines = historyData.split("\\r?\\n");

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                String date = parts[0]; 
                try {
                    long timestamp = Long.parseLong(date);
                    LocalDate formattedDate = Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                    date = formattedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (NumberFormatException e) {
                    log.error("Invalid timestamp format: {}", date);
                    throw new RuntimeException("Invalid timestamp format: " + date, e);
                }

                Map<String, Object> ohlc = new HashMap<>();
                ohlc.put("openPrice", parts[1]);
                ohlc.put("highPrice", parts[2]);
                ohlc.put("lowPrice", parts[3]);
                ohlc.put("closePrice", parts[4]);
                ohlc.put("volume", parts[5]);
                ohlc.put("openInterest", parts[6]);

                historyMap.put(date, ohlc);
            }
        }

        return historyMap;

    } catch (Exception e) {
        log.error("Error calling external API (raw URL): {}", e.getMessage());
        throw new RuntimeException("Error calling external API: " + e.getMessage(), e);
    }
}

public List<Map<String, Object>> getIntradayRates(List<StockRequest> stocks) {

    HttpHeaders headers = createJsonHeaders();
    headers.add("apiKey", gateWayToFlipApiKey);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("stocks", stocks);

    String apiUrl = flipApiUrl + getIntradayRatesEndpoint;
    log.info("POST request to {} with body: {}", apiUrl, requestBody);
    HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> responseEntity = restTemplate.exchange(
        URI.create(apiUrl),
        HttpMethod.POST,
        entity,
        String.class
    );

    log.info("Response from external API: {}", responseEntity.getBody());
    String responseBody = responseEntity.getBody();
    if (responseBody == null || responseBody.isEmpty()) {
        log.warn("Received empty response from external API");
        return List.of(); 
    }else{
        log.info("Received response from external API: {}", responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> responseList;
        try {
           responseList = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Error parsing response JSON: {}", e.getMessage());
            throw new RuntimeException("Error parsing response JSON: " + e.getMessage(), e);
        }
        
    return responseList;
    }

}


    /**
     * Calls an external API using a full URL (GET request) and parses CSV response into a map keyed by date string.
     * Each value is a map with keys: o, h, l, c, v, oi (open, high, low, close, volume, oi).
     * @param url the full URL to call
     * @return Map<String, Map<String, Object>> where key is date (yyyy-MM-dd) and value is ohlcv/oi map
     */
    public java.util.Map<String, java.util.Map<String, Object>> getHistoryByDateFromExternalApi(String url) {
        try {
            log.info("Requesting external API (raw URL): {}", url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("Response from external API (raw URL): {}", response.getBody());
            String historyData = response.getBody();
            java.util.Map<String, java.util.Map<String, Object>> historyMap = new java.util.HashMap<>();
            if (historyData != null) {
                String[] lines = historyData.split("\\r?\\n");
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length < 7) continue;
                    long timestamp = 0L;
                    try { timestamp = Long.parseLong(parts[0]); } catch (Exception ignore) {}
                    java.time.LocalDate date = java.time.Instant.ofEpochMilli(timestamp)
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    String dateStr = date.toString();
                    java.util.Map<String, Object> ohlcv = new java.util.HashMap<>();
                    ohlcv.put("openPrice", parts[1]);
                    ohlcv.put("highPrice", parts[2]);
                    ohlcv.put("lowPrice", parts[3]);
                    ohlcv.put("closePrice", parts[4]);
                    ohlcv.put("volume", parts[5]);
                    ohlcv.put("openInteresr", parts[6]);
                    historyMap.put(dateStr, ohlcv);
                }
            }
            return historyMap;
        } catch (Exception e) {
            log.error("Error calling external API (raw URL): {}", e.getMessage());
            throw new RuntimeException("Error calling external API: " + e.getMessage(), e);
        }
    }

}
