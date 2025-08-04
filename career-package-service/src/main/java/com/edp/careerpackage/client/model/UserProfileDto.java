package com.edp.careerpackage.client.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDate birthdate;
    private String phoneNumber;
    private String department;
    private String position;
    private boolean admin;
    private Long reportsToId;
}
