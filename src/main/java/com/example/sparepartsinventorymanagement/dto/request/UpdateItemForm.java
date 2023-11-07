package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemForm {
    @Positive(message = "Purchase Price must be a positive value")
    private double purchasePrice;

    @Positive(message = "Pricing price must be a positive value")
    private double pricingPrice;

    @Min(value = 0, message = "Minimum stock level cannot be less than 0")
    @NotNull(message = "Minimum stock level is required")
    private int minStockLevel;

    @Min(value = 0, message = "Maximum stock level cannot be less than 0")
    @NotNull(message = "Maximum stock level is required")
    private int maxStockLevel;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotNull(message = "Product ID is required")
    private Long product_id;

    @NotNull(message = "Brand ID is required")
    private Long brand_id;

    @NotNull(message = "Supplier ID is required")
    private Long supplier_id;

    @NotNull(message = "Origin ID is required")
    private Long origin_id;
}
