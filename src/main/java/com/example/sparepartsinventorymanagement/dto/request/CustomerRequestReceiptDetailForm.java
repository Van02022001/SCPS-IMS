package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestReceiptDetailForm {
    private Long itemId;
    private int quantity;
    private Long unitId;
    private double price;


}
