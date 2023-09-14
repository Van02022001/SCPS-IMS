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
@Table(name = "product_category", indexes = {
        @Index(name = "idx_pc_category", columnList = "category_id"),
        @Index(name = "idx_pc_product", columnList = "product_id")
})
public class ProductCategory {
    @EmbeddedId
    private ProductCategoryId id;

    @ManyToOne
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("category_id")
    @JoinColumn(name = "category_id")
    private Category category;

    // Getters and setters (omitted for brevity)
}