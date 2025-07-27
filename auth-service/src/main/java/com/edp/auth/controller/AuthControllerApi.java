package com.edp.auth.controller;

import com.edp.auth.model.*;
import com.edp.shared.error.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Authentication", description = "Endpoints for registration, login, logout, and token refresh")
@Validated
@RequestMapping("/api/auth")
public interface AuthControllerApi {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user and returns tokens upon success.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Registration data",
                    content = @Content(schema = @Schema(implementation = UserRegisterRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully and tokens returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples =
                            @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"username must not be blank\",\"path\":\"/api/auth/register\"}")
                    )),
            @ApiResponse(responseCode = "409", description = "Username already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    ResponseEntity<AuthResponseDto> register(
            @Valid  @org.springframework.web.bind.annotation.RequestBody UserRegisterRequestDto userRegisterRequestDto,
            UriComponentsBuilder uriBuilder
    );

    @Operation(
            summary = "Login",
            description = "Authenticates a user and returns tokens.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Login credentials",
                    content = @Content(schema = @Schema(implementation = AuthRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponseDto> authenticate(@Valid  @org.springframework.web.bind.annotation.RequestBody AuthRequestDto request);

    @Operation(
            summary = "Refresh tokens",
            description = "Takes a valid refresh token and returns a new access token and refresh token.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Refresh token",
                    content = @Content(schema = @Schema(implementation = RefreshRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    ResponseEntity<AuthResponseDto> refreshToken(@Valid  @org.springframework.web.bind.annotation.RequestBody RefreshRequestDto request);

    @Operation(
            summary = "Logout",
            description = "Delete the user's refresh token.",
            requestBody = @RequestBody(
                    required = true,
                    description = "username",
                    content = @Content(schema = @Schema(implementation = LogoutRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid logout request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Valid  @org.springframework.web.bind.annotation.RequestBody LogoutRequestDto request);
}
