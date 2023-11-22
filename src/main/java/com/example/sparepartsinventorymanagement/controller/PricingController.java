package com.example.sparepartsinventorymanagement.controller;
import com.example.sparepartsinventorymanagement.dto.request.CreatePricingRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePricingRequest;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.PricingService;
import com.example.sparepartsinventorymanagement.dto.response.PricingDTOs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
@Tag(name = "pricing")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/pricing")
public class PricingController {
    private final PricingService pricingService;


    @Operation(summary = "Add new pricing")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPricing(@RequestBody @Valid CreatePricingRequest request) {

        try {
            PricingDTOs pricingDTOs = pricingService.addPricing(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(pricingDTOs);
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding pricing");
        }
    }

    @Operation(summary = "Update pricing")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePricing(@RequestBody  UpdatePricingRequest request) {
        try {
            PricingDTOs updatedPricingDTOs = pricingService.updatePricing(request);
            return ResponseEntity.ok(updatedPricingDTOs);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating pricing");
        }
    }

    @Operation(summary = "Delete pricing by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePricing(@PathVariable Long id) {
        try {
            pricingService.deletePricing(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting pricing");
        }
    }
}