package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.Item;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckDetail {
    @NotNull(message = "Item ID cannot be null")
    @NotEmpty(message = "Required field.")
    private Long itemId;
    @NotNull(message = "actualQuantity cannot be null")
    @NotEmpty(message = "Required field.")
    @Min(value = 0, message = "Actual quantity must be non-negative")
    private int actualQuantity;
    @Size(max = 1000, message = "Note length must be less than or equal to 1000 characters")
    private String note;
    @Valid
    @NotNull(message = "locationQuantities cannot be null")
    @NotEmpty(message = "Required field.")
    private List<LocationQuantityDetail > locationQuantities;

}
