package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.response.UnitMeasurementDTO;
import com.example.sparepartsinventorymanagement.service.impl.UnitMeasurementServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units-measurement")
@Tag(name = "unit-measurement")
public class UnitMeasurementController {

    @Autowired
    private UnitMeasurementServiceImpl unitMeasurementService;

    @Operation(summary = "For get list of units of measurement")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        List<UnitMeasurementDTO> res = unitMeasurementService.getAll();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list unit measurement successfully",
                res
        ));
    }

}
