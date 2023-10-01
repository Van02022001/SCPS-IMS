package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.entities.Unit;
import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import com.example.sparepartsinventorymanagement.repository.UnitMeasurementRepository;
import com.example.sparepartsinventorymanagement.service.UnitMeasurementService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitMeasurementServiceImpl implements UnitMeasurementService {

    @Autowired
    private UnitMeasurementRepository repository;
    @Override
    public ResponseEntity<?> getAll() {
        List<UnitMeasurement> unitMeasurements = repository.findAll();

        if(unitMeasurements.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list Unit Measurement successfully.",
                unitMeasurements
        ));
    }

    @Override
    public ResponseEntity<?> findByName(String keyword) {
        List<UnitMeasurement> unitMeasurements = repository.findByNameContaining(keyword);

        if(unitMeasurements.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list Unit Measurement successfully.",
                unitMeasurements
        ));
    }
}
