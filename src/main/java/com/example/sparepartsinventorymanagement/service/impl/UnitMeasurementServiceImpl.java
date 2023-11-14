package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.response.UnitMeasurementDTO;
import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import com.example.sparepartsinventorymanagement.repository.UnitMeasurementRepository;
import com.example.sparepartsinventorymanagement.service.UnitMeasurementService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitMeasurementServiceImpl implements UnitMeasurementService {

    @Autowired
    private UnitMeasurementRepository repository;
    @Autowired
    private ModelMapper mapper;
    @Override
    public List<UnitMeasurementDTO> getAll() {
        List<UnitMeasurement> unitMeasurements = repository.findAll();
        return mapper.map(unitMeasurements, new TypeToken<List<UnitMeasurementDTO>>(){}
                .getType());
    }
}
