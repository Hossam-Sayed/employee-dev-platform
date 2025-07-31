package com.edp.careerpackage.security.jwt;

import java.util.Collection;
import java.util.List;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@AllArgsConstructor
@Builder
public class JwtUserPrincipal {

    private final Long userId;
    private final String username;
    private final boolean admin;

    public Collection<? extends GrantedAuthority> authorities() {
        if (admin) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
