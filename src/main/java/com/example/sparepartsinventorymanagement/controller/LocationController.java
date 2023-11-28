package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.LocationFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.LocationTagRequest;
import com.example.sparepartsinventorymanagement.dto.response.LocationDTO;
import com.example.sparepartsinventorymanagement.dto.response.LocationTagDTO;
import com.example.sparepartsinventorymanagement.service.impl.LocationServiceImpl;
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

@Tag(name = "location")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/locations")
public class LocationController {
    private final LocationServiceImpl locationService;

    @Operation(summary = "For get list of location of this warehouse")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllByWarehouse() {
        List<LocationDTO> res = locationService.getLocationsByWarehouse();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list location successfully",
                res
        ));
    }
    @Operation(summary = "For get list of location by item id in this warehouse")
    @GetMapping(value = "/locations-by-item/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLocationsByItem(
            @Parameter(description = "Enter id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        List<LocationDTO> res = locationService.getLocationsByItemId(id);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list location successfully",
                res
        ));
    }
    @Operation(summary = "For get location by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(
            @Parameter(description = "Enter id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        LocationDTO res = locationService.getLocationById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get location  successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "For create location ")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createLocation(
            @Valid @RequestBody LocationFormRequest form
    ) {
        LocationDTO res = locationService.creatLocation(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create location failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create location successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "For update location")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateLocation(
            @Parameter(description = "Enter id to update", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody LocationFormRequest form
    ) {
        LocationDTO res = locationService.updateLocation(id, form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update location successfully",
                res
        ));
    }
}
