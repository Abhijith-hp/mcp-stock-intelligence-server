package com.gtl.MCPServer.authentication;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionAuthRegistry {

    
    private final ConcurrentHashMap<String, Object> sessionMap = new ConcurrentHashMap<>();


    public void markAuthenticated(String sessionId, Object authData) {
        sessionMap.put(sessionId, authData);
    }

    public void markUnauthenticated(String sessionId) {
    if (sessionMap.containsKey(sessionId)) {
        sessionMap.put(sessionId, Boolean.FALSE); 
    } else {
        sessionMap.putIfAbsent(sessionId, Boolean.FALSE); 
    }
}

    public boolean isAuthenticated(String sessionId) {
        return sessionMap.containsKey(sessionId) && sessionMap.get(sessionId) != Boolean.FALSE;
    }

    public Object getAuthData(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void clearSessionId(String sessionId) {
        sessionMap.remove(sessionId);
    }
}
