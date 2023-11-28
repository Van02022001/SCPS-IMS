package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ItemMovementRequest;
import com.example.sparepartsinventorymanagement.dto.response.ItemMovementDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.ItemMovementService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemMovementServiceImpl implements ItemMovementService {
    private final ItemMovementRepository itemMovementRepository;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper mapper;
    @Override
    public List<ItemMovementDTO> getByItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()->new NotFoundException("Item not found")
        );
        List<ItemMovement> itemMovements = itemMovementRepository.findByItem(item);
        return mapper.map(itemMovements, new TypeToken<List<ItemMovementDTO>>(){}
                .getType());
    }

    @Override
    public ItemMovementDTO getById(Long id) {
        ItemMovement itemMovement = itemMovementRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item movement not found")
        );
        return mapper.map(itemMovement, ItemMovementDTO.class);
    }

    @Override
    public ItemMovementDTO createItemMovementInWarehouse(ItemMovementRequest request) {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        if(user.getWarehouse() == null){
            throw new InvalidResourceException("User not is inventory staff of any warehouse");
        }
        //Check item
        Item item = itemRepository.findById(request.getItem_id()).orElseThrow(
                ()-> new NotFoundException("Item not found")
        );
        Inventory inventory = inventoryRepository.findByItemAndWarehouse(item, user.getWarehouse()).orElseThrow(
                ()-> new NotFoundException("Inventory not found")
        );
        if(request.getQuantity() > inventory.getClosingStockQuantity()){
            throw new InvalidResourceException("Quantity move is big");
        }
        if(item.getLocations().isEmpty()){
            throw new InvalidResourceException("Item has not any location");
        }
        //Check locations
        Location fromLocation = locationRepository.findByIdAndItem(request.getFromLocation_id(), item).orElseThrow(
                ()-> new NotFoundException("Item has not this location")
        );

        Location toLocation = locationRepository.findById(request.getToLocation_id()).orElseThrow(
                ()-> new NotFoundException("Location not found")
        );
        if(toLocation.getItem() != null && !Objects.equals(toLocation.getItem().getId(), item.getId())){
            throw new InvalidResourceException("location has already contains others item");
        }
        if(!Objects.equals(fromLocation.getWarehouse().getId(), toLocation.getWarehouse().getId())){
            throw new InvalidResourceException("Locations is not the same warehouse.");
        }
        if(Objects.equals(fromLocation.getId(), toLocation.getId())){
            throw new DuplicateResourceException("Locations is duplicate.");
        }
        //Kiểm tra số lượng của item cho mỗi location
        if(fromLocation.getItem_quantity() < request.getQuantity()){
            throw new InvalidResourceException("Quantity move is big");
        }
        else if(fromLocation.getItem_quantity() == request.getQuantity()){
            fromLocation.setItem(null);
            fromLocation.setItem_quantity(0);
            if(toLocation.getItem()!=null){
                if(Objects.equals(toLocation.getItem().getId(), item.getId())){
                    toLocation.setItem_quantity(toLocation.getItem_quantity()+ request.getQuantity());
                }
            } else {
                toLocation.setItem_quantity(request.getQuantity());
                toLocation.setItem(item);
            }
        } else {
            fromLocation.setItem_quantity(fromLocation.getItem_quantity() - request.getQuantity());
            if(toLocation.getItem()!=null){
                if(Objects.equals(toLocation.getItem().getId(), item.getId())){
                    toLocation.setItem_quantity(toLocation.getItem_quantity()+ request.getQuantity());
                }
            } else {
                toLocation.setItem_quantity(request.getQuantity());
                toLocation.setItem(item);
            }
        }
        ItemMovement itemMovement = ItemMovement.builder()
                .quantity(request.getQuantity())
                .notes(request.getNotes())
                .item(item)
                .movedAt(new Date())
                .movedBy(user)
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .build();
        itemMovementRepository.save(itemMovement);
        fromLocation.getFromMovements().add(itemMovement);
        toLocation.getToMovements().add(itemMovement);
        locationRepository.save(fromLocation);
        locationRepository.save(toLocation);
        return mapper.map(itemMovement, ItemMovementDTO.class);
    }
}
