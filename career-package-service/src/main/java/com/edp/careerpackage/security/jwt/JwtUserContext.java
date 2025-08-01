package com.edp.careerpackage.security.jwt;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class JwtUserContext {

    public JwtUserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            throw new IllegalStateException("No valid JWT user authenticated");
        }
        return (JwtUserPrincipal) authentication.getPrincipal();
    }

    public Long getUserId() {
        return getPrincipal().getUserId();
    }

    public String getUsername() {
        return getPrincipal().getUsername();
    }

    public boolean isAdmin() {
        return getPrincipal().isAdmin();
    }
}
