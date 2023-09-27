package com.example.sparepartsinventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
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

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "supplier_item",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;


}
