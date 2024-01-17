package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ReportRequest;
import com.example.sparepartsinventorymanagement.dto.request.SubCategoryFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.ReceiptReportResponse;
import com.example.sparepartsinventorymanagement.dto.response.ReportResponse;
import com.example.sparepartsinventorymanagement.dto.response.SubCategoryDTO;
import com.example.sparepartsinventorymanagement.service.ReportService;
import com.example.sparepartsinventorymanagement.service.impl.ReportServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "report")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
public class ReportController {
    private final ReportServiceImpl reportService;

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "For get report")
    @GetMapping(value = "/receipts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReceiptReport(
            @Valid @RequestBody ReportRequest form
    ) {
        ReceiptReportResponse res = reportService.getReceiptReport(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "Lấy báo cáo không thành công.",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Lấy báo cáo thành công.",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "For get report")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReport(
    ) {
        ReportResponse res = reportService.getReport();
        if(res == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "Lấy báo cáo không thành công.",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Lấy báo cáo thành công.",
                res
        ));
    }
}
