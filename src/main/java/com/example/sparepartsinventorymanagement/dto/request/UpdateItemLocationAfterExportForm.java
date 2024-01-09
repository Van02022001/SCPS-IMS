package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemLocationAfterExportForm {
    @NotNull(message = "Receipt detail ID is required")
    @Min(value = 1, message = "Receipt detail ID cannot be less than 1")
    private Long receipt_detail_id;
    @Valid
    @NotNull(message = "locations của kho đích không được để trống")
    Set<UpdateItemLocationAfterExportRequest> locations;

}
