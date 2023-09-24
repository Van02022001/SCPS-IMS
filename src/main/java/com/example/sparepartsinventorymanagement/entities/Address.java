package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    private Long id;



    @Column(name = "line", length = 50)
    private String line;


    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "country", length = 50)
    private String country;

    @OneToOne(mappedBy = "address")
    private User user;


    @OneToOne(mappedBy = "address")
    private Warehouse warehouse;

}