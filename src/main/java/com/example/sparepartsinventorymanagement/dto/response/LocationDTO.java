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

    private List<String> tags;

    private WarehouseDTO warehouse;
}
