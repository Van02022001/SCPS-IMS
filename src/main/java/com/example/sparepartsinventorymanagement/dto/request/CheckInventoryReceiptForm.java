package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInventoryReceiptForm {
    @Size(max = 500, message = "Description length must be less than or equal to 500 characters")
    private String description;
    @Valid
    @NotNull(message = "details cannot be null")
    @NotEmpty(message = "Required field.")
    private List<InventoryCheckDetail> details;
}
