package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestReceiptForm {
    private Long customerId;
    private Long warehouseId;
    private Long inventoryStaff;
    private String note;
    private List<CustomerRequestReceiptDetailForm> details;
}
