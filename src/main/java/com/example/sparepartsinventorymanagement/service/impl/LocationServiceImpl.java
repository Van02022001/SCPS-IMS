package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.LocationFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.LocationDTO;
import com.example.sparepartsinventorymanagement.dto.response.ItemLocationsDTO;
import com.example.sparepartsinventorymanagement.entities.Item;
import com.example.sparepartsinventorymanagement.entities.Location;
import com.example.sparepartsinventorymanagement.entities.LocationTag;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.LocationRepository;
import com.example.sparepartsinventorymanagement.repository.LocationTagRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final LocationTagRepository locationTagRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper mapper;
    @Override
    public List<LocationDTO> getLocationsByWarehouse() {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        if(user.getWarehouse() == null){
            throw new InvalidResourceException("User not is inventory staff of any warehouse");
        }
        List<Location> locations = locationRepository.findByWarehouse(user.getWarehouse());
        return mapper.map(locations, new TypeToken<List<LocationDTO>>(){}
                .getType());
    }

    @Override
    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Location not found")
        );
        return mapper.map(location, LocationDTO.class);
    }

    @Override
    public LocationDTO creatLocation(LocationFormRequest form) {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        if(user.getWarehouse() == null){
            throw new InvalidResourceException("User not is inventory staff of any warehouse");
        }
        List<Location> locations = locationRepository.findByWarehouse(user.getWarehouse());
        checkDuplicate(locations, form);
        Location location = Location.builder()
                .binNumber(form.getBinNumber().trim())
                .shelfNumber(form.getShelfNumber().trim())
                .warehouse(user.getWarehouse())
                .build();
        if(!form.getTags_id().isEmpty()){
            List<LocationTag> locationTags = new ArrayList<>();
            for (Long tag_id: form.getTags_id()
                 ) {
                LocationTag locationTag = locationTagRepository.findById(tag_id).orElseThrow(
                        ()-> new NotFoundException("Location tag not found")
                );
                locationTags.add(locationTag);
            }
            location.setTags(locationTags);
        }
        locationRepository.save(location);
        return mapper.map(location, LocationDTO.class);
    }

    @Override
    public LocationDTO updateLocation(Long id, LocationFormRequest form) {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        if(user.getWarehouse() == null){
            throw new InvalidResourceException("User not is inventory staff of any warehouse");
        }
        Location location = locationRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Location not found")
        );

        if(!location.getBinNumber().equalsIgnoreCase(form.getBinNumber().trim()) ||
        !location.getShelfNumber().equalsIgnoreCase(form.getShelfNumber().trim())){
            List<Location> locations = locationRepository.findByWarehouse(user.getWarehouse());
            checkDuplicate(locations, form);
            location.setShelfNumber(form.getShelfNumber().trim());
            location.setBinNumber(form.getBinNumber().trim());
        }
        List<LocationTag> locationTags = new ArrayList<>();
        if(!form.getTags_id().isEmpty()){
            int count = 0;
            for (LocationTag locationTag: location.getTags()
                 ) {
                for (Long tag_id: form.getTags_id()
                     ) {
                    if(Objects.equals(locationTag.getId(), tag_id)){
                       count++;
                       break;
                    }
                }
            }
            if(location.getTags().size()!=count || location.getTags().isEmpty()){
                for (Long tag_id: form.getTags_id()
                ) {
                    LocationTag locationTag = locationTagRepository.findById(tag_id).orElseThrow(
                            ()-> new NotFoundException("Location tag not found")
                    );
                    locationTags.add(locationTag);
                }

            }
        }
        location.setTags(locationTags);
        locationRepository.save(location);
        return mapper.map(location, LocationDTO.class);
    }

    @Override
    public ItemLocationsDTO getLocationsByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new NotFoundException("Item not found")
        );
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        if(user.getWarehouse() == null){
            throw new InvalidResourceException("User not is inventory staff of any warehouse");
        }
        List<Location> locations = locationRepository.findByItemAndWarehouse(item, user.getWarehouse());
        List<LocationDTO> locationDTOS = mapper.map(locations, new TypeToken<List<LocationDTO>>(){}
                .getType());
        return new ItemLocationsDTO(itemId, locationDTOS);
    }

    private void checkDuplicate(List<Location> locations, LocationFormRequest form){
        if(!locations.isEmpty()){
            for (Location location: locations
            ) {
                if(location.getShelfNumber().equalsIgnoreCase(form.getShelfNumber().trim())
                        && location.getBinNumber().equalsIgnoreCase(form.getBinNumber().trim())){
                    throw new DuplicateResourceException("Location was existed");
                }
            }
        }
    }
}
