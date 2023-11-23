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




    @Operation(summary = "Get all purchase prices")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllPurchasePrices() {
        var allPurchasePrices = purchasePriceService.getAllPurchasePrice();
        return ResponseEntity.ok(allPurchasePrices);
    }





}
