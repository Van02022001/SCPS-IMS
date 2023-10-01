package com.example.sparepartsinventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(mappedBy = "size")
    @JsonIgnore
    private List<Product> products;

    @ManyToOne
    @JoinColumn(name = "unit_measurements_id")
    private UnitMeasurement unitMeasurement;
}
