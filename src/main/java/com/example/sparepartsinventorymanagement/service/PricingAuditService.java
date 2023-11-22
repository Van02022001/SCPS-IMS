package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.response.PricingAuditDTO;

import java.util.List;

public interface PricingAuditService {

    List<PricingAuditDTO> getPricingHistory(Long itemId);
}
