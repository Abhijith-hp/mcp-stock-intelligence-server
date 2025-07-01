package com.gtl.MCPServer.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsContext implements UserDetails {

    private String username; 
    private String tradeCode;
    private String email;
    private Long tokenExpiry;
    private List<GrantedAuthority> authorities;

    public UserDetailsContext() {
        // Default constructor
    }
    public UserDetailsContext(String username) {
        this.username = username;
    }

    public UserDetailsContext(String username, String email, String pan, Long tokenExpiry,String tradeCode) {
        this.username = username;
        this.email = email;
        this.tokenExpiry = tokenExpiry;
        this.tradeCode = tradeCode;
    }

    // === Getters ===
    @Override public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getTradeCode() { return tradeCode; }
    public Long getTokenExpiry() { return tokenExpiry; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // === Setters ===
    public void setUsername(String username) { this.username = username; }
    public void setTradeCode(String tradeCode) { this.tradeCode = tradeCode; }
    public void setEmail(String email) { this.email = email; }
    public void setTokenExpiry(Long tokenExpiry) { this.tokenExpiry = tokenExpiry; }
    public void setAuthorities(List<GrantedAuthority> authorities) { this.authorities = authorities; }
}
