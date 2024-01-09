package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.response.PricingAuditDTO;
import com.example.sparepartsinventorymanagement.repository.PricingAuditRepository;
import com.example.sparepartsinventorymanagement.service.PricingAuditService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingAuditServiceImpl implements PricingAuditService {

    private final PricingAuditRepository pricingAuditRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PricingAuditDTO> getPricingHistory(Long itemId) {
        var audits = pricingAuditRepository.findByPricingItemId(itemId);
        return audits.stream()
                .map(audit -> modelMapper.map(audit, PricingAuditDTO.class))
                .toList();
    }
}