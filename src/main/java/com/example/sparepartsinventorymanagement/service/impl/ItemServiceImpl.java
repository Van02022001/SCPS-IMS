package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.ItemDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.ItemService;
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
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private OriginRepository originRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> getAll() {
        List<Item> items = itemRepository.findAll();
        if(items.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<ItemDTO> res = mapper.map(items, new TypeToken<List<ItemDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list items successfully.", res
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity<?> getItemById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Item not found")
        );
        ModelMapper mapper = new ModelMapper();
        ItemDTO res = mapper.map(item, ItemDTO.class);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get item by id successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> getItemByProduct(Long productId) {
        SubCategory subCategory = productRepository.findById(productId).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );
        List<Item> items = itemRepository.findByProduct(subCategory);
        if(items.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<ItemDTO> res = mapper.map(items, new TypeToken<List<ItemDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list items successfully.", res
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity<?> getItemByActiveStatus() {
        List<Item> items = itemRepository.findByStatus(ItemStatus.Active);
        if(items.size() > 0){

            ModelMapper mapper = new ModelMapper();
            List<ItemDTO> res = mapper.map(items, new TypeToken<List<ItemDTO>>() {
            }.getType());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list items successfully.", res
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                HttpStatus.NOT_FOUND.toString(), "List empty.", null
        ));
    }

    @Override
    public ResponseEntity<?> createItem(ItemFormRequest form) {
        //Check brand
        Brand brand = brandRepository.findById(form.getBrand_id()).orElseThrow(
                ()-> new NotFoundException("Brand not found")
        );
        //Check origin
        Origin origin = originRepository.findById(form.getOrigin_id()).orElseThrow(
                ()-> new NotFoundException("Origin not found")
        );
        //Check supplier
        Supplier supplier = supplierRepository.findById(form.getSupplier_id()).orElseThrow(
                ()-> new NotFoundException("Supplier not found")
        );
        //Check product
        SubCategory subCategory = productRepository.findById(form.getProduct_id()).orElseThrow(
                ()-> new NotFoundException("Product not found")
        );

        //Check warehouse
        Warehouse warehouse = warehouseRepository.findById(form.getWarehouse_id()).orElseThrow(
                ()-> new NotFoundException("Warehouse not found")
        );
        //Check manager
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );

        //Create location
        List<Location> locations = locationRepository.findByWarehouseAndTagsIn(warehouse,form.getTags());
        if(locations.size() > 0){
           if(checkLocationExists(locations, form.getTags())){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                       HttpStatus.BAD_REQUEST.toString(), "The location already contains the item, please choose another location.", null
               ));
           }
        }

        Location location = Location.builder()
                .tags(form.getTags())
                .warehouse(warehouse)
                .build();
        //Create code
        String code = createItemCode(subCategory.getName().trim(), subCategory.getSize(), brand.getName().trim(), origin.getName().trim(), origin.getName().trim());
        //Create item
        Date currentDate = new Date();

        Item item = Item.builder()
                .code(code)
                .costPrice(form.getCostPrice())
                .salePrice(form.getSalePrice())
                .quantity(form.getQuantity())
                .status(ItemStatus.Active)
                .createdAt(currentDate)
                .updatedAt(currentDate)
                .createdBy(user)
                .updatedBy(user)
                .subCategory(subCategory)
                .brand(brand)
                .origin(origin)
                .supplier(supplier)
                .location(location)
                .build();
        location.setItem(item);
        locationRepository.save(location);
        itemRepository.save(item);
        ModelMapper mapper = new ModelMapper();
        ItemDTO res = mapper.map(item, ItemDTO.class);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get item successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> updateItem(Long id, ItemFormRequest form) {
        //Check Item
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item not found")
        );
        //Check brand
        Brand brand = brandRepository.findById(form.getBrand_id()).orElseThrow(
                () -> new NotFoundException("Brand not found")
        );
        //Check origin
        Origin origin = originRepository.findById(form.getOrigin_id()).orElseThrow(
                () -> new NotFoundException("Origin not found")
        );
        //Check supplier
        Supplier supplier = supplierRepository.findById(form.getSupplier_id()).orElseThrow(
                () -> new NotFoundException("Supplier not found")
        );
        //Check product
        SubCategory subCategory = productRepository.findById(form.getProduct_id()).orElseThrow(
                () -> new NotFoundException("Product not found")
        );

        //Check warehouse
        Warehouse warehouse = warehouseRepository.findById(form.getWarehouse_id()).orElseThrow(
                () -> new NotFoundException("Warehouse not found")
        );
        //Check manager
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        //Check location
        List<Location> locations = locationRepository.findByWarehouseAndTagsIn(warehouse, form.getTags());
        if (locations.size() > 0) {
            for (Location l : locations
            ) {
                if (l.getItem().getId() == item.getId()) {
                    locations.remove(l);
                    break;
                }
            }
            if (checkLocationExists(locations, form.getTags())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.toString(), "The location already contains the item, please choose another location.", null
                ));
            }
        }
        //Create code
        String code = null;
        if (item.getSubCategory().getId() != form.getProduct_id() || item.getBrand().getId() != form.getBrand_id()
                || item.getOrigin().getId() != form.getOrigin_id() || item.getSupplier().getId() != form.getSupplier_id()) {
            code = createItemCode(subCategory.getName().trim(), subCategory.getSize(), brand.getName().trim(), origin.getName().trim(), origin.getName().trim());
            item.setCode(code);
        }
        //update item
        item.setSubCategory(subCategory);
        item.setBrand(brand);
        item.setOrigin(origin);
        item.setSupplier(supplier);
        item.setUpdatedBy(user);
        item.setCostPrice(form.getCostPrice());
        item.setSalePrice(form.getSalePrice());
        item.setQuantity(form.getQuantity());
        item.setUpdatedAt(new Date());
        item.getLocation().setTags(form.getTags());
        itemRepository.save(item);

        ModelMapper mapper = new ModelMapper();
        ItemDTO res = mapper.map(item, ItemDTO.class);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update item by id successfully.", res
        ));
    }

    @Override
    public ResponseEntity<?> updateItemStatus(Long id, ItemStatus status) {
        //Check Item
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item not found")
        );
        if(status.equals(ItemStatus.Active)){
            item.setStatus(ItemStatus.Active);
        }else{
            item.setStatus(ItemStatus.Inactive);
        }
        itemRepository.save(item);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Update item status by id successfully.", null
        ));
    }

    private String createItemCode(String productName, Size size, String brandName, String originName, String supplierName){
        StringBuilder itemCode = new StringBuilder();

        if (productName != null && !productName.isEmpty()) {
            itemCode.append(getFirstLetters(productName));
            itemCode.append("-");
        }

        if (size != null) {
            if (size.getLength() > 0 || size.getWidth() > 0 || size.getHeight() > 0) {
                itemCode.append((int)size.getLength() + "x" +(int) size.getWidth() + "x" +(int)size.getHeight());
                itemCode.append("-");
            }
            itemCode.append((int) size.getDiameter());
        }

        if (brandName != null && !brandName.isEmpty()) {
            if (productName != null && !productName.isEmpty()) {
                itemCode.append("-");
            }
            itemCode.append(getFirstLetters(brandName));
        }

        if (originName != null && !originName.isEmpty()) {
            if (brandName != null && !brandName.isEmpty()) {
                itemCode.append("-");
            }
            itemCode.append(getFirstLetters(originName));
        }

        if (supplierName != null && !supplierName.isEmpty()) {
            if (originName != null && !originName.isEmpty()) {
                itemCode.append("-");
            }
            itemCode.append(getFirstLetters(supplierName));
        }

        String finalItemCode = itemCode.toString();

        return finalItemCode;
    }
    private String getFirstLetters(String words){
        StringBuilder result = new StringBuilder();
        String[] ws = words.split(" ");
        for (String word : ws) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase());
            }
        }
        return result.toString();
    }
    private boolean checkLocationExists(List<Location> locations, List<String> tags){
        for (Location l: locations
        ) {
            if(l.getTags().size() == tags.size()){
                int count = 0;
                for (String s1: l.getTags()
                ) {
                    for (String s2 : tags
                    ) {
                        if(s1.trim().equalsIgnoreCase(s2.trim())){
                            count++;
                            break;
                        }
                    }
                }
                if(count == tags.size()){
                    return true;
                }
            }
        }
        return false;
    }
}
