package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.LocationFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.LocationDTO;
import com.example.sparepartsinventorymanagement.entities.Location;

import java.util.List;

public interface LocationService {
    List<LocationDTO> getLocationsByWarehouse();
    LocationDTO getLocationById(Long id);
    LocationDTO creatLocation(LocationFormRequest form);
    LocationDTO updateLocation(Long id, LocationFormRequest form);

}
