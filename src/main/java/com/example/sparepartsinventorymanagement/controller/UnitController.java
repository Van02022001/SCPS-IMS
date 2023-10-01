package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.service.impl.UnitServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController {

    @Autowired
    private UnitServiceImpl unitService;

    @Operation(summary = "For get list of units")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return unitService.getAll();
    }

    @Operation(summary = "For search list units by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnitsByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        return unitService.findByName(keyword);
    }
}
