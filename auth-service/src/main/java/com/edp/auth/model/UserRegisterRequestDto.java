package com.edp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequestDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private LocalDate birthdate;
    private String phoneNumber;
    private String department;
    private String position;
    private boolean admin;
    private Long reportsToId;
}