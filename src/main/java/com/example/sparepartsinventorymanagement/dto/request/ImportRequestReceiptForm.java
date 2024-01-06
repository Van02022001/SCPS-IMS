package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportRequestReceiptForm {
    @NotNull(message = "Warehouse ID cannot be null")
    private Long warehouseId;
    @NotNull(message = "Inventory Staff ID cannot be null")
    private Long inventoryStaffId;
    @Size(max = 500, message = "Description length must be less than or equal to 500 characters")
    private String description;
    @Valid
    @NotNull(message = "details cannot be null")
    private List<ImportRequestReceiptDetailForm>  details;
}
