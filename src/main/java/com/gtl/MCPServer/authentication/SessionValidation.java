package com.gtl.MCPServer.authentication;

public class SessionValidation {
     private final boolean valid;
    private final String errorMessage;

    private SessionValidation(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public static SessionValidation success() {
        return new SessionValidation(true, null);
    }

    public static SessionValidation failure(String errorMessage) {
        return new SessionValidation(false, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
