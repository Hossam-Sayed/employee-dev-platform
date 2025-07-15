package com.edp.auth.controller;

import com.edp.auth.model.AuthRequestDto;
import com.edp.auth.model.AuthResponseDto;
import com.edp.auth.model.UserDto;
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
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @RequestBody UserDto userDto,
            UriComponentsBuilder uriBuilder
    ) {
        UserDto createdUser = userService.createUser(userDto);

        UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(createdUser.getId()).toUri();

        return ResponseEntity.created(location).body(AuthResponseDto.builder().token(jwtToken).build());
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
        String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(AuthResponseDto.builder().token(jwtToken).build());
    }
}