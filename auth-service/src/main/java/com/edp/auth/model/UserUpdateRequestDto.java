package com.edp.auth.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    private String username;

    @Email(message = "Email should be a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long if provided")
    // TODO: Add @Pattern for additional complexity
    private String password;

    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is not valid")
    private String phoneNumber;

    @Size(max = 50, message = "Department cannot exceed 50 characters")
    private String department;

    @Size(max = 50, message = "Position cannot exceed 50 characters")
    private String position;

    private boolean admin;

    @PositiveOrZero(message = "reportsToId must be a positive number or zero")
    private Long reportsToId;
}