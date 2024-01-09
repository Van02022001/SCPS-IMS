package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Required field.")
    private Long customerId;
    @NotEmpty(message = "Required field.")
    @NotNull(message = "Mã kho hàng không được để trống")
    private Long warehouseId;
    @NotNull(message = "Nhân viên quản lý kho hàng không được để trống")
    @NotEmpty(message = "Required field.")
    private Long inventoryStaff;
    private String note;
    @Valid
    @NotNull(message = "details cannot be null")
    @NotEmpty(message = "Required field.")
    private List<CustomerRequestReceiptDetailForm> details;
}
