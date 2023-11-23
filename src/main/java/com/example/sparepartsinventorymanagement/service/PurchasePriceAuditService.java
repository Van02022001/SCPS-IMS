package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;

import java.util.List;

public interface PurchasePriceAuditService {
    List<PurchasePriceAuditDTO> getAllAudits();
    PurchasePriceAuditDTO getAuditById(Long auditId);



}
