package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreatePricingRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePricingRequest;
import com.example.sparepartsinventorymanagement.dto.response.PricingDTOs;

public interface PricingService {
    PricingDTOs addPricing(CreatePricingRequest request);

    PricingDTOs updatePricing(UpdatePricingRequest request);

    void deletePricing(Long pricingId);
}
