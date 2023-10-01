package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.entities.Unit;
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
}
