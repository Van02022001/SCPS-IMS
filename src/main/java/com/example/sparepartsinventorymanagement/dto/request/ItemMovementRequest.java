package com.example.sparepartsinventorymanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemMovementRequest {
    @Min(value = 1, message = "Quantity cannot be less than 1")
    @NotNull(message = "Quantity is required")
    private int quantity;

    @Schema(description = "notes name", example = "Chuyển trong kho")
    @Size(min = 1, max = 100)
    @NotBlank(message = "notes cannot be null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String notes;

    @Min(value = 1, message = "Item id cannot be less than 1")
    @NotNull(message = "Item id is required")
    private Long item_id;

    @Min(value = 1, message = "Location id cannot be less than 1")
    @NotNull(message = "Location id is required")
    private Long fromLocation_id;

    @Min(value = 1, message = "Location id cannot be less than 1")
    @NotNull(message = "Location id is required")
    private Long toLocation_id;

}
