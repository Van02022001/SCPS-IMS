package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.entities.PurchasePriceAudit;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.PurchasePriceAuditRepository;
import com.example.sparepartsinventorymanagement.service.PurchasePriceAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchasePriceAuditServiceImpl implements PurchasePriceAuditService {
    private final PurchasePriceAuditRepository purchasePriceAuditRepository;


    @Override
    public List<PurchasePriceAuditDTO> getAllAudits() {
        var audits = purchasePriceAuditRepository.findAll();
        return audits.stream()

                .map(audit -> {
                    PurchasePriceAuditDTO dto = new PurchasePriceAuditDTO();
                    dto.setId(audit.getId());
                    dto.setItemName(audit.getPurchasePrice().getItem().getSubCategory().getName());
                    dto.setChangedBy(audit.getChangedBy().getLastName()+" "+ audit.getChangedBy().getMiddleName()+ " "+audit.getChangedBy().getFirstName());
                    dto.setChangeDate(audit.getChangeDate());
                    dto.setOldPrice(audit.getOldPrice());
                    dto.setNewPrice(audit.getNewPrice());
                    return dto;
                })
                .sorted(Comparator.comparing(PurchasePriceAuditDTO::getItemName))
                .toList();
    }

    @Override
    public PurchasePriceAuditDTO getAuditById(Long auditId) {
        PurchasePriceAudit audit = purchasePriceAuditRepository.findById(auditId)
                .orElseThrow(() -> new NotFoundException("Audit not found"));

        PurchasePriceAuditDTO dto = new PurchasePriceAuditDTO();
        dto.setId(audit.getId());
        dto.setItemName(audit.getPurchasePrice().getItem().getSubCategory().getName());
        dto.setChangedBy(audit.getChangedBy().getLastName()+" "+ audit.getChangedBy().getMiddleName()+ " "+audit.getChangedBy().getFirstName());
        dto.setChangeDate(audit.getChangeDate());
        dto.setOldPrice(audit.getOldPrice());
        dto.setNewPrice(audit.getNewPrice());
        return dto;
    }





}
