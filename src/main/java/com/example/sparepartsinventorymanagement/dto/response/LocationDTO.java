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

    private WarehouseDTO warehouse;

    private List<LocationTagDTO> tags;
}
