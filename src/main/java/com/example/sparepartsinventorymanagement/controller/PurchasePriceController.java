package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetListPurchasePriceDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.PurchasePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "purchase-price")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/purchase-price")
public class PurchasePriceController {
    private final PurchasePriceService purchasePriceService;

    @Operation(summary = "Create a new purchase price")
    @PostMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPurchasePrice(@RequestBody CreatePurchasePriceRequest request) {
        try {
            var purchasePrice = purchasePriceService.createPurchasePrice(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(purchasePrice);
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the purchase price");
        }
    }
    @Operation(summary = "Update an existing purchase price")
    @PutMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePurchasePrice(@RequestBody UpdatePurchasePriceRequest request) {
        try {
            var updatedPurchasePrice = purchasePriceService.updatePurchasePrice(request);
            return ResponseEntity.ok(updatedPurchasePrice);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the purchase price");
        }
    }


    @Operation(summary = "Get all purchase prices")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllPurchasePrices() {
        var allPurchasePrices = purchasePriceService.getAllPurchasePrice();
        return ResponseEntity.ok(allPurchasePrices);
    }




    @Operation(summary = "Get historical prices for a specific item")
    @GetMapping(value = "/historical/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getHistoricalPricesForItem(@PathVariable Long itemId) {
        try {
            List<GetListPurchasePriceDTO> historicalPrices = purchasePriceService.getHistoricalPricesForItem(itemId);
            return ResponseEntity.ok(historicalPrices);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving historical prices");
        }
    }
}
