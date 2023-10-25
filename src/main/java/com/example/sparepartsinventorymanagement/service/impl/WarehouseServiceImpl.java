package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.WarehouseFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.repository.WarehouseRepository;
import com.example.sparepartsinventorymanagement.service.WarehouseService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> getAll() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        if(warehouses.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<WarehouseDTO> res = mapper.map(warehouses, new TypeToken<List<WarehouseDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list warehouse successfully.", res
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity<?> getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Warehouse not found")
        );
        ModelMapper mapper = new ModelMapper();
        WarehouseDTO res = mapper.map(warehouse, WarehouseDTO.class);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get warehouse by id successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> getWarehousesByActiveStatus() {
        List<Warehouse> warehouses = warehouseRepository.findByStatus(WarehouseStatus.Active);
        if(warehouses.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<WarehouseDTO> res = mapper.map(warehouses, new TypeToken<List<WarehouseDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list warehouse successfully.", res
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity<?> getWarehouseByName(String keyword) {
        List<Warehouse> whs = warehouseRepository.findByNameContaining(keyword);
        if(whs.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<WarehouseDTO> res = mapper.map(whs, new TypeToken<List<WarehouseDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list warehouse successfully.", res
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity<?> createWarehouse(WarehouseFormRequest form) {
        //check name and address of warehouse

        if(warehouseRepository.existsByName(form.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Name of warehouse was existed.", null
            ));
        }
        if(warehouseRepository.existsByAddress(form.getAddress())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Address of warehouse was existed.", null
            ));
        }
        Date cDate = new Date();
        Warehouse warehouse = Warehouse.builder()
                .name(form.getName())
                .address(form.getAddress())
                .status(WarehouseStatus.Active)
                .createdAt(cDate)
                .updatedAt(cDate)
                .build();
        warehouseRepository.save(warehouse);
        ModelMapper mapper = new ModelMapper();
        WarehouseDTO res = mapper.map(warehouse, WarehouseDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Create warehouse successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> updateWarehouse(Long id, WarehouseFormRequest form) {
        //check name and address of warehouse
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Warehouse not found")
        );
        if(warehouseRepository.existsByName(form.getName()) && !form.getName().equalsIgnoreCase(warehouse.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Name of warehouse was existed.", null
            ));
        }
        if(warehouseRepository.existsByAddress(form.getAddress()) && !form.getAddress().equalsIgnoreCase(warehouse.getAddress())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Address of warehouse was existed.", null
            ));
        }

        warehouse.setName(form.getName());
        warehouse.setAddress(form.getAddress());
        warehouse.setUpdatedAt(new Date());
        warehouseRepository.save(warehouse);
        ModelMapper mapper = new ModelMapper();
        WarehouseDTO res = mapper.map(warehouse, WarehouseDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update warehouse successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> updateWarehouseStatus(Long id, WarehouseStatus status) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Warehouse not found")
        );
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        ModelMapper mapper = new ModelMapper();
        if(status == WarehouseStatus.Active){
            warehouse.setStatus(WarehouseStatus.Active);
        }else {
            warehouse.setStatus(WarehouseStatus.Inactive);
           if(warehouse.getLocations().size() > 0){
               for (Location l : warehouse.getLocations()
               ) {
                   l.getItem().setStatus(ItemStatus.Inactive);
                   l.getItem().setUpdatedAt(new Date());
                   l.getItem().setUpdatedBy(user);
                   itemRepository.save(l.getItem());
               }
           }
        }
        warehouseRepository.save(warehouse);
        WarehouseDTO res = mapper.map(warehouse, WarehouseDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update status of warehouse successfully.", res
        ));
    }
}
