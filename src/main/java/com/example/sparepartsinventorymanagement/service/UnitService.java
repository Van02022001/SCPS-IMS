package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.UnitFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.UnitDTO;

import java.util.List;

public interface UnitService {
    List<UnitDTO> getAll();
    List<UnitDTO> findByName(String keyword);
    UnitDTO getUnitById(Long id);
    UnitDTO createUnit(UnitFormRequest form);
    UnitDTO updateUnit(Long id, UnitFormRequest form);
}
