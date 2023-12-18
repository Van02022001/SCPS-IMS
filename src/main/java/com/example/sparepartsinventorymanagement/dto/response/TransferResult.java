package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.dto.request.ItemTransferDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResult {
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private List<ItemTransferDetail> transferredItems;
    // Có thể thêm trường thông báo hoặc trạng thái nếu cần
}
