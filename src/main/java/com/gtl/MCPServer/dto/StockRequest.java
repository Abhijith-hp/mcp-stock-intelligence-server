package com.gtl.MCPServer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StockRequest {
    @JsonProperty("venuecode")
    private String venuecode;
    @JsonProperty("venuescriptcode")
    private String venuescriptcode;
}
