package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateItemLocationsFrom;
import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateItemLocationAfterExportForm;
import com.example.sparepartsinventorymanagement.dto.response.ItemDTO;
import com.example.sparepartsinventorymanagement.dto.response.PricingAuditDTO;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.entities.ItemStatus;

import java.util.List;

public interface ItemService {
    List<ItemDTO> getAll();
    ItemDTO getItemById(Long id);
    List<ItemDTO> getItemBySubCategory(Long productId);
    List<ItemDTO> getItemByActiveStatus();
    ItemDTO createItem(ItemFormRequest form);
    ItemDTO updateItem(Long id, ItemFormRequest form);
    ItemDTO updateItemStatus(Long id, ItemStatus status);
    List<ItemDTO> findBySubCategory_NameContainingIgnoreCase(String name);

    List<ItemDTO> getAllItemByWarehouse(Long warehouseId);


    List<PurchasePriceAuditDTO> getPurchasePriceHistoryOfItem(Long itemId);

    List<PricingAuditDTO> getPricingHistoryOfItem(Long itemId);

    ItemDTO createItemLocations(Long id, CreateItemLocationsFrom form);
    ItemDTO updateItemLocationAfterExport(UpdateItemLocationAfterExportForm form);

    boolean checkUpdateItemLocationAfterUpdate(Long receiptId);
    List<ItemDTO> getItemsByThisWarehouse();

    List<ItemDTO> getAllItemByWarehouseForSaleStaff(Long warehouseId);

    String getNameItemByItemId(Long itemId);
}
