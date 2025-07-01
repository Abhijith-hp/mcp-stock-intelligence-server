package com.gtl.MCPServer.dto;


public class SecurityRateResponse {
    private String securityName;
    private String venueScriptCode;
    private String rate;

    public SecurityRateResponse(String securityName, String venueScriptCode, String rate) {
        this.securityName = securityName;
        this.venueScriptCode = venueScriptCode;
        this.rate = rate;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getVenueScriptCode() {
        return venueScriptCode;
    }

    public String getRate() {
        return rate;
    }
}

