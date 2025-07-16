package com.edp.auth.service;

import com.edp.auth.data.entity.AppUser;
import com.edp.auth.data.entity.RefreshToken;
import com.edp.auth.data.repository.RefreshTokenRepository;
import com.edp.auth.exception.InvalidRefreshTokenException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${application.security.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    public RefreshToken createRefreshToken(AppUser user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getValidRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(this::verifyExpiration)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }

    @Transactional
    public void deleteByUser(AppUser user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
