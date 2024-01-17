package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubcategoryReportResponse {
    private int totalSubcategory;
    private int totalItem;
    private int totalItemQuantity;
    private double totalInventoryValue;
    private int totalItemImportQuantity;
    private double totalItemImportValue;
    private int totalItemExportQuantity;
    private double totalItemExportValue;
    private int totalDefectiveItemQuantity;
    private int totalLostItemQuantity;
}
