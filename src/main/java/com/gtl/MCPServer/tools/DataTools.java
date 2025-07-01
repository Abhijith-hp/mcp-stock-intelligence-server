package com.gtl.MCPServer.tools;

import org.springframework.stereotype.Component;

import com.gtl.MCPServer.dto.CurrentRateResponse;
import com.gtl.MCPServer.dto.HistoryDataResponse;
import com.gtl.MCPServer.dto.InstrumentSearchResponse;
import com.gtl.MCPServer.dto.SecurityRateResponse;
import com.gtl.MCPServer.dto.StockRequest;
import com.gtl.MCPServer.service.APIServices;
import com.gtl.MCPServer.utilities.AuthSessionManager;
import com.gtl.MCPServer.utilities.HelperFunctions;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import com.gtl.MCPServer.authentication.SessionAuthRegistry;
import io.modelcontextprotocol.spec.McpServerSession;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.*;

@Component
@Log4j2
public class DataTools {
    private final APIServices apiService;
    
    @Autowired
    private final HelperFunctions helperFunctions;

    @Autowired
    private final AuthSessionManager authSessionManager;

    @Autowired
    private SessionAuthRegistry sessionAuthRegistry;


    public DataTools(APIServices apiService,HelperFunctions helperFunctions, AuthSessionManager authSessionManager) {
        this.apiService = apiService;
        this.helperFunctions = helperFunctions;
        this.authSessionManager = authSessionManager;
      
    }

     /**
     * Tool A - Search instrument to get venueScriptCode
     */
    @Tool(name = "searchInstruments", description = "Finds venueScriptCode using the security name.")
    public String searchInstruments(String securityName) {
       
        Map<String, String> symbolMap = Map.of(
            "infosys", "INFY",
            "tcs", "TCS",
            "reliance", "RELIANCE"
        );

        String code = symbolMap.getOrDefault(securityName.toLowerCase(), null);
        if (code == null) return null;

        return code;
    }
    
    @Tool(name = "getCurrentRates", description = "Gets the current market rate of a security using venueScriptCode.")
    public String getCurrentRates(String venueScriptCode) {
        
        Map<String, String> priceMap = Map.of(
            "INFY", "₹1510.25",
            "TCS", "₹3650.75",
            "RELIANCE", "₹2890.50"
        );

        String rate = priceMap.getOrDefault(venueScriptCode, "Unavailable");
        return "Current rate for " + venueScriptCode + " is " + rate;
    }

    @Tool(
  name = "getRatesForSecurityBatch",
  description = "Takes a list of security names and returns their venueScriptCodes and current rates."
)
public List<SecurityRateResponse> getRatesForSecurities(List<String> securityNames) {
    Map<String, String> symbolMap = Map.of(
        "infosys", "INFY",
        "tcs", "TCS",
        "reliance", "RELIANCE"
    );

    Map<String, String> priceMap = Map.of(
        "INFY", "₹1510.25",
        "TCS", "₹3650.75",
        "RELIANCE", "₹2890.50"
    );

    List<SecurityRateResponse> result = new ArrayList<>();

    for (String name : securityNames) {
        String venueCode = symbolMap.get(name.toLowerCase());
        if (venueCode != null) {
            String price = priceMap.getOrDefault(venueCode, "Price unavailable");
            result.add(new SecurityRateResponse(name, venueCode, price));
        } else {
            result.add(new SecurityRateResponse(name, "NOT_FOUND", "N/A"));
        }
    }

    return result;
}

    // @Tool(
    //     name = "getHistoricalRates",
    //     description = "Get historical chart data within a given date range."
    // )
    // public List<HistoryDataResponse> getHistoricalRates(ToolContext toolContext,
    //     @Parameter(name = "venueScriptCode", description = "Scrip code of the security listed on the trading venue. Used to uniquely identify the security in that venue.", example = "513353") String venueScriptCode,
    //     @Parameter(name = "venueCode", description = "Trading venue or exchange where the security is listed (e.g., BSE, NSE).", example = "BSE") String venueCode,
    //     @Parameter(name = "startDate", description = "Start date for historical data in yyyyMMdd format (no dashes).", example = "20240323") String startDate,
    //     @Parameter(name = "endDate", description = "End date for historical data in yyyyMMdd format (no dashes).", example = "20240324") String endDate,
    //     @Parameter(name = "securityCode", description = "Security symbol or code used for identifying the stock (e.g., COCHINM).", example = "COCHINM") String securityCode
    // ) {
    //      McpServerSession sessionId = null;
    //      try {
    //     sessionId = authSessionManager.extractSessionFromContext(toolContext);
    // } catch (Exception e) {
    //    log.info("Error occur in extracting session ID");
    //     e.printStackTrace();
    // }
    // if (!sessionAuthRegistry.isAuthenticated(sessionId.getId())) {
    //     HistoryDataResponse errorResponse = new HistoryDataResponse();
    //          errorResponse.setError("Authentication required. Please login at: " +  helperFunctions.buildLoginUrl(sessionId.getId()));
    //         return List.of(errorResponse);
          
    //     } 
       
    //     try {
    //         String formattedStartDate = HelperFunctions.formatToYYYYMMDD(startDate);
    //         String formattedEndDate = HelperFunctions.formatToYYYYMMDD(endDate);
    //         List<HistoryDataResponse> results = new ArrayList<>();
    //         HistoryDataResponse result = new HistoryDataResponse();
    //         result.setSecurityCode(securityCode);
    //         result.setVenueCode(venueCode);
    //         result.setSeries("");
    //         result.setVenuescripcode(venueScriptCode);
    //         try {
    //             Map<String,Map<String, Object>>historyList = apiService.getRatesFromExternalApi(securityCode, venueCode, venueScriptCode,formattedStartDate,formattedEndDate);
    //             result.setHistory(historyList);
    //         } catch (Exception e) {
    //             log.error("Error fetching history for {}: {}", securityCode, e.getMessage());
    //             result.setError(e.getMessage());
    //         }
    //         results.add(result);
    //         log.info("Fetched {} history records {}", results.size(), results);
    //         return results;
    //     } catch (Exception ex) {
    //         log.error("Error fetching portfolio history: {}", ex.getMessage(), ex);
    //         HistoryDataResponse errorResponse = new HistoryDataResponse();
    //         errorResponse.setError("Failed to fetch portfolio history: " + ex.getMessage());
    //         return List.of(errorResponse);
    //     }
    // }

//   @Tool(
//     name = "getCurrentRates",
//      description = "Get the current rate of securities. Provide a list of stocks with 'venuecode' and 'venuescriptcode'."
//   )
//   public List<CurrentRateResponse> getCurrentRates(ToolContext toolContext,
//      @Parameter(name = "stocks", description = "Get the current rate of a security listed on a trading venue. Input should be a list of stocks, each with fields: venuecode (e.g., 'NSE') and venuescriptcode (e.g., '7627')")
//     List<StockRequest> stocks){
//        try {
//         List<Map<String, Object>> responseList = apiService.getIntradayRates(stocks);
//         List<CurrentRateResponse> resultList = new ArrayList<>();

//         for (Map<String, Object> item : responseList) {
//             try {
//                 CurrentRateResponse res = new CurrentRateResponse();
//                 res.setVenue((String) item.get("venue"));
//                 res.setVenuescriptcode((String) item.get("venuescriptcode"));
//                 res.setLastTradePrice((String) item.get("LastTradePrice"));
//                 res.setLastTradeTime((String) item.get("LastTradeTime"));
//                 resultList.add(res);
//             } catch (Exception e) {
//                 log.warn("Skipping malformed item: {}", item);
//             }
//         }

//         return resultList;
//     } catch (Exception e) {
//         log.error("Failed to fetch intraday rates", e);
//         CurrentRateResponse errorResponse = new CurrentRateResponse();
//         errorResponse.setError("Server error while fetching intraday rates: " + e.getMessage());
//         return List.of(errorResponse);
//     }
//     }
}
