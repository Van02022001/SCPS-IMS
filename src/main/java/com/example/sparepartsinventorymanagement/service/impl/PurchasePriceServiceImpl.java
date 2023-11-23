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






}
