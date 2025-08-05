package com.edp.filemanagement.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import com.edp.filemanagement.exception.JwtValidationException;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, String claimName, Class<T> type) {
        final Claims claims = extractAllClaims(token);
        T val = claims.get(claimName, type);
        return val;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, "userId", Long.class);
    }

    public Boolean extractIsAdmin(String token) {
        Boolean val = extractClaim(token, "isAdmin", Boolean.class);
        return val != null ? val : Boolean.FALSE;
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtValidationException ex) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new JwtValidationException("JWT token is expired", e);
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new JwtValidationException("Invalid JWT signature", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new JwtValidationException("Malformed JWT token", e);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            throw new JwtValidationException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException("JWT token is invalid or empty", e);
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
