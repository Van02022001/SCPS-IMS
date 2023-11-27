package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.Location;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemLocationRequest {
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity cannot be less than 1")
    private int quantity;

    @NotNull(message = "Location ID is required")
    @Min(value = 1, message = "Quantity cannot be less than 1")
    private Long toLocation_id;
}
