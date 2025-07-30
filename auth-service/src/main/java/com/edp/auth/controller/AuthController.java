package com.edp.auth.controller;

import com.edp.auth.data.entity.AppUser;
import com.edp.auth.data.entity.RefreshToken;
import com.edp.auth.model.*;
import com.edp.auth.service.RefreshTokenService;
import com.edp.auth.service.UserService;
import com.edp.auth.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public ResponseEntity<AuthResponseDto> register(
            @RequestBody UserRegisterRequestDto userRegisterRequestDto,
            UriComponentsBuilder uriBuilder
    ) {
        UserResponseDto createdUser = userService.createUser(userRegisterRequestDto);
        UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", createdUser.getId());
        claims.put("isAdmin", createdUser.isAdmin());
        String accessToken = jwtService.generateToken(claims, userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken((AppUser) userDetails);


        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(createdUser.getId()).toUri();

        return ResponseEntity.created(location).body(AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build());
    }

    @Override
    public ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody AuthRequestDto request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(request.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", appUser.getId());
        claims.put("isAdmin", appUser.isAdmin());

        String accessToken = jwtService.generateToken(claims, appUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUser);


        return ResponseEntity.ok(AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build());
    }

    @Override
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody RefreshRequestDto request) {
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(request.getRefreshToken());


        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", newRefreshToken.getUser().getId());
        claims.put("isAdmin", newRefreshToken.getUser().isAdmin());

        String newAccessToken = jwtService.generateToken(claims, newRefreshToken.getUser());

        return ResponseEntity.ok(AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build());
    }

    @Override
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        AppUser user = (AppUser) userDetailsService.loadUserByUsername(request.getUsername());
        refreshTokenService.deleteByUser(user);
        return ResponseEntity.noContent().build();
    }


}