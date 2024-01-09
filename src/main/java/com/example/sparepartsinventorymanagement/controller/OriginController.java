package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.OriginFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.OriginDTO;
import com.example.sparepartsinventorymanagement.service.impl.OriginServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/origins")
@Tag(name = "origin")
public class OriginController {
    @Autowired
    private OriginServiceImpl originService;

    @Operation(summary = "For get list of origins")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        List<OriginDTO> res = originService.getAll();
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
    @Operation(summary = "For origin by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOriginById(
            @Parameter(description = "Enter origin to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        OriginDTO res = originService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get origin successfully",
                res
        ));
    }
    @Operation(summary = "For get list of origins by name")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOriginByName(
            @Parameter(description = "Enter keyword to search", required = true)
            @NotEmpty @NotBlank String keyword
    ) {
        List<OriginDTO> res = originService.findByName(keyword);
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

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create origin")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrigin(
            @Valid @RequestBody OriginFormRequest form
    ) {
        OriginDTO res = originService.createOrigin(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create origin failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create origin successfully",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update origin")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOrigin(
            @Parameter(description = "Enter origin id to update", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id,
            @Valid @RequestBody OriginFormRequest form
    ) {
        OriginDTO res = originService.updateOrigin(id, form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Update failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create origin successfully",
                res
        ));
    }

}
