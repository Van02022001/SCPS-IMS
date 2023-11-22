package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateImportRequestReceipt {
    private Long warehouseId;
    private Long inventoryStaffId;
    private String description;
    private List<UpdateImportRequestReceiptDetail> details;
}
