package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePurchasePriceRequest {
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    @NotNull(message = "Effective date cannot be null")
    @FutureOrPresent(message = "Effective date must be in the present or future")
    private Date effectiveDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @NotNull(message = "price cannot be null")
    private double price;
}
