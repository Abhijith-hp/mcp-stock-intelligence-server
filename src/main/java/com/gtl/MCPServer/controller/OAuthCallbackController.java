package com.gtl.MCPServer.controller;

import io.jsonwebtoken.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gtl.MCPServer.authentication.SessionAuthRegistry;
import com.gtl.MCPServer.dto.AuthRequest;
import com.gtl.MCPServer.dto.UserDetailsContext;
import com.gtl.MCPServer.service.APIServices;
import com.gtl.MCPServer.utilities.HelperFunctions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/login/oauth2/code")
public class OAuthCallbackController {

    @Value("${jwt.secret.key}")
    private static String SECRET_KEY;

    @Value("${mcp.oauth.token.url}")
    private String tokenUrl;

    @Value("${mcp.client.grant_type}")
    private String grantType;

    @Value("${mcp.client.client_secret}")
    private String clientSecret;

    @Value("${mcp.client.client_id}")
    private String clientId;

    @Autowired
    APIServices apiServices;
 
    @Autowired
    private SessionAuthRegistry sessionAuthRegistry;

    @GetMapping("/callback")
    public String handleLoginCallback( @RequestParam MultiValueMap<String, String> queryParams,
                                                      HttpServletRequest request,HttpServletResponse response,
                                                       Model model) {

        String code = queryParams.getFirst("code");
        String userCode = queryParams.getFirst("userCode");
        String sessionId = queryParams.getFirst("state") != null ? queryParams.getFirst("state") : "1";
        
        log.info("Received accessCode:{} ,received usercode {} received sessionId {}" ,code,userCode,sessionId);
        log.info("Query String: " + request.getQueryString());
        String token = apiServices.getAccessToken(tokenUrl,code,userCode,grantType, clientId,clientSecret);
        log.info("Received token: " + token);
        try {
            log.info("Generating public key");
            PublicKey publicKey = HelperFunctions.getPublicKeyFromResource("public-key.pem");
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            log.info("---- JWT Token Claims ----");
                for (String key : claims.keySet()) {
                    log.info(key + " : " + claims.get(key));
                }
            log.info("--------------------------");
            String tradecode = claims.getSubject(); // "sub"
            String email = (String) claims.get("email");
            Long tokenExpiry = claims.getExpiration().getTime(); 

            UserDetailsContext userDetails = new UserDetailsContext();
            userDetails.setUsername(tradecode);
            userDetails.setTradeCode(tradecode);
            userDetails.setEmail(email);
            userDetails.setTokenExpiry(tokenExpiry);
            String username = claims.getSubject();  
            log.info("Parsed username from JWT: " + username);
           
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null);
            sessionAuthRegistry.markAuthenticated(sessionId, authentication);
            model.addAttribute("username", username);
            
            return "authentication/login-success";
        } catch (JwtException e) {
            return "authentication/login-error";
        } catch (Exception e) {
            return "authentication/login-error";
        }
    }


    @PostMapping("/authorization")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleAuthorization(
      @RequestBody AuthRequest request) {

    log.info("handleAuthorization endpoint called with: tradecode={}, email={}, tokenExpiry={}, sessionId={}",
        request.getTradecode(), request.getEmail(), request.getTokenExpiry(), request.getSessionId());
        Number rawExpiry = (Number) request.getTokenExpiry();
        Long tokenExpiry = rawExpiry.longValue();

    try {
        UserDetailsContext userDetails = new UserDetailsContext();
        userDetails.setUsername(request.getTradecode());
        userDetails.setTradeCode(request.getTradecode());
        userDetails.setEmail(request.getEmail());
        userDetails.setTokenExpiry(tokenExpiry);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null);
        sessionAuthRegistry.markAuthenticated(request.getSessionId(), authentication);


    log.info("User authenticated successfully with tradecode: " + request.getTradecode()+ "Email  "+request.getEmail()+ "Token expiry  "+ request.getTokenExpiry());

        Map<String, String> response = new HashMap<>();
        response.put("message", "User authenticated successfully");
        return ResponseEntity.ok(response);

    } catch (JwtException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Invalid JWT token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    } catch (Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Login failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

    @GetMapping("/sample")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sampleGet() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Sample GET endpoint is working");
        return ResponseEntity.ok(response);
    }

    }


