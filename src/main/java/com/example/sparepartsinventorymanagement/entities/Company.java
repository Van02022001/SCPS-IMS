package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;


    @Column(name = "code", nullable =false)
    private String code;

    @Column(name = "name", nullable =false)
    private String name;

    @Column(name = "phone", nullable =false)
    private String phone;


    @Column(name="email", nullable =false)
    private String email;

    @Column(name = "tax_code", nullable =false)
    private String taxCode;


    @Column(name = "address", nullable =false)
    private String address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> users;



}
