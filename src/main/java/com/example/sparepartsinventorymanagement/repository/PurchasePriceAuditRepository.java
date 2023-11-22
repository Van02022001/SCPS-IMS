package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.dto.request.AuditSearchCriteriaForm;
import com.example.sparepartsinventorymanagement.entities.PurchasePriceAudit;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasePriceAuditRepository extends JpaRepository<PurchasePriceAudit, Long> {
    @Query("SELECT a FROM PurchasePriceAudit a WHERE (a.purchasePrice.item.id = :#{#criteria.itemId} OR :#{#criteria.itemId} IS NULL) AND (a.changedBy.id = :#{#criteria.userId} OR :#{#criteria.userId} IS NULL) AND (a.changeDate BETWEEN :#{#criteria.startDate} AND :#{#criteria.endDate})")
    List<PurchasePriceAudit> searchWithCriteria(@Param("criteria") AuditSearchCriteriaForm criteria);
}

