package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateImportRequestReceiptDetail {
    private Long id;
    @NotNull(message = "Item ID cannot be null")
    @NotEmpty(message = "Required field.")
    private Long itemId;
    @Min(value = 0, message = "Quantity must be non-negative")
    @NotEmpty(message = "Required field.")
    private int quantity;

    @Min(value = 0, message = "Unit price must be non-negative")
    private double unitPrice;

    private Long unitId;
    private String description;
}
