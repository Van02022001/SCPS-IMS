package com.example.sparepartsinventorymanagement.dto.request;

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
    private List<InventoryCheckDetail> details;
}
