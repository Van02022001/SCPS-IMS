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
        @Index(name = "idx_pc_category", columnList = "categoryId"),
        @Index(name = "idx_pc_product", columnList = "productId")
})
public class ProductCategory {
    @EmbeddedId
    private ProductCategoryId id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productId")
    private Product product;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "categoryId")
    private Category category;

    // Getters and setters (omitted for brevity)
}