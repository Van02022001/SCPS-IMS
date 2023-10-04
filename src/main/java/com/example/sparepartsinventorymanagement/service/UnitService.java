package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.UnitFormRequest;
import org.springframework.http.ResponseEntity;

public interface UnitService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> findByName(String keyword);

    ResponseEntity<?> createUnit(UnitFormRequest form);
    ResponseEntity<?> updateUnit(Long id, UnitFormRequest form);
    ResponseEntity<?> deleteUnit(Long id);

}
