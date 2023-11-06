package com.example.sparepartsinventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "sub_category_meta")
public class SubCategoryMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_category_meta_id")
    private Long id;


    @Column(name = "meta_key", length = 50, nullable = false)
    private String key;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToOne(mappedBy = "subCategoryMeta")
    @JsonIgnore
    private SubCategory subCategory;

}