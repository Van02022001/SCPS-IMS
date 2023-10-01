package com.example.sparepartsinventorymanagement.service;

import org.springframework.http.ResponseEntity;

public interface UnitMeasurementService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> findByName(String keyword);
}
