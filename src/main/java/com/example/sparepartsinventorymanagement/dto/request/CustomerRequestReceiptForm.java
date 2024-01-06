package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestReceiptForm {
    @NotNull(message = "Mã khách hàng không được để trống")
    private Long customerId;
    @NotNull(message = "Mã kho hàng không được để trống")
    private Long warehouseId;
    @NotNull(message = "Nhân viên quản lý kho hàng không được để trống")
    private Long inventoryStaff;
    private String note;
    @Valid
    @NotNull(message = "details cannot be null")
    private List<CustomerRequestReceiptDetailForm> details;
}
