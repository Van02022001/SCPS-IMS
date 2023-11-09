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
    @Min(value = 0, message = "Minimum stock level cannot be less than 0")
    @NotNull(message = "Minimum stock level is required")
    private int minStockLevel;

    @Min(value = 0, message = "Maximum stock level cannot be less than 0")
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

}
