package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.*;
import com.example.sparepartsinventorymanagement.dto.response.ItemDTO;
import com.example.sparepartsinventorymanagement.dto.response.PricingAuditDTO;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.service.impl.ItemServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "item")
public class ItemController {
    @Autowired
    private ItemServiceImpl itemService;

    @Operation(summary = "For get list of items")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        List<ItemDTO> res = itemService.getAll();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
               HttpStatus.NOT_FOUND.toString(),
               "List is empty",
               null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list items successfully",
                res
        ));
    }

    @Operation(summary = "For get item by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getItemById(
            @Parameter(description = "Enter id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
        ItemDTO res = itemService.getItemById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list items successfully",
                res
        ));
    }
    @Operation(summary = "For get items by sub category id")
    @GetMapping(value = "/items-by-sub-category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getItemsByProduct(
            @Parameter(description = "Enter id to get", example = "1", required = true) @PathVariable(name = "id") @NotBlank Long id
    ) {
       List<ItemDTO> res = itemService.getItemBySubCategory(id);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list items by sub category successfully",
                res
        ));
    }

    @Operation(summary = "For get items by active status")
    @GetMapping(value = "/active-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveItems() {
        List<ItemDTO> res = itemService.getItemByActiveStatus();
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list items by active status successfully",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For create items")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createItem(
            @Valid @RequestBody ItemFormRequest form
    ) {
        if(form.getMinStockLevel()>= form.getMaxStockLevel()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Min stock cannot be greater than max stock", null
            ));
        }
        ItemDTO res = itemService.createItem(form);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Create failed",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Create item successfully",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For update item")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateItem(
            @Parameter(description = "Enter id", required = true, example = "1") @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody ItemFormRequest form
    ) {
        ItemDTO res = itemService.updateItem(id, form);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update item successfully",
                res
        ));
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
        ItemDTO res = itemService.updateItemStatus(id, status);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Update item status successfully",
                res
        ));
    }

    @Operation(summary = "Search for items by name ")
    @GetMapping("subcategoryName")
    public ResponseEntity<?> searchItemBySubCategory_NameContainingIgnoreCase(
            @Parameter(description = "Subcategory name to search for ", example = "Bạc")
            @RequestParam(name = "partialName") String name
    ){
        List<ItemDTO> response = itemService.findBySubCategory_NameContainingIgnoreCase(name);
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Get items successfully",
                response
        ));
    }

    @Operation(summary = "Get purchase price history for Item")
    @GetMapping("/purchase-price-history/{itemId}")
    public ResponseEntity<?> getItemPurchasePriceHistory(@PathVariable Long itemId) {
        try {
            List<PurchasePriceAuditDTO> history = itemService.getPurchasePriceHistoryOfItem(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Get list Purchase Price History successfully",
                    history
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get Pricing history for Item")
    @GetMapping("/pricing-history/{itemId}")
    public ResponseEntity<?> getItemPricingHistory(@PathVariable Long itemId) {
        try {
            List<PricingAuditDTO> history = itemService.getPricingHistoryOfItem(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Get list Purchase Price History successfully",
                    history
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "For update locations of item by receipt id")
    @PutMapping(value = "/item-locations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateItemLocation(
            @Parameter(description = "Enter id", required = true, example = "1")
            @NotNull @NotEmpty @PathVariable(name = "id") Long id,
            @Valid @RequestBody CreateItemLocationsFrom form
    ) {
        ItemDTO res = itemService.createItemLocations(id, form);
        if(res != null){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Update item locations successfully",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                HttpStatus.BAD_REQUEST.toString(),
                "Update item locations failed",
                null
        ));

    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "For update locations item after export")
    @PutMapping(value = "/item-locations/warehouse-export ", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateItemLocation(
            @Valid @RequestBody UpdateItemLocationAfterExportForm form
    ) {
        ItemDTO res = itemService.updateItemLocationAfterExport(form);
        if(res != null){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Update item locations successfully",
                    res
            ));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                HttpStatus.BAD_REQUEST.toString(),
                "Update item locations failed",
                null
        ));

    }
    @Operation(summary = "For check item import/export by receipt detail")
    @GetMapping(value = "/item-locations/examination", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkItemLocationAfterUpdate(
            @Valid @RequestBody CheckItemLocationAfterUpdateForm form
    ) {
        boolean res = itemService.checkUpdateItemLocationAfterUpdate(form);
        if(!res){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Các sản phẩm chưa được cập nhật hết vị trí.",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Cập nhật vị trí các sản phẩm thành công.",
                null
        ));
    }
    @PreAuthorize("hasRole('ROLE_INVENTORY_STAFF')")
    @Operation(summary = "Get all items by warehouse")
    @GetMapping("/items-by-warehouse/{warehouseId}")
    public ResponseEntity<?> getAllItemsByWarehouse(
            @Parameter(description = "Enter warehouse ID", required = true, example = "1")
            @PathVariable(name = "warehouseId") Long warehouseId
    ) {
        try {
            List<ItemDTO> items = itemService.getAllItemByWarehouse(warehouseId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Get list items by warehouse successfully",
                    items
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "Warehouse not found",
                    null
            ));
        } catch (InvalidResourceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObject(
                    HttpStatus.FORBIDDEN.toString(),
                    "Access denied",
                    null
            ));
        }
    }
}
