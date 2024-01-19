package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.dto.response.InventoryItemSummaryDTO;
import com.example.sparepartsinventorymanagement.entities.Inventory;
import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByItem(Item item);
    List<Inventory> findByWarehouse(Warehouse warehouse);

    Optional<Inventory> findByItemAndWarehouse(Item item, Warehouse warehouse);

    List<Inventory> findAllByWarehouseId(Long warehouseId);
    @Query("SELECT " +
            "i.item.id AS itemId, " +

            "SUM(i.inboundQuantity) AS inboundQuantitySum, " +
            "SUM(i.inboundValue) AS inboundValueSum, " +
            "SUM(i.outboundQuantity) AS outboundQuantitySum, " +
            "SUM(i.outboundValue) AS outboundValueSum, " +
            "SUM(i.totalValue) AS totalValueSum, " +
            "SUM(i.discrepancyQuantity) AS discrepancyQuantitySum, " +
            "SUM(i.discrepancyValue) AS discrepancyValueSum " +
            "FROM Inventory i " +
            "GROUP BY i.item.id")
    List<InventoryItemSummaryDTO> getInventorySummaryForAllItems();
    Optional<Inventory> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);

//    @Query("SELECT i FROM Inventory i WHERE i.date >= :startDate AND i.date <= :endDate")
//    List<Inventory> findAllBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
