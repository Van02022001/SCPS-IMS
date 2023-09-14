package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", indexes = {
        @Index(name = "uq_id", columnList = "user_id"),
        @Index(name = "uq_mobile", columnList = "mobile"),
        @Index(name = "uq_email", columnList = "email"),
        @Index(name = "uq_username", columnList = "username")

})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "mobile", length = 15, unique = true)
    private String mobile;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 32, nullable = false)
    private String password;

    @Column(name = "registered_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(name = "intro", columnDefinition = "TINYTEXT")
    private String intro;

    @Column(name = "profile", columnDefinition = "TEXT")
    private String profile;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Getters and setters (omitted for brevity)
}
