package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    private Long itemId;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private int quantity;
}
