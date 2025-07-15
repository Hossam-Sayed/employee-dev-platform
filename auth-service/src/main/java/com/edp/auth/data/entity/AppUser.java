package com.edp.auth.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDate birthdate;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String department;

    private String position;

    private boolean admin;

    // Self-referencing relationship (manager)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reports_to")
    private AppUser reportsTo;
}
