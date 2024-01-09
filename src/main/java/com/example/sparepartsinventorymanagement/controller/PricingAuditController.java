package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.response.PricingAuditDTO;
import com.example.sparepartsinventorymanagement.service.PricingAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "pricing-audit")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/pricing-audit")
public class PricingAuditController {
    private final PricingAuditService pricingAuditService;

    @Operation(summary = "Get pricing history for an item")
    @GetMapping(value = "/history/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPricingHistory(@PathVariable Long itemId) {
        try {
            List<PricingAuditDTO> pricingAudits = pricingAuditService.getPricingHistory(itemId);
            return ResponseEntity.ok(pricingAudits);
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving pricing history");
        }
    }
}