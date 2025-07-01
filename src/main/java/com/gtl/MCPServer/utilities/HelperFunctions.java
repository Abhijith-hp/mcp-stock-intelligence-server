package com.gtl.MCPServer.utilities;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;


@Component
@Log4j2
public class HelperFunctions {

    @Value("${mcp.oauth.auth_base_url}")
    private String AUTH_BASE_URL;

    @Value("${mcp.oauth.redirect_uri}")
    private String REDIRECT_URI;

    @Value("${mcp.client.client_id}")
    private String CLIENT_ID;

    @Value("${mcp.oauth.response_type}")
    private  String RESPONSE_TYPE;
    
   public static String formatToYYYYMMDD(String dateStr) {
    if (dateStr == null || dateStr.isEmpty()) return "";
    try {
        return java.time.LocalDate.parse(dateStr).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
    } catch (Exception e) {
        java.time.format.DateTimeFormatter[] formatters = new java.time.format.DateTimeFormatter[] {
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")
        };
        for (java.time.format.DateTimeFormatter fmt : formatters) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(dateStr, fmt);
                return date.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            } catch (Exception ignore) {}
        }
    }
    throw new IllegalArgumentException("Invalid date format: " + dateStr + ". Please use a valid date format.");
}



public static PublicKey getPublicKeyFromResource(String resourcePath) throws Exception {
    try (InputStream is = HelperFunctions.class.getClassLoader().getResourceAsStream(resourcePath)) {
        if (is == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        String key = new String(is.readAllBytes())
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}

 public String buildLoginUrl(String sessionId) {
        String encodedRedirectUri = java.net.URLEncoder.encode(REDIRECT_URI, java.nio.charset.StandardCharsets.UTF_8);
        return String.format("%s?state=%s&redirect_uri=%s&client_id=%s&response_type=%s",
                AUTH_BASE_URL, sessionId, encodedRedirectUri, CLIENT_ID, RESPONSE_TYPE);
    }




public HttpServletRequest getCurrentHttpRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
        return servletRequestAttributes.getRequest();
    }
    return null;
}

 public String generateUniqueSessionId() {
        long timestamp = System.currentTimeMillis();
        int randomSuffix = (int) (Math.random() * 100000);  
        return "SID-" + timestamp + "-" + randomSuffix;
    }


}
