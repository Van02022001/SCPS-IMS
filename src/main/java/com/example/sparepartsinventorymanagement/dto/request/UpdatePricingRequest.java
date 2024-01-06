package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePricingRequest {
    @NotNull(message = "Pricing ID cannot be null")
    @NotEmpty(message = "Required field.")
    private Long id;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    @NotEmpty(message = "Required field.")
    private Date startDate;

    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @NotEmpty(message = "Required field.")
    private double price;
}
