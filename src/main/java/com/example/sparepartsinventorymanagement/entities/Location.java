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
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="location_id")
    private Long id;

    @Column(name = "location", length = 50)
    private String location;




    @ManyToOne
    @JoinColumn(name = "warehouse_id") // Foreign key column pointing to Warehouse
    private Warehouse warehouse;
}