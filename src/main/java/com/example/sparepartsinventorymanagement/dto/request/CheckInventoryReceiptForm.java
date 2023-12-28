package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInventoryReceiptForm {

    private Long managerId;
    private String description;
    private List<InventoryCheckDetail> details;
}
