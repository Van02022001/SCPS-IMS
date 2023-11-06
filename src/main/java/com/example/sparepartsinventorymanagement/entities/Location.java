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
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="location_id")
    private Long id;



    @Column(name = "shelf_number")
    private String shelfNumber; // số kệ

    @Column(name = "bin_number")
    private String binNumber; // số ngăn:

    @ManyToOne
    @JoinColumn(name = "warehouse_id") // Foreign key column pointing to Warehouse
    private Warehouse warehouse;

    @OneToMany(mappedBy = "locations", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @ManyToMany
    @JoinTable(
            name = "location_tag",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<LocationTag> tags;
}