package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateBrandFrom;
import com.example.sparepartsinventorymanagement.dto.request.UpdateBrandFrom;
import com.example.sparepartsinventorymanagement.entities.Brand;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.BrandRepository;
import com.example.sparepartsinventorymanagement.service.BrandService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Override
    public ResponseEntity getAll() {
        List<Brand> brands = brandRepository.findAll();
        if(brands.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),"Get list brand successfully.", brands
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(),"List empty", null
        ));
    }

    @Override
    public ResponseEntity getBrandById(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Brand not found.")
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Get brand successfully.", brand
        ));
    }

    @Override
    public ResponseEntity createBrand(CreateBrandFrom from) {
        if(brandRepository.existsByName(from.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),"Name of brand already exists.", null
            ));
        }
        ModelMapper mapper =  new ModelMapper();
        Brand brand = mapper.map(from, Brand.class);

        brandRepository.save(brand);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Create brand successfully.", brand
        ));
    }

    @Override
    public ResponseEntity updateBrand(Long id, UpdateBrandFrom from) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Brand not found.")
        );
        brand.setName(from.getName());
        brand.setDescription(from.getDescription());

        brandRepository.save(brand);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),"Update brand successfully.", brand
        ));
    }

    @Override
    public ResponseEntity getBrandByName(String name) {
        List<Brand> brands = brandRepository.findByNameContaining(name);
        if(brands.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),"Get list brand by name successfully.", brands
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(),"List empty", null
        ));
    }




}
