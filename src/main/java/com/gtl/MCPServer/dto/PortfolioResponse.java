package com.gtl.MCPServer.dto;

public class PortfolioResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Constructors
    public PortfolioResponse() {}

    public PortfolioResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    } 
}
