package com.gtl.MCPServer.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import java.lang.reflect.Field;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import com.gtl.MCPServer.authentication.SessionAuthRegistry;
import com.gtl.MCPServer.dto.PortfolioResponse;
import com.gtl.MCPServer.service.APIServices;
import com.gtl.MCPServer.utilities.AuthSessionManager;
import com.gtl.MCPServer.utilities.HelperFunctions;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerSession;
import lombok.extern.log4j.Log4j2;
import java.util.*;
@Controller
@Log4j2
public class PortfoliosTools {

    @Autowired
    private final APIServices apiService;

    @Autowired
    private final HelperFunctions helperFunctions;

    @Autowired
    private final AuthSessionManager authSessionManager;

    @Autowired
    private SessionAuthRegistry sessionAuthRegistry;

    @Value("${flip.api.getPortfoliosEndpoint}")
    private String getPortfoliosEndpoint;   
    
    @Autowired
    public PortfoliosTools(
            APIServices apiService,
            HelperFunctions helperFunctions,
            AuthSessionManager authSessionManager) {
        this.apiService = apiService;
        this.helperFunctions = helperFunctions;
        this.authSessionManager = authSessionManager;
       
    }

   

    @Tool(name = "getPortfolio", description = "Fetches portfolio details from the external API.")
    public PortfolioResponse<String> getPortfolio(ToolContext toolContext) {
        try{

        
          String clientName =  authSessionManager.getClientName(toolContext);
          log.info("Client Name: " + clientName);
          boolean chatServerClient = authSessionManager.isChartServerClient(clientName);
          log.info("Is Chat Server Client: " + chatServerClient);
          String sessionId = null;
          if (chatServerClient) {
            sessionId = clientName;
            log.info("Session ID: From chat server client " + sessionId);
          }else{
            sessionId = authSessionManager.extractSessionFromContext(toolContext).getId();
            log.info("Session ID: From out source application" +sessionId);
          }
         if (!sessionAuthRegistry.isAuthenticated(sessionId) && !chatServerClient) {
            log.info("Server Session ID is not authenticated: " + sessionId);
           return new PortfolioResponse<>(false,  "Unauthenticated","Please login " + helperFunctions.buildLoginUrl(sessionId));
     }else if (!sessionAuthRegistry.isAuthenticated(sessionId) && chatServerClient) {
           return new PortfolioResponse<>(false,  "Unauthenticated","Please login by refreshing browser: ");
         } 
           log.info("The session in Portfoilio tool is "+sessionId);
           String tradeCode = authSessionManager.getSessionTradeCode(sessionId);
           
         
        try {
            log.info("Fetching portfolio for trade code: {}", tradeCode);
            String response = apiService.getFromExternalApi(tradeCode, getPortfoliosEndpoint);
            return new PortfolioResponse<>(true, "Portfolio fetched successfully", response);
        } catch (Exception ex) {
            log.error("Error fetching portfolio for trade code {}: {}", tradeCode, ex.getMessage());
            return new PortfolioResponse<>(false, "Failed to fetch portfolio: " + ex.getMessage(), null);
        }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in getPortfolio tool: {}", e.getMessage());
            return new PortfolioResponse<>(false, "An error occurred while fetching portfolio: " + e.getMessage(), null);
        }
    }


@Tool(name = "getSessionId", description = "Returns the session ID from current exchange.")
    public String getSessionId(ToolContext toolContext) throws Exception {
        String clientName =  authSessionManager.getClientName(toolContext);
        boolean chatServerClient = authSessionManager.isChartServerClient(clientName);
        String sessionId = null;
        if (chatServerClient) {
            sessionId = clientName;
            log.info("Session ID: From chat servear client " + sessionId);
          }else{
            sessionId = authSessionManager.extractSessionFromContext(toolContext).getId();
            log.info("Session ID: From out source application" +sessionId);
          }
       
            log.info("Session ID extracted from sync exchange: {}", sessionId);
            return sessionId;
        } 

}
