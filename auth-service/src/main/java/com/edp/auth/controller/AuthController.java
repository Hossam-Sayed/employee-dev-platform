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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @RequestBody UserRegisterRequestDto userRegisterRequestDto,
            UriComponentsBuilder uriBuilder
    ) {
        UserResponseDto createdUser = userService.createUser(userRegisterRequestDto);
        UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken((AppUser) userDetails);


        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(createdUser.getId()).toUri();

        return ResponseEntity.created(location).body(AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody AuthRequestDto request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken((AppUser) userDetails);

        return ResponseEntity.ok(AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDto> refreshToken(@RequestBody RefreshRequestDto request) {
        RefreshToken token = refreshTokenService.getValidRefreshToken(request.getRefreshToken());
        String newAccessToken = jwtService.generateToken(token.getUser());

        return ResponseEntity.ok(RefreshResponseDto.builder()
                .accessToken(newAccessToken)
                .build());
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        AppUser user = (AppUser) userDetailsService.loadUserByUsername(request.getUsername());
        refreshTokenService.deleteByUser(user);
        return ResponseEntity.noContent().build();
    }


}