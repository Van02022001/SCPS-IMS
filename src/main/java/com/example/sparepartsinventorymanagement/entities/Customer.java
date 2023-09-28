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
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;


    @Column(name = "code", nullable =false)
    private String code;

    @Column(name = "name", nullable =false)
    private String name;

    @Column(name = "phone", nullable =false)
    private int phone;



    @Column(name="email", nullable =false)
    private String email;

    @Column(name = "tax_code", nullable =false)
    private String taxCode;


    @Column(name = "address", nullable =false)
    private String address;

    @Column(name="customer_type", nullable =false)
    @Enumerated(EnumType.STRING)
    private CustomerType type;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "customer")
    private List<Receipt> receipts;
}
