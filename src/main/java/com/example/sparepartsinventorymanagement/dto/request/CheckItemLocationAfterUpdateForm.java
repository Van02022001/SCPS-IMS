package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckItemLocationAfterUpdateForm {
    @NotNull(message = "Receipt ID is required")
    @Min(value = 1, message = "Receipt ID cannot be less than 1")
    private Long receipt_id;
}
