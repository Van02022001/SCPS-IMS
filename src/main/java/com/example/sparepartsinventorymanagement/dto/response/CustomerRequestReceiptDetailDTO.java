package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestReceiptDetailDTO {
    private Long id; // ID of the detail record
    private Long itemId;
    private String itemName; // Name of the item for easy reference
    private int quantity; // The quantity requested
    private String unitName; // Name of the unit for display
    private double unitPrice;
    private double totalPrice;

}
