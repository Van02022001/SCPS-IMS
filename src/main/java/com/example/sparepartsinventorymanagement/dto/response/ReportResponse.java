package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    private StaffReportResponse staffReportResponse;
    private SubcategoryReportResponse subcategoryReportResponse;
}
