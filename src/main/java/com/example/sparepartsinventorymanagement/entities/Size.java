package com.example.sparepartsinventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "sizes")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="size_id")
    private Long id;

    @Column(name = "length")
    private float length;

    @Column(name = "width")
    private float width;

    @Column(name = "height")
    private float height;

    @Column(name = "diameter")
    private float diameter;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subcategory_id")
    private SubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "unit_measurements_id")
    private UnitMeasurement unitMeasurement;
}
