package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "sizes")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="size_id")
    private Long id;

    @Column(name = "length", nullable = false)
    private float length;

    @Column(name = "width", nullable = false)
    private float width;

    @Column(name = "height", nullable = false)
    private float height;

    @OneToOne(mappedBy = "sizes")
    private Product product;


    @ManyToOne
    @JoinColumn(name = "unit_measurements_id")
    private UnitMeasurement unitMeasurement;
}
