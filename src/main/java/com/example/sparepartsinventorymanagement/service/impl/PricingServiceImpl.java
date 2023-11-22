package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreatePricingRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePricingRequest;
import com.example.sparepartsinventorymanagement.dto.response.PricingDTOs;
import com.example.sparepartsinventorymanagement.entities.Pricing;
import com.example.sparepartsinventorymanagement.entities.PricingAudit;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.PricingAuditRepository;
import com.example.sparepartsinventorymanagement.repository.PricingRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {
    private final PricingRepository pricingRepository;
    private final PricingAuditRepository pricingAuditRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public PricingDTOs addPricing(CreatePricingRequest request) {
        var pricing = modelMapper.map(request, Pricing.class);
        var savedPricing = pricingRepository.save(pricing);
        return modelMapper.map(savedPricing, PricingDTOs.class);
    }

    @Override
    @Transactional
    public PricingDTOs updatePricing(UpdatePricingRequest request) {
        var existingPricing = pricingRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Pricing not found"));

        // Capture old price for auditing
        double oldPrice = existingPricing.getPrice();

        // Update pricing
        existingPricing.setPrice(request.getPrice());
        existingPricing.setStartDate(request.getStartDate());
        pricingRepository.save(existingPricing);

        // Audit the price change
        var auditRecord = new PricingAudit();
        auditRecord.setPricing(existingPricing);
        auditRecord.setOldPrice(oldPrice);
        auditRecord.setNewPrice(request.getPrice());
        auditRecord.setChangeDate(new Date()); // Assuming current date as change date
        auditRecord.setChangedBy(getCurrentAuthenticatedUser());
        pricingAuditRepository.save(auditRecord);

        return modelMapper.map(existingPricing, PricingDTOs.class);
    }

    @Override
    @Transactional
    public void deletePricing(Long pricingId) {
        pricingRepository.deleteById(pricingId);
    }

    private User getCurrentAuthenticatedUser() {
        // Logic to get the current authenticated user
        return userRepository.findById(((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
