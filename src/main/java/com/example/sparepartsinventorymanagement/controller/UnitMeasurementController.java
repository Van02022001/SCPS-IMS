package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.service.impl.UnitMeasurementServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/units-measurement")
public class UnitMeasurementController {

    @Autowired
    private UnitMeasurementServiceImpl unitMeasurementService;

    @Operation(summary = "For get list of units of measurement")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return unitMeasurementService.getAll();
    }

}
