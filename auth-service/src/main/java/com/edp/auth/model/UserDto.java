package com.edp.auth.model;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
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
