package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.AuditSearchCriteriaForm;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.entities.PurchasePriceAudit;

import java.util.List;

public interface PurchasePriceAuditService {
    List<PurchasePriceAuditDTO> getAllAudits();
    PurchasePriceAuditDTO getAuditById(Long auditId);

    List<PurchasePriceAuditDTO> searchAudits(AuditSearchCriteriaForm criteria);

    List<PurchasePriceAuditDTO> getItemPriceChangeHistory(Long itemId);
}
