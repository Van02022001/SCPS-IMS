package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.AuditSearchCriteriaForm;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.PurchasePriceAuditRepository;
import com.example.sparepartsinventorymanagement.service.PurchasePriceAuditService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchasePriceAuditServiceImpl implements PurchasePriceAuditService {
    private final PurchasePriceAuditRepository purchasePriceAuditRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PurchasePriceAuditDTO> getAllAudits() {
        var audits = purchasePriceAuditRepository.findAll();
        return audits.stream()
                .map(audit -> modelMapper.map(audit, PurchasePriceAuditDTO.class))
                .toList();
    }

    @Override
    public PurchasePriceAuditDTO getAuditById(Long auditId) {
        var audit = purchasePriceAuditRepository.findById(auditId)
                .orElseThrow(() -> new NotFoundException("Audit not found"));
        return modelMapper.map(audit, PurchasePriceAuditDTO.class);

    }

    @Override
    public List<PurchasePriceAuditDTO> searchAudits(AuditSearchCriteriaForm criteria) {
        var audits = purchasePriceAuditRepository.searchWithCriteria(criteria);

        return audits.stream()
                .map(audit -> modelMapper.map(audit, PurchasePriceAuditDTO.class))
                .toList();
    }
}
