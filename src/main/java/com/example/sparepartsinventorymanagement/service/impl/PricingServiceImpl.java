package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreatePricingRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePricingRequest;
import com.example.sparepartsinventorymanagement.dto.response.PricingDTOs;
import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Pricing;
import com.example.sparepartsinventorymanagement.entities.PricingAudit;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.PricingAuditRepository;
import com.example.sparepartsinventorymanagement.repository.PricingRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.PricingService;
import jakarta.persistence.EntityNotFoundException;
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
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public PricingDTOs addPricing(CreatePricingRequest request) {
        // Map the request to a Pricing entity
        Pricing pricing = new Pricing();
        pricing.setStartDate(request.getStartDate());
        pricing.setPrice(request.getPrice());
        // Fetch the item
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + request.getItemId()));

        // Set the item to the pricing
        pricing.setItem(item);

        // Save the Pricing entity
        Pricing savedPricing = pricingRepository.save(pricing);

        // Create and save the PricingAudit
        PricingAudit pricingAudit = new PricingAudit();
        pricingAudit.setChangeDate(new Date()); // Set the current date as the change date
        pricingAudit.setOldPrice(item.getPricing() != null ? item.getPricing().getPrice() : 0.0); // Old price from the item's current pricing
        pricingAudit.setNewPrice(pricing.getPrice());
        pricingAudit.setPricing(savedPricing);
        pricingAudit.setChangedBy(getCurrentAuthenticatedUser());
        pricingAuditRepository.save(pricingAudit);

        // Update the Item's Pricing
        item.setPricing(savedPricing);
        itemRepository.save(item);

        // Map the saved Pricing to a DTO and return it
        PricingDTOs pricingDTOs = modelMapper.map(savedPricing, PricingDTOs.class);
        pricingDTOs.setItemName(item.getCode()); // Assuming item's code is what you want as itemName in the DTO
        return pricingDTOs;
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
