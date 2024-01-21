package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Location;
import com.example.sparepartsinventorymanagement.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByWarehouse(Warehouse warehouse);
    Optional<Location> findByIdAndWarehouse(Long id, Warehouse warehouse);


    Optional<Location> findByIdAndItem(Long id, Item item);
    List<Location> findByItemAndWarehouse(Item item, Warehouse warehouse);
    List<Location> findByItem(Item item);


    @Modifying
    @Query("DELETE FROM Location l WHERE l.id = :id")
    void deleteLocationById(@Param("id") Long id);

}
