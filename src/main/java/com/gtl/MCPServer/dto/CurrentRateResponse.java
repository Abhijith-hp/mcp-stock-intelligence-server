package com.gtl.MCPServer.dto;

import java.util.Map;

import lombok.Data;

@Data
public class CurrentRateResponse {
    private String venuescriptcode;
    private String venue;
    private String lastTradePrice;
    private String lastTradeTime;
    private String error;
}
