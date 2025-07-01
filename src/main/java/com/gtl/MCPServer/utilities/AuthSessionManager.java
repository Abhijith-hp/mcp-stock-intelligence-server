package com.gtl.MCPServer.utilities;


import com.gtl.MCPServer.authentication.SessionAuthRegistry;
import com.gtl.MCPServer.authentication.SessionValidation;
import com.gtl.MCPServer.dto.UserDetailsContext;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@Log4j2
public class AuthSessionManager {

    @Autowired
    private SessionAuthRegistry sessionAuthRegistry;

    public String getClientName(ToolContext toolContext) throws Exception {

        McpSchema.Implementation clientInfo = getClientInfoField(toolContext.getContext().get("exchange"));
        if (clientInfo != null) {
            return clientInfo.name();
        } else {
            throw new IllegalStateException("Exchange is not an instance of McpSyncServerExchange or McpAsyncServerExchange");
        }
      
    }
    public McpServerSession extractSessionFromContext(ToolContext toolContext) throws Exception {
        Object exchangeObj = toolContext.getContext().get("exchange");
        return getSessionField(exchangeObj);
        
    }

    public SessionValidation validateSessionTradeCode(String sessionId, String requestedTradeCode) {
        if (sessionId == null || !sessionAuthRegistry.isAuthenticated(sessionId)) {
            log.warn("Session ID is not authenticated or missing.");
            return SessionValidation.failure("User is not authenticated. Please login first.");
        }

        Object authObj = sessionAuthRegistry.getAuthData(sessionId);
        if (!(authObj instanceof UsernamePasswordAuthenticationToken token)) {
            return SessionValidation.failure("Invalid session authentication.");
        }

        Object principal = token.getPrincipal();
        if (!(principal instanceof UserDetailsContext userDetails)) {
            return SessionValidation.failure("Invalid user context.");
        }

        if (requestedTradeCode != null && !userDetails.getUsername().equalsIgnoreCase(requestedTradeCode)) {
            return SessionValidation.failure("Access denied: Not your tradecode.");
        }

        return SessionValidation.success();
    }

    public String getSessionTradeCode(String sessionId) {
        if (sessionId == null || !sessionAuthRegistry.isAuthenticated(sessionId)) {
            log.warn("Session ID is not authenticated or missing.");
            return null;
        }

        Object authObj = sessionAuthRegistry.getAuthData(sessionId);
        if (!(authObj instanceof UsernamePasswordAuthenticationToken token)) {
            return null;
        }

        Object principal = token.getPrincipal();
        if (!(principal instanceof UserDetailsContext userDetails)) {
            return null;
        }

        return userDetails.getUsername();
    }

    public boolean isChartServerClient(String clientInfo) {
    if (clientInfo == null || !clientInfo.contains("|")) {
        return false;
    }
    String[] parts = clientInfo.split("\\|");
    return parts.length > 0 && "CSA".equals(parts[0]);
}
 
    private Object extractField(Object obj, Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private McpServerSession getSessionField(Object exchangeObj) throws Exception {
        if (exchangeObj instanceof McpSyncServerExchange syncExchange) {
            Object asyncExchangeObj = extractField(syncExchange, McpSyncServerExchange.class, "exchange");
            if (asyncExchangeObj instanceof McpAsyncServerExchange asyncExchange) {
                McpServerSession mcpSession = (McpServerSession) extractField(asyncExchange, McpAsyncServerExchange.class, "session");
                log.info("Session ID extracted from sync exchange: {}", mcpSession.getId());
                McpSchema.Implementation clientInfo = (McpSchema.Implementation) extractField(asyncExchange, McpAsyncServerExchange.class, "clientInfo");
                log.info("Client info extracted from async exchange: {}", clientInfo);
                log.info("the name of the client is: {}", clientInfo.name());
                return mcpSession;
            } else {
                throw new IllegalStateException("Inner exchange is not an instance of McpAsyncServerExchange");
            }
        } else if (exchangeObj instanceof McpAsyncServerExchange asyncExchange) {
            McpServerSession mcpSession = (McpServerSession) extractField(asyncExchange, McpAsyncServerExchange.class, "session");
            log.info("Session ID extracted from async exchange: {}", mcpSession.getId());
            McpSchema.Implementation clientInfo = (McpSchema.Implementation) extractField(asyncExchange, McpAsyncServerExchange.class, "clientInfo");
            log.info("Client name is : {}", clientInfo.name());
            log.info("Client info extracted from async exchange: {}", clientInfo);
            return mcpSession;
        } else {
            throw new IllegalStateException("Exchange is not an instance of McpSyncServerExchange or McpAsyncServerExchange");
        }
    }

    private McpSchema.Implementation getClientInfoField(Object exchangeObj) throws Exception {
        if (exchangeObj instanceof McpSyncServerExchange syncExchange) {
            Object asyncExchangeObj = extractField(syncExchange, McpSyncServerExchange.class, "exchange");
            if (asyncExchangeObj instanceof McpAsyncServerExchange asyncExchange) {
                McpServerSession mcpSession = (McpServerSession) extractField(asyncExchange, McpAsyncServerExchange.class, "session");
                log.info("Session ID extracted from sync exchange: {}", mcpSession.getId());
                McpSchema.Implementation clientInfo = (McpSchema.Implementation) extractField(asyncExchange, McpAsyncServerExchange.class, "clientInfo");
                log.info("Client info extracted from async exchange: {}", clientInfo);
                log.info("the name of the client is: {}", clientInfo.name());
                return clientInfo;
            } else {
                throw new IllegalStateException("Inner exchange is not an instance of McpAsyncServerExchange");
            }
        } else if (exchangeObj instanceof McpAsyncServerExchange asyncExchange) {
            McpServerSession mcpSession = (McpServerSession) extractField(asyncExchange, McpAsyncServerExchange.class, "session");
            log.info("Session ID extracted from async exchange: {}", mcpSession.getId());
            McpSchema.Implementation clientInfo = (McpSchema.Implementation) extractField(asyncExchange, McpAsyncServerExchange.class, "clientInfo");
            log.info("Client name is : {}", clientInfo.name());
            log.info("Client info extracted from async exchange: {}", clientInfo);
            return clientInfo;
        } else {
            throw new IllegalStateException("Exchange is not an instance of McpSyncServerExchange or McpAsyncServerExchange");
        }
    }
}

