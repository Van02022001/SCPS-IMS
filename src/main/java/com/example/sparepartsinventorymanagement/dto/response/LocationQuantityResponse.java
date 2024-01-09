package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationQuantityResponse {
    private Long locationId;
    private String shelfNumber;
    private String binNumber;
    private int quantity;
}
