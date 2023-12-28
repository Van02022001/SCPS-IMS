package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy
            = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "phone", length = 15, unique = true)
    private String phone;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "registered_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date registeredAt;

    @Column(name = "last_login")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date lastLogin;

    @Column(name = "intro", columnDefinition = "TINYTEXT")
    private String intro;

    @Column(name = "profile", columnDefinition = "TEXT")
    private String profile;


    @Column(name = "image", length = 2048)
    private String image;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    @OneToMany(mappedBy = "createdBy")
    private List<Item> createdItems;

    @OneToMany(mappedBy = "updatedBy")
    private List<Item> updatedItems;

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private Set<Receipt> createdReceipts;

    @JsonIgnore
    @OneToMany(mappedBy = "lastModifiedBy")
    private Set<Receipt> updatedReceipts;


    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "changedBy")
    @JsonIgnore
    private Set<PurchasePriceAudit> purchasePrices;

    @OneToMany(mappedBy = "changedBy")
    @JsonIgnore
    private Set<PricingAudit> pricingAudits;

    // Getters and setters (omitted for brevity)
}
