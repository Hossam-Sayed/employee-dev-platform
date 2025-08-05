//package com.edp.auth.security.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//
//import static com.edp.shared.error.util.JsonErrorUtil.toJsonError;
//
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return request.getRequestURI().equals("/api/auth/register")
//                || request.getRequestURI().equals("/api/auth/login")
//                || request.getRequestURI().equals("/api/auth/refresh")
//                || request.getRequestURI().contains("/swagger-ui/")
//                || request.getRequestURI().contains("/v3/api-docs")
//                || request.getRequestURI().contains("/swagger-ui.html");
//    }
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String username;
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write(toJsonError(
//                    HttpServletResponse.SC_UNAUTHORIZED,
//                    "Unauthorized",
//                    "Missing or invalid authorization token",
//                    request.getRequestURI(),
//                    LocalDateTime.now()
//            ));
//            return;
//        }
//
//        jwt = authHeader.substring(7);
//
//        try {
//            username = jwtService.extractUsername(jwt);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//                    authToken.setDetails(
//                            new WebAuthenticationDetailsSource().buildDetails(request)
//                    );
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//            filterChain.doFilter(request, response);
//
//        } catch (com.edp.auth.exception.JwtValidationException ex) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write(toJsonError(
//                    HttpServletResponse.SC_UNAUTHORIZED,
//                    "Unauthorized",
//                    ex.getMessage(),
//                    request.getRequestURI(),
//                    LocalDateTime.now()
//            ));
//        }
//    }
//}

//TODO: Refactor according to service needs
