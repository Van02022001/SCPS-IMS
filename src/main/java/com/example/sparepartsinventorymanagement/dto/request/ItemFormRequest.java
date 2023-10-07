package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemFormRequest {

    @Positive(message = "Cost price must be a positive value")
    private double costPrice;

    @Positive(message = "Sale price must be a positive value")
    private double salePrice;

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

    @NotNull(message = "Warehouse ID is required")
    private Long warehouse_id;

    @NotEmpty(message = "At least one tag is required")
    @Size(min = 1, message = "At least one tag is required")
    private List<String> tags;
}
