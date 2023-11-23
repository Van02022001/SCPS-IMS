package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.WarehouseFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.InventoryStaffDTO;
import com.example.sparepartsinventorymanagement.dto.response.WarehouseDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.repository.WarehouseRepository;
import com.example.sparepartsinventorymanagement.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {


    private final WarehouseRepository warehouseRepository;

    private final ItemRepository itemRepository;


    private final UserRepository userRepository;

    private final ModelMapper mapper;



    @Override
    public List<WarehouseDTO> getAll() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        return mapper.map(warehouses, new TypeToken<List<WarehouseDTO>>() {
            }.getType());
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Warehouse not found")
        );
        return mapper.map(warehouse, WarehouseDTO.class);
    }

    @Override
    public List<WarehouseDTO> getWarehousesByActiveStatus() {
        List<Warehouse> warehouses = warehouseRepository.findByStatus(WarehouseStatus.Active);
        return mapper.map(warehouses, new TypeToken<List<WarehouseDTO>>() {
            }.getType());
    }

    @Override
    public List<WarehouseDTO> getWarehouseByName(String keyword) {
        List<Warehouse> whs = warehouseRepository.findByNameContaining(keyword);
        return mapper.map(whs, new TypeToken<List<WarehouseDTO>>() {
            }.getType());
    }

    @Override
    public WarehouseDTO createWarehouse(WarehouseFormRequest form) {
        //check name and address of warehouse

        if(warehouseRepository.existsByName(form.getName())){
            throw new DuplicateResourceException("Name of warehouse was existed.");
        }
        if(warehouseRepository.existsByAddress(form.getAddress())){
           throw new DuplicateResourceException("Address of warehouse was existed.");
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
        return mapper.map(warehouse, WarehouseDTO.class);
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseFormRequest form) {
        //check name and address of warehouse
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Warehouse not found")
        );
        if(warehouseRepository.existsByName(form.getName()) && !form.getName().equalsIgnoreCase(warehouse.getName())){
            throw new DuplicateResourceException("Name of warehouse was existed.");
        }
        if(warehouseRepository.existsByAddress(form.getAddress()) && !form.getAddress().equalsIgnoreCase(warehouse.getAddress())){
            throw new DuplicateResourceException("Address of warehouse was existed.");
        }

        warehouse.setName(form.getName());
        warehouse.setAddress(form.getAddress());
        warehouse.setUpdatedAt(new Date());
        warehouseRepository.save(warehouse);
        return mapper.map(warehouse, WarehouseDTO.class);
    }

    @Override
    public List<InventoryStaffDTO> getAllInventoryStaffByWarehouseId(Long warehouseId) {
        try {
            // Truy xuất danh sách người dùng (inventory staff) thuộc warehouse dựa trên warehouseId
            List<User> inventoryStaffList = userRepository.findAllByWarehouseIdAndRole_Name(warehouseId, "INVENTORY_STAFF");

            // Chuyển đổi danh sách người dùng thành danh sách InventoryStaffDTO
            List<InventoryStaffDTO> inventoryStaffDTOs = inventoryStaffList.stream()
                    .map(this::convertToInventoryStaffDTO)
                    .collect(Collectors.toList());

            return inventoryStaffDTOs;
        } catch (Exception e) {
            // Xử lý lỗi ở đây, ví dụ ghi log hoặc trả về một danh sách rỗng hoặc thông báo lỗi tùy thuộc vào yêu cầu của bạn.
            // Ví dụ trả về danh sách rỗng:
            return Collections.emptyList();
        }
    }
    private InventoryStaffDTO convertToInventoryStaffDTO(User user) {
        InventoryStaffDTO inventoryStaffDTO = new InventoryStaffDTO();
        inventoryStaffDTO.setId(user.getId());
        inventoryStaffDTO.setFirstName(user.getFirstName());
        inventoryStaffDTO.setMiddleName(user.getMiddleName());
        inventoryStaffDTO.setLastName(user.getLastName());
        return inventoryStaffDTO;
    }


//    @Override
//    public ResponseEntity<?> updateWarehouseStatus(Long id, WarehouseStatus status) {
//        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
//                ()-> new NotFoundException("Warehouse not found")
//        );
//        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
//                ()-> new NotFoundException("User not found")
//        );
//        ModelMapper mapper = new ModelMapper();
//        if(status == WarehouseStatus.Active){
//            warehouse.setStatus(WarehouseStatus.Active);
//        }else {
//            warehouse.setStatus(WarehouseStatus.Inactive);
//           if(warehouse.getLocations().size() > 0){
//               for (Location l : warehouse.getLocations()
//               ) {
//                   l.getItem().setStatus(ItemStatus.Inactive);
//                   l.getItem().setUpdatedAt(new Date());
//                   l.getItem().setUpdatedBy(user);
//                   itemRepository.save(l.getItem());
//               }
//           }
//        }
//        warehouseRepository.save(warehouse);
//        WarehouseDTO res = mapper.map(warehouse, WarehouseDTO.class);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
//                HttpStatus.OK.toString(), "Update status of warehouse successfully.", res
//        ));
//    }
}
