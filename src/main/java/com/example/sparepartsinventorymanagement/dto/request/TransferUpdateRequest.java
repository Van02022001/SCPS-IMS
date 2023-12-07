package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferUpdateRequest {
    private Long transferId;
    private Long newSourceWarehouseId;
    private Long newDestinationWarehouseId;
    private int newQuantity;
}
