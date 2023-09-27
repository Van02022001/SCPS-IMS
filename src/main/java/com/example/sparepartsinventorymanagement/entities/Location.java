package com.example.sparepartsinventorymanagement.entities;

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
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="location_id")
    private Long id;

    @Column(name = "location", length = 50)
    private String location;


    @ManyToOne
    @JoinColumn(name = "item_id") // Tên trường khóa ngoại trong bảng Location
    private Item item; // Mối quan hệ với bảng Item


}