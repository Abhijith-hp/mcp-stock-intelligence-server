package com.gtl.MCPServer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRequest {

    @JsonProperty("tradecode")
    private String tradecode;

    @JsonProperty("email")
    private String email;

    @JsonProperty("tokenExpiry")
    private int tokenExpiry;

    @JsonProperty("sessionId")
    private String sessionId;

    // No-args constructor for Jackson
    public AuthRequest() {}
    
    public String getTradecode() {
        return tradecode;
    }

    public void setTradecode(String tradecode) {
        this.tradecode = tradecode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(int tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
