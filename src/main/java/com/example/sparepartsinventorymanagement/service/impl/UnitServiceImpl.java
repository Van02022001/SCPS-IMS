package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.UnitFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.UnitDTO;
import com.example.sparepartsinventorymanagement.entities.Unit;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.UnitRepository;
import com.example.sparepartsinventorymanagement.service.UnitService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitRepository unitRepository;

    @Override
    public ResponseEntity<?> getAll() {

        List<Unit> units = unitRepository.findAll();
        if(units.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list units successfully.",
                units
                ));
    }

    @Override
    public ResponseEntity<?> findByName(String keyword) {

        List<Unit> units = unitRepository.findByNameContaining(keyword);

        if(units.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list units successfully.",
                units
        ));
    }

    @Override
    public ResponseEntity<?> createUnit(UnitFormRequest form) {
        if(unitRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Unit name already exists", null
            ));
        }
        Unit unit = Unit.builder()
                .name(form.getName())
                .build();
        unitRepository.save(unit);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Create unit successfully.",
                new UnitDTO(unit.getId(), unit.getName())
        ));
    }

    @Override
    public ResponseEntity<?> updateUnit(Long id,UnitFormRequest form) {
        Unit unit = unitRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Unit not found")
        );
        unit.setName(form.getName());
        unitRepository.save(unit);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update unit successfully.",
                new UnitDTO(unit.getId(), unit.getName())
        ));
    }

    @Override
    public ResponseEntity<?> deleteUnit(Long id) {
        Unit unit = unitRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Unit not found")
        );
        unitRepository.delete(unit);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Delete unit successfully.",
                null
        ));
    }
}
