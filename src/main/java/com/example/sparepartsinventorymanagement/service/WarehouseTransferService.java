package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.TransferRequest;
import com.example.sparepartsinventorymanagement.dto.request.TransferUpdateRequest;
import com.example.sparepartsinventorymanagement.dto.response.TransferResult;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseTransferDTO;

import java.util.List;

public interface WarehouseTransferService {
    TransferResult transferMultipleItems(TransferRequest request);

//TransferResult updateTransferItems(TransferRequest updatedRequest);
    List<WarehouseTransferDTO> getAllWarehouseTransfers();
    WarehouseTransferDTO getWarehouseTransferById(Long transferId);
}
