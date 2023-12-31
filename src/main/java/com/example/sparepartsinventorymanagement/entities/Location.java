package com.example.sparepartsinventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

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

    @Column(name = "item_quantity")
    private int item_quantity;

    @ManyToOne
    @JoinColumn(name = "warehouse_id") // Foreign key column pointing to Warehouse
    private Warehouse warehouse;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "location_location_tag", // tên bảng liên kết
            joinColumns = @JoinColumn(name = "location_id"), // khóa ngoại cho Location
            inverseJoinColumns = @JoinColumn(name = "tag_id") // khóa ngoại cho LocationTag
    )
    private List<LocationTag> tags;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @OneToMany(mappedBy = "fromLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemMovement> fromMovements ;
    @OneToMany(mappedBy = "toLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemMovement> toMovements;
}