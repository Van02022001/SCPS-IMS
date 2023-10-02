package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.OriginFormRequest;
import org.springframework.http.ResponseEntity;

public interface OriginService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> createOrigin(OriginFormRequest form);
    ResponseEntity<?> updateOrigin(Long id, OriginFormRequest form);
    ResponseEntity<?> deleteOrigin(Long id);
    ResponseEntity<?> findByName(String keyword);
}
