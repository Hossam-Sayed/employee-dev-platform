package com.edp.shared.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
public class JwtUserContext {

    public static String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        return request.getHeader("Authorization");
    }

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
