package com.gtl.MCPServer.dto;

public class InstrumentSearchResponse {
    private String securityName;
    private String venueScriptCode;

    public InstrumentSearchResponse() {}

    public InstrumentSearchResponse(String securityName, String venueScriptCode) {
        this.securityName = securityName;
        this.venueScriptCode = venueScriptCode;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public String getVenueScriptCode() {
        return venueScriptCode;
    }

    public void setVenueScriptCode(String venueScriptCode) {
        this.venueScriptCode = venueScriptCode;
    }
}
