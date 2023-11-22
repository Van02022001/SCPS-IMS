package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportRequestReceiptForm {
    private Long warehouseId;
    private Long inventoryStaffId;
    private String description;
    private List<ImportRequestReceiptDetailForm>  details;
}
