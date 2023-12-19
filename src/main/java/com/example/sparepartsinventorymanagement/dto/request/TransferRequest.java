package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    private Long destinationWarehouseId; // ID của kho đích
    private List<ItemTransferDetail> items;
}