package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptReportResponse {
    private int numberImportItem;
    private double importedItemValue;
    private int numberExportItem;
    private double exportedItemValue;
}
