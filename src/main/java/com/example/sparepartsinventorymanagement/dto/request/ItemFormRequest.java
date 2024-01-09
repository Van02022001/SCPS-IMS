package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemFormRequest {
    @Min(value = 5, message = "Minimum stock level cannot be less than 5")
    @NotNull(message = "Minimum stock level is required")
    private int minStockLevel;

    @Min(value = 10, message = "Maximum stock level cannot be less than 10")
    @NotNull(message = "Maximum stock level is required")
    private int maxStockLevel;

    @NotNull(message = "Product ID is required")
    private Long sub_category_id;

    @NotNull(message = "Brand ID is required")
    private Long brand_id;

    @NotNull(message = "Supplier ID is required")
    private Long supplier_id;

    @NotNull(message = "Origin ID is required")
    private Long origin_id;


    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private double price;

    @DecimalMin(value = "0.01", message = "Purchase price must be greater than zero")
    private double purchasePrice;
}
