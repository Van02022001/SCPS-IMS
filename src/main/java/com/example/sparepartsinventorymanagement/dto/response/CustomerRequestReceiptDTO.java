package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestReceiptDTO {
    private Long id;
    private String status;

}
