package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "unit_measurements_id")
    private UnitMeasurement unitMeasurement;
}
