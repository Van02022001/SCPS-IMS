package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.LocationTagRequest;
import com.example.sparepartsinventorymanagement.dto.response.LocationTagDTO;

import java.util.List;

public interface LocationTagService {
    List<LocationTagDTO> getAll();
    LocationTagDTO getLocationTagById(Long id);
    LocationTagDTO createLocationTag(LocationTagRequest form);
    LocationTagDTO updateLocationTag(Long id, LocationTagRequest form);
}
