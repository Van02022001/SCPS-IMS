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
public class LocationQuantityDetail {
    @NotNull(message = "Location ID cannot be null")
    @NotEmpty(message = "Required field.")
    private Long locationId;
    @NotNull(message = "quantity cannot be null")
    @NotEmpty(message = "Required field.")
    @Min(value = 0, message = "Quantity must be non-negative")
    private int quantity;
}
