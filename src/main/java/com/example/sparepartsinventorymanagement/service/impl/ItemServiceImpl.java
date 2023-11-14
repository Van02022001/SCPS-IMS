package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.dto.response.ItemDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.entities.Period;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
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

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

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

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private PurchasePriceRepository purchasePriceRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PricingAuditRepository pricingAuditRepository;

    @Autowired
    private PurchasePriceAuditRepository purchasePriceAuditRepository;

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private  PeriodRepository periodRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<ItemDTO> getAll() {
        List<Item> items = itemRepository.findAll();
        return modelMapper.map(items, new TypeToken<List<ItemDTO>>(){}.getType());
    }

    @Override
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Item not found")
        );
        return modelMapper.map(item, ItemDTO.class);
    }

    @Override
    public List<ItemDTO> getItemBySubCategory(Long productId) {
        SubCategory subCategory = subCategoryRepository.findById(productId).orElseThrow(
                ()-> new NotFoundException("SubCategory not found")
        );
        List<Item> items = itemRepository.findBySubCategory(subCategory);
        return modelMapper.map(items, new TypeToken<List<ItemDTO>>() {
        }.getType());
    }

    @Override
    public List<ItemDTO> getItemByActiveStatus() {
        List<Item> items = itemRepository.findByStatus(ItemStatus.Active);
        return modelMapper.map(items, new TypeToken<List<ItemDTO>>(){}.getType());
    }

    @Override
    public ItemDTO createItem(ItemFormRequest form) {

        //Check brand
        Brand brand = brandRepository.findById(form.getBrand_id()).orElseThrow(
                ()-> new NotFoundException("Brand not found")
        );
        //Check origin
        Origin origin = originRepository.findById(form.getOrigin_id()).orElseThrow(
                ()-> new NotFoundException("Origin not found")
        );
        //Check supplier
        Supplier supplier = supplierRepository.findByIdAndStatus(form.getSupplier_id(), true).orElseThrow(
                ()-> new RuntimeException("Supplier is invalid")
        );
        //Check sub category
        SubCategory subCategory = subCategoryRepository.findByIdAndStatus(form.getSub_category_id(), SubCategoryStatus.Active).orElseThrow(
                ()-> new RuntimeException("SubCategory is invalid")
        );
        //Check manager
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );


        if(itemRepository.existsBySubCategoryAndOriginAndBrandAndSupplier(subCategory, origin, brand, supplier)){
            throw new DuplicateResourceException("Item was existed");
        }

        //Create code
        String code = createItemCode(subCategory.getName().trim(), subCategory.getSize(), brand.getName().trim(), origin.getName().trim(), supplier.getName().trim());
        String newCode = checkCode(code);
        //Create item
        Date currentDate = new Date();

        Item item = Item.builder()
                .code(newCode)
                .minStockLevel(form.getMinStockLevel())
                .maxStockLevel(form.getMaxStockLevel())
                .quantity(0)
                .sold(0)
                .available(0)
                .status(ItemStatus.Inactive)
                .createdAt(currentDate)
                .updatedAt(currentDate)
                .createdBy(user)
                .subCategory(subCategory)
                .brand(brand)
                .origin(origin)
                .supplier(supplier)
                .updatedBy(user)

                .build();

        itemRepository.save(item);
        return modelMapper.map(item, ItemDTO.class);
    }

    @Override
    public ItemDTO updateItem(Long id, ItemFormRequest form) {
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
        SubCategory subCategory = subCategoryRepository.findById(form.getSub_category_id()).orElseThrow(
                () -> new NotFoundException("SubCategory not found")
        );

        //Check manager
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        //Create code if something on change
        String code = "";
        if (!Objects.equals(item.getSubCategory().getId(), form.getSub_category_id()) || !Objects.equals(item.getBrand().getId(), form.getBrand_id())
                || !Objects.equals(item.getOrigin().getId(), form.getOrigin_id()) || !Objects.equals(item.getSupplier().getId(), form.getSupplier_id())) {
            code = createItemCode(subCategory.getName().trim(), subCategory.getSize(), brand.getName().trim(), origin.getName().trim(), origin.getName().trim());
            if(!code.equalsIgnoreCase(item.getCode())){
                String newCode = checkCode(code);
                item.setCode(newCode);
            }
        }

        Date date = new Date();
        //update item
        item.setSubCategory(subCategory);
        item.setBrand(brand);
        item.setOrigin(origin);
        item.setSupplier(supplier);
        item.setUpdatedBy(user);
        item.setUpdatedAt(date);

        itemRepository.save(item);

        return modelMapper.map(item, ItemDTO.class);
    }

    @Override
    public ItemDTO updateItemStatus(Long id, ItemStatus status) {
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
        return modelMapper.map(item, ItemDTO.class);
    }


    @Override
    public ResponseEntity<?> findBySubCategory_NameContainingIgnoreCase(String name) {
        List<SubCategory> subCategories = subCategoryRepository.findByNameContaining(name);
        List<ItemDTO> itemDTOs = new ArrayList<>();
        for(SubCategory subCategory: subCategories){
            List<Item> items = itemRepository.findBySubCategory(subCategory);
            List<ItemDTO> subCategoryItemDTOs = items.stream()
                    .map(item -> modelMapper.map(item, ItemDTO.class))
                    .toList();
            itemDTOs.addAll(subCategoryItemDTOs);
        }

        if(itemDTOs.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "No items found with the subcategory name " + name, null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Items found with the subcategory name containing: " + name, itemDTOs
        ));

    }
    private String createItemCode(String productName, Size size, String brandName, String originName, String supplierName){
        StringBuilder itemCode = new StringBuilder();

        if (productName != null && !productName.isEmpty()) {
            itemCode.append(getFirstLetters(productName));
            itemCode.append("-");
        }


        if (brandName != null && !brandName.isEmpty()) {
            itemCode.append(getFirstLetters(brandName));
        }

        if (originName != null && !originName.isEmpty()) {
            if (brandName != null && !brandName.isEmpty()) {
                itemCode.append("-");
            }
            itemCode.append(getFirstLetters(originName));
        }
        itemCode.append("-");
        if (supplierName != null && !supplierName.isEmpty()) {
            itemCode.append(getFirstLetters(supplierName));
        }
        itemCode.append("-");
        if (size != null) {
            if (size.getLength() > 0 || size.getWidth() > 0 || size.getHeight() > 0) {
                itemCode.append((int) size.getLength()).append("x").append((int) size.getWidth()).append("x").append((int) size.getHeight());
                itemCode.append("-");
            }
            itemCode.append((int) size.getDiameter());
        }
        return itemCode.toString();
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
    private String checkCode(String code){
        int count = 0;
        String newCode = code;
        while(itemRepository.existsItemByCodeEqualsIgnoreCase(newCode)){
            count++;
            newCode = code + "-N" + count;
        }
        return newCode;
    }
    private Period getPeriod(){
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        Instant instant = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
        Date sdate = Date.from(instant);
        LocalTime endOfDay = LocalTime.of(23, 59, 59, 999999999); // 23:59:59.999999999
        LocalDateTime endOfToday = LocalDateTime.of(today, endOfDay);
        Instant instantEndOfDay = endOfToday.atZone(ZoneId.systemDefault()).toInstant();
        Date edate = Date.from(instantEndOfDay);
        Period period = periodRepository.findByStartDateAndEndDate(sdate, edate);
        if(period == null){
            period = Period.builder()
                    .startDate(sdate)
                    .endDate(edate)
                    .build();
            periodRepository.save(period);
        }
        return period;
    }
}
