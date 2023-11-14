package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.OriginFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.OriginDTO;
import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Origin;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.OriginRepository;
import com.example.sparepartsinventorymanagement.service.OriginService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OriginServiceImpl implements OriginService {

    @Autowired
    private OriginRepository originRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<OriginDTO> getAll() {
        List<Origin> origins = originRepository.findAll();
        return mapper.map(origins, new TypeToken<List<OriginDTO>>(){}.getType());
    }

    @Override
    public OriginDTO getById(Long id) {
        Origin origin = originRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Origin not found")
        );
        return mapper.map(origin, OriginDTO.class);
    }

    @Override
    public OriginDTO createOrigin(OriginFormRequest form) {
        if(originRepository.existsByName(form.getName())){
            throw new DuplicateResourceException("Name was existed");
        }
        Origin origin = Origin.builder()
                .name(form.getName())
                .build();
        originRepository.save(origin);
        return mapper.map(origin, OriginDTO.class);
    }

    @Override
    public OriginDTO updateOrigin(Long id, OriginFormRequest form) {
        Origin origin = originRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Origin not found")
        );
        if (!origin.getName().equalsIgnoreCase(form.getName().trim())){
            if (originRepository.existsByName(form.getName().trim())){
                throw new DuplicateResourceException("Name was existed");
            }
            origin.setName(form.getName().trim());
            if(!origin.getItems().isEmpty()){
                for (Item item: origin.getItems()
                ) {
                    int v1 = item.getCode().indexOf("-");
                    int v2 = item.getCode().indexOf("-", v1 + 1) ;
                    int v3 = item.getCode().indexOf("-", v2 + 1);
                    StringBuilder result = new StringBuilder();
                    String[] ws = origin.getName().split(" ");
                    for (String word : ws) {
                        if (!word.isEmpty()) {
                            result.append(word.substring(0, 1).toUpperCase());
                        }
                    }

                    String code = item.getCode().substring(0, v2) +"-"+ result +"-"+ item.getCode().substring(v3+1);
                    if(!item.getCode().substring(v2+1, v3).equalsIgnoreCase(result.toString())){
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
        originRepository.save(origin);
        return mapper.map(origin, OriginDTO.class);
    }


    @Override
    public List<OriginDTO> findByName(String keyword) {
        List<Origin> origins = originRepository.findByNameContaining(keyword);
        return mapper.map(origins, new TypeToken<List<OriginDTO>>(){}.getType());
    }
}
