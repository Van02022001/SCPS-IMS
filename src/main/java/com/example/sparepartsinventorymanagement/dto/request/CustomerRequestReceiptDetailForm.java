package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestReceiptDetailForm {
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;
    @Min(value = 0, message = "Quantity must be non-negative")
    private int quantity;





}
