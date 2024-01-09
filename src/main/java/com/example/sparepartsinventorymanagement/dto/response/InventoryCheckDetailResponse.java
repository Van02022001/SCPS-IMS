package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckDetailResponse {
    private Long itemId;
    private String codeItem;
    private String itemName;
    private int expectedQuantity;
    private int actualQuantity;
    private int discrepancyQuantity;
    private double discrepancyValue;
    private String note;
    private List<LocationQuantityResponse> locations;
}
