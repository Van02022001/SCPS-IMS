package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.UnitFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.UnitDTO;
import com.example.sparepartsinventorymanagement.service.impl.UnitServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@Tag(name = "unit")
public class UnitController {

    @Autowired
    private UnitServiceImpl unitService;

    @Operation(summary = "For get list of units")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        List<UnitDTO> res = unitService.getAll();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list unit successfully",
                res
        ));
    }
    @Operation(summary = "For unit by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnitById(
            @Parameter(description = "Enter unit to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        UnitDTO res = unitService.getUnitById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get unit successfully",
                res
        ));
    }
    @Operation(summary = "For search list units by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnitsByName(
            @Parameter(description = "enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        List<UnitDTO> res = unitService.findByName(keyword);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list unit by name successfully",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create unit")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creatUnit(
            @Valid @RequestBody UnitFormRequest form
    ) {
        UnitDTO res = unitService.createUnit(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create unit failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create unit successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update unit")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUnit(
            @Parameter(description = "Enter unit id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody UnitFormRequest form
    ) {
        UnitDTO res = unitService.updateUnit(id, form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update unit failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update unit successfully",
                res
        ));
    }

}
