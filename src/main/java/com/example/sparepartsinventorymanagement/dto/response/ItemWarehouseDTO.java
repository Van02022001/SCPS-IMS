package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWarehouseDTO {
    private Long id;
    private String code;
    private String subcategoryName;
    private String brandName;
    private String supplierName;
    private String originName;
    private String imageUrl;
    private int availableQuantity;

}
