package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Receipt;
import com.example.sparepartsinventorymanagement.entities.ReceiptDetail;
import com.example.sparepartsinventorymanagement.entities.ReceiptType;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptDetailRepository extends JpaRepository<ReceiptDetail, Long> {



    List<ReceiptDetail> findByReceiptId(Long receiptId);

    List<ReceiptDetail> findByItemId(Long itemId);

    @Query("SELECT rd FROM ReceiptDetail rd WHERE rd.item = :item AND rd.receipt.type = :receiptType")
    List<ReceiptDetail> findByItemAndReceiptType(@Param("item") Item item, @Param("receiptType") ReceiptType receiptType);


}
