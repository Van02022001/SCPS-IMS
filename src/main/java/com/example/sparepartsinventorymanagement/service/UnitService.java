package com.example.sparepartsinventorymanagement.service;

import org.springframework.http.ResponseEntity;

public interface UnitService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> findByName(String keyword);
}
