package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.OriginFormRequest;
import com.example.sparepartsinventorymanagement.entities.Origin;
import com.example.sparepartsinventorymanagement.entities.Product;
import com.example.sparepartsinventorymanagement.entities.ProductStatus;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.OriginRepository;
import com.example.sparepartsinventorymanagement.repository.ProductRepository;
import com.example.sparepartsinventorymanagement.service.OriginService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OriginServiceImpl implements OriginService {

    @Autowired
    private OriginRepository originRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ResponseEntity<?> getAll() {
        List<Origin> origins = originRepository.findAll();
        if(origins.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list units successfully.",
                origins
        ));
    }

    @Override
    public ResponseEntity<?> createOrigin(OriginFormRequest form) {
        if(originRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Name of origin already exists.",
                    null
            ));
        }
        ModelMapper mapper = new ModelMapper();
        Origin origin = mapper.map(form, Origin.class);
        originRepository.save(origin);
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Add origin successfully.",
                origin
        ));
    }

    @Override
    public ResponseEntity<?> updateOrigin(Long id, OriginFormRequest form) {
        Origin origin = originRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Origin not found")
        );
        origin.setName(form.getName());
        originRepository.save(origin);
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Update origin successfully.",
                origin
        ));
    }

    @Override
    public ResponseEntity<?> deleteOrigin(Long id) {
        Origin origin = originRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Origin not found")
        );
        originRepository.delete(origin);
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Delete origin successfully.",
                null
        ));
    }

    @Override
    public ResponseEntity<?> findByName(String keyword) {
        List<Origin> origins = originRepository.findByNameContaining(keyword);
        if(origins.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "List is empty",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject(
                HttpStatus.OK.toString(),
                "Get list units successfully.",
                origins
        ));
    }
}
