package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetListPurchasePriceDTO;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceRequestDTO;
import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.service.PurchasePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "purchase-price")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/purchase-price")
public class PurchasePriceController {
    private final PurchasePriceService purchasePriceService;
    private final ItemRepository itemRepository;



    @Operation(summary = "Get all purchase prices")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllPurchasePrices() {
        var allPurchasePrices = purchasePriceService.getAllPurchasePrice();
        return ResponseEntity.ok(allPurchasePrices);
    }

    @Operation(summary = "Create or update a purchase price")
    @PostMapping()
    public ResponseEntity<?> createOrUpdatePurchasePrice(@RequestBody PurchasePriceRequestDTO request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        purchasePriceService.createOrUpdatePurchasePrice(item, request.getUnitPrice());

        return ResponseEntity.ok().body("Purchase price updated successfully");
    }



}
