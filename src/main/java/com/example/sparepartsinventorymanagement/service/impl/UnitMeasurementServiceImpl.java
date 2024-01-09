package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.response.UnitMeasurementDTO;
import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import com.example.sparepartsinventorymanagement.repository.UnitMeasurementRepository;
import com.example.sparepartsinventorymanagement.service.UnitMeasurementService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitMeasurementServiceImpl implements UnitMeasurementService {

    private final UnitMeasurementRepository repository;
    private final ModelMapper mapper;
    @Override
    public List<UnitMeasurementDTO> getAll() {
        List<UnitMeasurement> unitMeasurements = repository.findAll();
        return mapper.map(unitMeasurements, new TypeToken<List<UnitMeasurementDTO>>(){}
                .getType());
    }
}
