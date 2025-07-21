package com.edp.auth.controller;

import com.edp.auth.model.UserRegisterRequestDto;
import com.edp.auth.model.UserResponseDto;
import com.edp.auth.model.UserUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import com.edp.shared.error.model.ErrorResponse;

@Tag(name = "User Management", description = "APIs for managing user accounts")
@Validated
@RequestMapping("/api/users")
public interface UserControllerApi {

    @Operation(
            summary = "Create a new user (Admin only)",
            description = "Allows an ADMIN to create a new user account. Returns 201 Created on success.",
            tags = {"User Management"},
            requestBody = @RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Validation failed: username: must not be blank;\",\"path\":\"/api/users\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\",\"path\":\"/api/users\"}"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have sufficient permissions to access this resource.\",\"path\":\"/api/users\"}")))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> createUser(@Valid @org.springframework.web.bind.annotation.RequestBody UserRegisterRequestDto userRegisterRequestDto, UriComponentsBuilder uriBuilder);

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user's details by their ID. Accessible by ADMINs for any user, or by a USER for their own profile.",
            tags = {"User Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this profile",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have sufficient permissions to access this resource.\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found\",\"path\":\"/api/users/999\"}")))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == T(com.edp.auth.data.entity.AppUser).cast(authentication.principal).id")
    ResponseEntity<UserResponseDto> getUser(@Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id);

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users. Accessible by ADMINs and USERs.",
            tags = {"User Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\",\"path\":\"/api/users\"}"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have sufficient permissions to access this resource.\",\"path\":\"/api/users\"}")))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @Operation(
            summary = "Update user by ID",
            description = "Updates an existing user's details. Accessible by ADMINs for any user, or by a USER for their own profile. Password update is optional.",
            tags = {"User Management"},
            requestBody = @RequestBody(
                    description = "User update details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User updated successfully (No Content)"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid email format;\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this profile",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have sufficient permissions to access this resource.\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found\",\"path\":\"/api/users/999\"}"))),
//            TODO: Edit response error message according to exception error
//              @ApiResponse(responseCode = "409", description = "Data integrity violation (e.g., duplicate username/email)",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":409,\"error\":\"Conflict\",\"message\":\"could not execute statement; SQL [n/a]; constraint [app_users_username_key]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred.\",\"path\":\"/api/users/1\"}")))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == T(com.edp.auth.data.entity.AppUser).cast(authentication.principal).id")
    ResponseEntity<Void> updateUser(@Parameter(description = "ID of the user to update", required = true) @PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody UserUpdateRequestDto userUpdateRequestDto);

    @Operation(
            summary = "Delete user by ID (Admin only)",
            description = "Deletes a user account by their ID. Accessible only by ADMINs.",
            tags = {"User Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully (No Content)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have sufficient permissions to access this resource.\",\"path\":\"/api/users/1\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2025-07-21T10:00:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found\",\"path\":\"/api/users/999\"}")))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteUser(@Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id);
}