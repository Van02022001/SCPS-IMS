package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.LocationTagRequest;
import com.example.sparepartsinventorymanagement.dto.response.LocationTagDTO;
import com.example.sparepartsinventorymanagement.service.impl.LocationTagServiceImpl;
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

@Tag(name = "location-tag")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/location-tags")
public class LocationTagController {
    private final LocationTagServiceImpl locationTagService;
    @Operation(summary = "For get list of location tags")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        List<LocationTagDTO> res = locationTagService.getAll();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list origin successfully",
                res
        ));
    }
    @Operation(summary = "For get location tag by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(
            @Parameter(description = "Enter id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        LocationTagDTO res = locationTagService.getLocationTagById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get location tag successfully",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "For create location tag")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createLocationTag(
            @Valid @RequestBody LocationTagRequest form
    ) {
        LocationTagDTO res = locationTagService.createLocationTag(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create location tag failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create location tag successfully",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "For update location tag")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateLocationTag(
            @Parameter(description = "Enter id to update", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody LocationTagRequest form
    ) {
        LocationTagDTO res = locationTagService.updateLocationTag(id, form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update location tag successfully",
                res
        ));
    }

}
