package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.OriginFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.OriginDTO;

import java.util.List;

public interface OriginService {
    List<OriginDTO> getAll();
    OriginDTO getById(Long id);
    OriginDTO createOrigin(OriginFormRequest form);
    OriginDTO updateOrigin(Long id, OriginFormRequest form);
    List<OriginDTO> findByName(String keyword);
}
