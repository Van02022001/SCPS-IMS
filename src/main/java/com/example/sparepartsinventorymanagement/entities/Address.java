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
@Table(name = "address", indexes = {

        @Index(name = "idx_address_order", columnList = "orderId")
})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "warehouseId", unique = true) // Thêm trường warehouseId để ánh xạ quan hệ 1-1
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @Column(name = "line", length = 50)
    private String line;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "country", length = 50)
    private String country;




    // Getters and setters (omitted for brevity)
}