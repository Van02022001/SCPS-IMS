package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.TransferRequest;
import com.example.sparepartsinventorymanagement.dto.request.TransferUpdateRequest;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseTransferDTO;

import java.util.List;

public interface WarehouseTransferService {
    void transferMultipleItems(List<TransferRequest> transferRequests);

    void updateTransfer(List<TransferUpdateRequest> updateRequest);
    List<WarehouseTransferDTO> getAllWarehouseTransfers();
    WarehouseTransferDTO getWarehouseTransferById(Long transferId);
}
