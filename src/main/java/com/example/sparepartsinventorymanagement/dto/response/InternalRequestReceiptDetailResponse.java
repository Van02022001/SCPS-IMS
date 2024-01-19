package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalRequestReceiptDetailResponse {
    private Long id;
    private InfoItemDTO item;
    private int quantity;
    private String unitName;
    private double price;
    private double totalPrice;
}
