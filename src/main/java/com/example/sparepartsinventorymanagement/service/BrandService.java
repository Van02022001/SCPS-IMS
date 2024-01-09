package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.BrandFromRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetBrandDTO;

import java.util.List;

public interface BrandService {
    List<GetBrandDTO> getAll();
    GetBrandDTO getBrandById(Long id);
    GetBrandDTO createBrand(BrandFromRequest from);
    GetBrandDTO updateBrand(Long id, BrandFromRequest from);
    List<GetBrandDTO> getBrandByName(String name);
}
