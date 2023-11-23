package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.AuditSearchCriteriaForm;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.PurchasePriceAuditRepository;
import com.example.sparepartsinventorymanagement.repository.PurchasePriceRepository;
import com.example.sparepartsinventorymanagement.service.PurchasePriceAuditService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchasePriceAuditServiceImpl implements PurchasePriceAuditService {
    private final PurchasePriceAuditRepository purchasePriceAuditRepository;
    private final PurchasePriceRepository purchasePriceRepository;
    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;

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

    @Override
    public List<PurchasePriceAuditDTO> getItemPriceChangeHistory(Long itemId) {
        // Đầu tiên, truy xuất PurchasePrice liên kết với itemId
        PurchasePrice purchasePrice = purchasePriceRepository.findByItemId(itemId)
                .orElseThrow(() -> new NotFoundException("Purchase price for item not found"));

        // Sau đó, sử dụng purchasePrice để truy xuất các bản ghi lịch sử giá từ PurchasePriceAudit
        List<PurchasePriceAudit> auditRecords = purchasePriceAuditRepository.findByPurchasePrice_IdOrderByChangeDateDesc(purchasePrice.getId());

        // Chuyển đổi các bản ghi sang DTOs
        return auditRecords.stream().map(auditRecord -> {
            User changedByUser = auditRecord.getChangedBy();
            // Định dạng tên của người dùng đã thay đổi giá
            String changedByName = changedByUser != null ?
                    changedByUser.getLastName() + " " + (changedByUser.getMiddleName() != null ? changedByUser.getMiddleName() : "") + " " + changedByUser.getFirstName() :
                    "Unknown User";

            // Tạo DTO mới từ các bản ghi
            return new PurchasePriceAuditDTO(
                    auditRecord.getId(),
                    // Giả định rằng bạn có một cách để truy xuất tên item từ item_id
                    getItemNameFromItemId(itemId), // Phương thức giả định này cần được viết để truy xuất tên item
                    changedByName,
                    auditRecord.getChangeDate(),
                    auditRecord.getOldPrice(),
                    auditRecord.getNewPrice()
            );
        }).collect(Collectors.toList());

    }
    public String getItemNameFromItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .map(item -> item.getSubCategory())
                .map(SubCategory::getName)
                .orElse("Unknow item");
    }
}
