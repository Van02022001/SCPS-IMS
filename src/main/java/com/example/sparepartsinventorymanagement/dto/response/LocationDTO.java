package com.example.sparepartsinventorymanagement.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private Long id;

    private String shelfNumber;

    private String binNumber;

    private int item_quantity;

    private WarehouseDTO warehouse;

    private List<LocationTagDTO> tags;
}
