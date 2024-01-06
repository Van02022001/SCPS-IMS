package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @NotNull(message = "ID của kho đích không được để trống")
    private Long destinationWarehouseId; // ID của kho đích
    @Valid
    @NotNull(message = "items của kho đích không được để trống")
    @NotEmpty(message = "Required field.")

    private List<ItemTransferDetail> items;
}
