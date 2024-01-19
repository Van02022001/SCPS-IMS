package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffReportResponse {
    private int totalStaff;
    private int totalManager;
    private int totalInventoryStaff;
    private int totalSaleStaff;
}
