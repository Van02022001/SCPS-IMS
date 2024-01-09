package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.LocationTagRequest;
import com.example.sparepartsinventorymanagement.dto.response.LocationTagDTO;
import com.example.sparepartsinventorymanagement.entities.LocationTag;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.LocationTagRepository;
import com.example.sparepartsinventorymanagement.service.LocationTagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationTagServiceImpl implements LocationTagService {
    private final LocationTagRepository locationTagRepository;
    private final ModelMapper mapper;
    @Override
    public List<LocationTagDTO> getAll() {
        List<LocationTag> locationTags = locationTagRepository.findAll();
        return mapper.map(locationTags, new TypeToken<List<LocationTagDTO>>(){}
                .getType());
    }

    @Override
    public LocationTagDTO getLocationTagById(Long id) {
        LocationTag locationTag = locationTagRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Location tag not found")
        );
        return mapper.map(locationTag, LocationTagDTO.class);
    }

    @Override
    public LocationTagDTO createLocationTag(LocationTagRequest form) {
        List<LocationTag> locationTags = locationTagRepository.findAll();
        checkDuplicateName(locationTags, form.getName().trim());
        LocationTag locationTag = LocationTag.builder()
                .name(form.getName())
                .build();
        locationTagRepository.save(locationTag);
        return mapper.map(locationTag, LocationTagDTO.class);
    }

    @Override
    public LocationTagDTO updateLocationTag(Long id, LocationTagRequest form) {
        LocationTag locationTag = locationTagRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Location tag not found")
        );
        if(!locationTag.getName().equalsIgnoreCase(form.getName().trim())){
            List<LocationTag> locationTags = locationTagRepository.findAll();
            checkDuplicateName(locationTags, form.getName().trim());
            locationTag.setName(form.getName().trim());
        }
        locationTagRepository.save(locationTag);
        return mapper.map(locationTag, LocationTagDTO.class);
    }

    private void checkDuplicateName(List<LocationTag> locationTags, String name){
        if(!locationTags.isEmpty()){
            for (LocationTag locationTag: locationTags
            ) {
                if (locationTag.getName().equalsIgnoreCase(name)){
                    throw  new DuplicateResourceException("Name of location tag was existed.");
                }
            }
        }
    }
}
