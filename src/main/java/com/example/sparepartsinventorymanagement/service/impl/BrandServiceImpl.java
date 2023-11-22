package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.BrandFromRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetBrandDTO;
import com.example.sparepartsinventorymanagement.entities.Brand;
import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.BrandRepository;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.service.BrandService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ItemRepository itemRepository;
    @Override
    public List<GetBrandDTO> getAll() {
        List<Brand> brands = brandRepository.findAll();
        return mapper.map(brands, new TypeToken<List<GetBrandDTO>>() {}.getType());
    }

    @Override
    public GetBrandDTO getBrandById(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Brand not found.")
        );
       return mapper.map(brand, GetBrandDTO.class);
    }

    @Override
    public GetBrandDTO createBrand(BrandFromRequest from) {
        if(brandRepository.existsByName(from.getName().trim())){
            throw new DuplicateResourceException("Name was existed.");
        }
        Brand brand = Brand.builder()
                .name(from.getName().trim())
                .description(from.getDescription())
                .build();

        brandRepository.save(brand);
        return mapper.map(brand, GetBrandDTO.class);
    }

    @Override
    public GetBrandDTO updateBrand(Long id, BrandFromRequest from) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Brand not found.")
        );
        if (!brand.getName().equalsIgnoreCase(from.getName().trim())){
            if (brandRepository.existsByName(from.getName().trim())){
                throw new DuplicateResourceException("Name was existed.");
            }
            brand.setName(from.getName().trim());
            if(!brand.getItems().isEmpty()){
                for (Item item: brand.getItems()
                ) {
                    int v1 = item.getCode().indexOf("-");
                    int v2 = item.getCode().indexOf("-", v1 + 1) ;
                    StringBuilder result = new StringBuilder();
                    String[] ws = brand.getName().split(" ");
                    for (String word : ws) {
                        if (!word.isEmpty()) {
                            result.append(word.substring(0, 1).toUpperCase());
                        }
                    }

                    String code = item.getCode().substring(0, v1) +"-"+ result +"-"+ item.getCode().substring(v2+1);
                    if(!item.getCode().substring(v1+1, v2).equalsIgnoreCase(result.toString())){
                        String newCode = code;
                        int count = 0;
                        while (itemRepository.existsItemByCodeEqualsIgnoreCase(newCode)){
                            count++;
                            newCode = code + "-N" + count;
                        }
                        item.setCode(newCode);
                        itemRepository.save(item);
                    }
                }
            }
        }
        brand.setDescription(from.getDescription());
        brandRepository.save(brand);
        return mapper.map(brand, GetBrandDTO.class);
    }

    @Override
    public List<GetBrandDTO> getBrandByName(String name) {
        List<Brand> brands = brandRepository.findByNameContaining(name);
        return mapper.map(brands, new TypeToken<List<GetBrandDTO>>() {}.getType());
    }
}
