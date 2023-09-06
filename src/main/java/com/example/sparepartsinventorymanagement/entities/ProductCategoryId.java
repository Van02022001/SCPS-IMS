package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductCategoryId implements Serializable {

    @Column(name = "productId")
    private Long productId;

    @Column(name = "categoryId")
    private Long categoryId;

    // Constructors, getters, setters, and equals, hashCode methods (omitted for brevity)
}