package com.gtl.MCPServer.dto;
import java.util.Map;

public class HistoryDataResponse {
    private String securityCode;
    private String venueCode;
    private String series;
    private String venuescripcode;
    private String url;
    private Map<String,Map<String, Object>> data;
    private String error;


     // No-argument constructor
    public HistoryDataResponse() {
    }

    // All-argument constructor
    public HistoryDataResponse(String securityCode, String venueCode, String series,
                               String venuescripcode, String url,
                               Map<String,Map<String, Object>> data, String error) {
        this.securityCode = securityCode;
        this.venueCode = venueCode;
        this.series = series;
        this.venuescripcode = venuescripcode;
        this.url = url;
        this.data = data;
        this.error = error;
    }

    // Getters and Setters
    public String getSecurityCode() { return securityCode; }
    public void setSecurityCode(String securityCode) { this.securityCode = securityCode; }

    public String getVenueCode() { return venueCode; }
    public void setVenueCode(String venueCode) { this.venueCode = venueCode; }

    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }

    public String getVenuescripcode() { return venuescripcode; }
    public void setVenuescripcode(String venuescripcode) { this.venuescripcode = venuescripcode; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Map<String,Map<String, Object>> getHistory() { return data; }
    public void setHistory(Map<String,Map<String, Object>> data) { this.data = data; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}

