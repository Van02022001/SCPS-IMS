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
@Table(name = "unit_measurements")
public class UnitMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="unit_measurements_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // Ví dụ: "m", "cm", "mm"

    @OneToMany(mappedBy = "unitMeasurement")
    private List<Size> sizes;

}
