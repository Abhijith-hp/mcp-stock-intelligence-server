package com.gtl.MCPServer.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gtl.MCPServer.authentication.SessionAuthRegistry;
import com.gtl.MCPServer.utilities.AuthSessionManager;
import com.gtl.MCPServer.utilities.HelperFunctions;

import io.modelcontextprotocol.spec.McpServerSession;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class AuthenticationTools {
    

    @Autowired
    private SessionAuthRegistry sessionAuthRegistry;

    @Autowired
    private HelperFunctions helperFunctions;

    @Autowired
    private  AuthSessionManager authSessionManager;
    
    @Tool(
        name = "getloginTool",
        description = "Checks if the user is authenticated and returns a login link if not.")
    public String getloginTool(ToolContext toolContext) {
       
        McpServerSession sessionId = null;
        try {
        sessionId = authSessionManager.extractSessionFromContext(toolContext);
        log.info("The session id to login "+sessionId.getId());
    } catch (Exception e) {
       log.info("Error occur in extracting session ID");
        e.printStackTrace();
    }
         log.info("Session ID in login tool: " + sessionId.getId()); 
         if (sessionId == null || !sessionAuthRegistry.isAuthenticated(sessionId.getId())) {
            log.warn("Session ID is not created or not authenticated");
            return "Authentication required. Please login at: " + helperFunctions.buildLoginUrl(sessionId.getId());
        }
        return "User authenticated successfully. Session ID: " + sessionId + ". You can proceed with your operations.";
    }

    @Tool(
    name = "getlogoutTool",
    description = "Logs out the user by clearing session authentication data."
)
    public String getlogoutTool(ToolContext toolContext) {
        McpServerSession sessionId = null;
        try {
        sessionId = authSessionManager.extractSessionFromContext(toolContext);
         log.info("The session id to login "+sessionId.getId());
    } catch (Exception e) {
       log.info("Error occur in extracting session ID");
        e.printStackTrace();
    }
        if (sessionId == null) {
        log.warn("No session ID found for logout.");
        return "No active session found. You are already logged out.";
        }

        sessionAuthRegistry.clearSessionId(sessionId.getId());         
        log.info("User logged out successfully. Session ID cleared: {}", sessionId);
        return "You have been logged out successfully.";
}
    }

