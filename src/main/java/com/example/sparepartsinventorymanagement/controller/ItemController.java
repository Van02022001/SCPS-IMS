package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateItemForm;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;
import com.example.sparepartsinventorymanagement.service.impl.ItemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
    @Autowired
    private ItemServiceImpl itemService;

    @Operation(summary = "For get list of items")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        return itemService.getAll();
    }

    @Operation(summary = "For get item by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getItemById(
            @Parameter(description = "Enter id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        return itemService.getItemById(id);
    }
    @Operation(summary = "For get items by sub category id")
    @GetMapping(value = "/items-by-sub-category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getItemsByProduct(
            @Parameter(description = "Enter id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        return itemService.getItemBySubCategory(id);
    }

    @Operation(summary = "For get items by active status")
    @GetMapping(value = "/active-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveItems() {
        return itemService.getItemByActiveStatus();
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create items")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createItem(
            @Valid @RequestBody ItemFormRequest form
    ) {
        return itemService.createItem(form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update item")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateItem(
            @Parameter(description = "Enter id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody UpdateItemForm form
    ) {
        return itemService.updateItem(id, form);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update status of item")
    @PutMapping(value = "/item-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateItemStatus(
            @Parameter(description = "Enter id", required = true, example = "1")
            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
            @Parameter(description = "Item status (Active or Inactive)", required = true)
            @NotNull @NotEmpty @Pattern(regexp = "Active|Inactive") @RequestParam(name = "status") ItemStatus status
    ) {
        return itemService.updateItemStatus(id, status);
    }

    @Operation(summary = "Search for items by subcategory name ")
    @GetMapping()
    public ResponseEntity<?> searchItemBySubCategory_NameContainingIgnoreCase(
            @Parameter(description = "Subcategory name to search for ", example = "Bạc")
            @RequestParam(name = "partialName") String name
    ){
        return itemService.findBySubCategory_NameContainingIgnoreCase(name);
    }
}
