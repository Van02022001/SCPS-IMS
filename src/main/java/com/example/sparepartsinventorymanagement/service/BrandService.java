package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateBrandFrom;
import com.example.sparepartsinventorymanagement.dto.request.UpdateBrandFrom;
import org.springframework.http.ResponseEntity;

public interface BrandService {
    ResponseEntity getAll();
    ResponseEntity getBrandId(Long id);
    ResponseEntity createBrand(CreateBrandFrom from);
    ResponseEntity updateBrand(Long id, UpdateBrandFrom from);
    ResponseEntity getBrandByName(String name);
    ResponseEntity deleteBrand(Long id);
}
