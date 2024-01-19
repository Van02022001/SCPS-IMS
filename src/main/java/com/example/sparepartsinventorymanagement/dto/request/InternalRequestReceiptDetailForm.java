package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalRequestReceiptDetailForm {
    @NotNull(message = "Item ID cannot be null")
    @NotEmpty(message = "Required field.")
    private Long itemId;
    @Min(value = 0, message = "Quantity must be non-negative")
    @NotNull(message = "quantity cannot be null")
    @NotEmpty(message = "Required field.")
    private int quantity;


    @Size(max = 500, message = "Description length must be less than or equal to 500 characters")
    private String description;
}
