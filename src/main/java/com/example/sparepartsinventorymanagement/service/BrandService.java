package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateBrandFrom;
import com.example.sparepartsinventorymanagement.dto.request.UpdateBrandFrom;
import com.example.sparepartsinventorymanagement.entities.Brand;
import com.example.sparepartsinventorymanagement.entities.BrandStatus;
import org.springframework.http.ResponseEntity;

public interface BrandService {
    ResponseEntity getAll();
    ResponseEntity getBrandById(Long id);
    ResponseEntity createBrand(CreateBrandFrom from);
    ResponseEntity updateBrand(Long id, UpdateBrandFrom from);
    ResponseEntity getBrandByName(String name);
    ResponseEntity updateBrandStatus(Long id, BrandStatus status);
}
