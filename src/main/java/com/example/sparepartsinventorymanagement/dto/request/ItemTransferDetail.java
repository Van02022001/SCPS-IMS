package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemTransferDetail {
    @NotNull(message = "Mã sản phẩm không được để trống")
    private Long itemId;
    @Positive(message = "Số lượng phải lớn hơn 0")
    private int quantity;
}
