package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.UnitFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.UnitDTO;
import com.example.sparepartsinventorymanagement.entities.Unit;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.UnitRepository;
import com.example.sparepartsinventorymanagement.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private ModelMapper mapper;

    @Override
    public List<UnitDTO> getAll() {
        List<Unit> units = unitRepository.findAll();
        return mapper.map(units, new TypeToken<List<UnitDTO>>(){}
                .getType());
    }

    @Override
    public List<UnitDTO> findByName(String keyword) {

        List<Unit> units = unitRepository.findByNameContaining(keyword);
        return mapper.map(units, new TypeToken<List<UnitDTO>>(){}
                .getType());
    }

    @Override
    public UnitDTO getUnitById(Long id) {
        Unit unit = unitRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Unit not found")
        );
        return mapper.map(unit, UnitDTO.class);
    }

    @Override
    public UnitDTO createUnit(UnitFormRequest form) {
        if(unitRepository.existsByName(form.getName())){
            throw new DuplicateResourceException("Unit name already exists");
        }
        Unit unit = Unit.builder()
                .name(form.getName())
                .build();
        unitRepository.save(unit);
        return mapper.map(unit, UnitDTO.class);
    }

    @Override
    public UnitDTO updateUnit(Long id,UnitFormRequest form) {
        Unit unit = unitRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Unit not found")
        );
        if(!unit.getName().equalsIgnoreCase(form.getName().trim())){
            if(unitRepository.existsByName(form.getName())){
                throw new DuplicateResourceException("Unit name already exists");
            }
            unit.setName(form.getName());
        }
        unitRepository.save(unit);
        return mapper.map(unit, UnitDTO.class);
    }

}
