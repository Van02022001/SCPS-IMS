package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferUpdateRequest {
    @NotNull(message = "ID của lệnh chuyển không được để trống")
    @NotEmpty(message = "Required field.")

    private Long transferId;
    @NotNull(message = "ID của kho đích không được để trống")
    @NotEmpty(message = "Required field.")

    private Long destinationWarehouseId; // ID của kho đích
    private List<ItemTransferDetail> items;
}
