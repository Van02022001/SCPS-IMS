package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportRequestReceiptDetailResponse {
    private Long id;
    private String itemName;
    private int quantity;
    private String unitName;
    private double price;
    private double totalPrice;


}
