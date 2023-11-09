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
    private String note;
    private Long approvedBy;
    private List<CustomerRequestReceiptDetailForm> details;
}
