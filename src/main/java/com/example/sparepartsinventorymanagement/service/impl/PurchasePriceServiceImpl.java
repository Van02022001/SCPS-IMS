package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.response.CreatePurchasePriceDTO;
import com.example.sparepartsinventorymanagement.dto.response.GetListPurchasePriceDTO;
import com.example.sparepartsinventorymanagement.dto.response.UpdatePurchasePriceDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.PurchasePriceAuditRepository;
import com.example.sparepartsinventorymanagement.repository.PurchasePriceRepository;
import com.example.sparepartsinventorymanagement.repository.ReceiptDetailRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.PurchasePriceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchasePriceServiceImpl implements PurchasePriceService {

    private final PurchasePriceRepository purchasePriceRepository;
    private final PurchasePriceAuditRepository purchasePriceAuditRepository;


    private final UserRepository userRepository;


    @Override
    public List<GetListPurchasePriceDTO> getAllPurchasePrice() {
        List<PurchasePrice>  allPurchasePrice = purchasePriceRepository.findAll();

        List<GetListPurchasePriceDTO> response = allPurchasePrice.stream()

                .map(price -> {
                    GetListPurchasePriceDTO getListPurchasePriceDTO = new GetListPurchasePriceDTO();
                    getListPurchasePriceDTO.setId(price.getId());
                    getListPurchasePriceDTO.setItemName(price.getItem().getSubCategory().getName());
                    getListPurchasePriceDTO.setPrice(price.getPrice());
                    getListPurchasePriceDTO.setEffectiveDate(price.getEffectiveDate());
                    return getListPurchasePriceDTO;
                })
                .toList();
        return response;
    }

    @Override
    public void createOrUpdatePurchasePrice(Item item, double unitPrice) {
        // Check if there is an existing PurchasePrice for this Item
        PurchasePrice existingPrice = purchasePriceRepository.findByItem(item);

        // Capture old price for auditing
        double oldPrice = (existingPrice != null) ? existingPrice.getPrice() : 0.0;

        if (existingPrice != null) {
            if (existingPrice.getPrice() != unitPrice) {
                // Cập nhật giá và tạo bản ghi kiểm toán
                existingPrice.setPrice(unitPrice);
                purchasePriceRepository.save(existingPrice);

                createPurchasePriceAudit(existingPrice, oldPrice, unitPrice);
            }
        } else {
            // Tạo mới PurchasePrice và PurchasePriceAudit
            PurchasePrice newPrice = PurchasePrice.builder()
                    .item(item)
                    .price(unitPrice)
                    .effectiveDate(new Date())

                    .build();
            purchasePriceRepository.save(newPrice);

            createPurchasePriceAudit(newPrice, 0.0, unitPrice);
        }
    }
    private void createPurchasePriceAudit(PurchasePrice purchasePrice, double oldPrice, double newPrice) {
        PurchasePriceAudit priceAudit = PurchasePriceAudit.builder()
                .changedBy(getCurrentAuthenticatedUser())
                .changeDate(new Date())
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .purchasePrice(purchasePrice)
                .changedBy(getCurrentAuthenticatedUser())
                .build();
        purchasePriceAuditRepository.save(priceAudit);
    }


    private User getCurrentAuthenticatedUser() {
        // Logic to get the current authenticated user
        return userRepository.findById(((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

}
