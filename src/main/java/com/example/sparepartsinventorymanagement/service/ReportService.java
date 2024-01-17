package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.ReportRequest;
import com.example.sparepartsinventorymanagement.dto.response.ReceiptReportResponse;
import com.example.sparepartsinventorymanagement.dto.response.ReportResponse;

public interface ReportService {
    ReportResponse getReport();
    ReceiptReportResponse getReceiptReport(ReportRequest request);
}
