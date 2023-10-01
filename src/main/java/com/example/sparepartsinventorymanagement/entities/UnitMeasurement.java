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
@Table(name = "unit_measurements")
public class UnitMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="unit_measurements_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // Ví dụ: "m", "cm", "mm"

    @OneToMany(mappedBy = "unitMeasurement")
    @JsonIgnore
    private List<Size> sizes;

}
