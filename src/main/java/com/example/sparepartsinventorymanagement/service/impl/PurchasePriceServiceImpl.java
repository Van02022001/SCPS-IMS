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
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PurchasePriceAuditRepository purchasePriceAuditRepository;
    private final ReceiptDetailRepository receiptDetailRepository;
    @Override
    public CreatePurchasePriceDTO createPurchasePrice(CreatePurchasePriceRequest request) {
        var purchasePrice = modelMapper.map(request, PurchasePrice.class);
        var savedPurchasePrice = purchasePriceRepository.save(purchasePrice);
        return modelMapper.map(savedPurchasePrice, CreatePurchasePriceDTO.class);
    }

    @Override
    @Transactional
    public UpdatePurchasePriceDTO updatePurchasePrice(UpdatePurchasePriceRequest request) {
        // Lấy PurchasePrice hiện tại của mặt hàng
        PurchasePrice existingPurchasePrice = purchasePriceRepository.findByItem_Id(request.getId());

        if (existingPurchasePrice == null) {
            throw new NotFoundException("Purchase not found");
        }

        // Tạo một bản sao của PurchasePrice hiện tại để lưu trữ giá cũ và ngày hiệu lực cũ
        double oldPrice = existingPurchasePrice.getPrice();
        Date oldEffectiveDate = existingPurchasePrice.getEffectiveDate();

        // Cập nhật PurchasePrice hiện tại với giá mới và ngày hiệu lực
        existingPurchasePrice.setPrice(request.getPrice());
        existingPurchasePrice.setEffectiveDate(request.getEffectiveDate());
        purchasePriceRepository.save(existingPurchasePrice);

        // Lấy tất cả ReceiptDetail liên quan đến mặt hàng này
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByItemId(request.getId());

        // Cập nhật unitPrice trong ReceiptDetail nếu creationDate của chúng là sau ngày hiệu lực của oldPurchasePrice
        for (ReceiptDetail receiptDetail : receiptDetails) {
            Date creationDate = receiptDetail.getCreationDate();
            if (creationDate != null && creationDate.after(oldEffectiveDate)) {
                // Đây là ReceiptDetail mới, cập nhật unitPrice từ PurchasePrice
                receiptDetail.setPurchasePrice(existingPurchasePrice.getItem().getPurchasePrice());
                receiptDetailRepository.save(receiptDetail);
            }
        }

        // Tạo và lưu audit record
        PurchasePriceAudit auditRecord = new PurchasePriceAudit();
        auditRecord.setPurchasePrice(existingPurchasePrice);
        auditRecord.setOldPrice(oldPrice);
        auditRecord.setNewPrice(request.getPrice());
        auditRecord.setChangeDate(new Date()); // Giả sử ngày thay đổi là ngày hiện tại
        auditRecord.setChangedBy(getCurrentAuthenticatedUser());
        purchasePriceAuditRepository.save(auditRecord);

        // Tạo DTO để trả về
        UpdatePurchasePriceDTO responseDTO = new UpdatePurchasePriceDTO();
        responseDTO.setId(existingPurchasePrice.getId());
        responseDTO.setItemName(existingPurchasePrice.getItem().getSubCategory().getName());
        responseDTO.setPrice(existingPurchasePrice.getPrice());
        responseDTO.setEffectiveDate(existingPurchasePrice.getEffectiveDate());

        return responseDTO;
    }


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
    public List<GetListPurchasePriceDTO> getHistoricalPricesForItem(Long itemId) {
        var historicalPrices = purchasePriceRepository.findByItemId(itemId);
        List<GetListPurchasePriceDTO> response =  historicalPrices.stream()
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

    private User getCurrentAuthenticatedUser() {
        // Logic to get the current authenticated user
        return userRepository.findById(((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
