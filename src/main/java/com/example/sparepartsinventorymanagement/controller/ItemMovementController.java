package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ItemMovementRequest;
import com.example.sparepartsinventorymanagement.dto.request.LocationFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.ItemMovementDTO;
import com.example.sparepartsinventorymanagement.dto.response.LocationDTO;
import com.example.sparepartsinventorymanagement.entities.ItemMovement;
import com.example.sparepartsinventorymanagement.service.impl.ItemMovementServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "item-movement")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/item-movements")
public class ItemMovementController {
    private final ItemMovementServiceImpl itemMovementService;

    @Operation(summary = "For get list of item movement by item")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllByItem(
            @Parameter(description = "Enter id to get", example = "1", required = true)
            @NotBlank @NotEmpty Long id
    ) {
        List<ItemMovementDTO> res = itemMovementService.getByItem(id);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list item movement by item successfully",
                res
        ));
    }
    @Operation(summary = "For get item movement by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(
            @Parameter(description = "Enter id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        ItemMovementDTO res = itemMovementService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get item movement successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "For create item movement ")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createItemMovement(
            @Valid @RequestBody ItemMovementRequest form
    ) {
        ItemMovementDTO res = itemMovementService.createItemMovementInWarehouse(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create item movement failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create item movement successfully",
                res
        ));
    }
}
