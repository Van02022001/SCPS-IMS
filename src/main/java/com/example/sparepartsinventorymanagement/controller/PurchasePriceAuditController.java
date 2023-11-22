package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.AuditSearchCriteriaForm;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.PurchasePriceAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "purchase-price-audit")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/purchase-price-audits")
public class PurchasePriceAuditController {
    private final PurchasePriceAuditService purchasePriceAuditService;

    @Operation(summary = "Get all purchase price audits")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllAudits() {
        try {
            List<PurchasePriceAuditDTO> audits = purchasePriceAuditService.getAllAudits();
            return ResponseEntity.ok(audits);
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving audits");
        }
    }

    @Operation(summary = "Get a purchase price audit by ID")
    @GetMapping(value = "/{auditId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAuditById(@PathVariable Long auditId) {
        try {
            PurchasePriceAuditDTO audit = purchasePriceAuditService.getAuditById(auditId);
            return ResponseEntity.ok(audit);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving the audit");
        }
    }

    @Operation(summary = "Search purchase price audits")
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchAudits(@RequestBody AuditSearchCriteriaForm criteria) {
        try {
            List<PurchasePriceAuditDTO> audits = purchasePriceAuditService.searchAudits(criteria);
            return ResponseEntity.ok(audits);
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during the audit search");
        }
    }
}
