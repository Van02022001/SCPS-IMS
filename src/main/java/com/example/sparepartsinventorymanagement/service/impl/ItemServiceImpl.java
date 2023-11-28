package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateItemLocationsFrom;
import com.example.sparepartsinventorymanagement.dto.request.ItemFormRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdateItemLocationRequest;
import com.example.sparepartsinventorymanagement.dto.response.ItemDTO;
import com.example.sparepartsinventorymanagement.dto.response.PricingAuditDTO;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceAuditDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.ItemService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final BrandRepository brandRepository;

    private final OriginRepository originRepository;

    private final SupplierRepository supplierRepository;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;



    private final ItemMovementRepository itemMovementRepository;


    private final PricingAuditRepository pricingAuditRepository;


    private final PurchasePriceAuditRepository purchasePriceAuditRepository;


    private final InventoryRepository inventoryRepository;



    private final ModelMapper modelMapper;

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

    public List<ItemDTO> findBySubCategory_NameContainingIgnoreCase(String name) {
        List<SubCategory> subCategories = subCategoryRepository.findByNameContaining(name);
        if (subCategories.isEmpty()) {
            throw new NotFoundException("No subcategories found with the name: " + name);
        }

        return subCategories.stream()
                .flatMap(subCategory -> itemRepository.findBySubCategory(subCategory).stream())
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());
    }

    @Override

    public List<PurchasePriceAuditDTO> getPurchasePriceHistoryOfItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        PurchasePrice purchasePrice = item.getPurchasePrice();
        if (purchasePrice == null) {
            throw new NotFoundException(" Purchase price not found for item");
        }
        List<PurchasePriceAudit> audits = purchasePriceAuditRepository.findByPurchasePriceId(purchasePrice.getId());

        return audits.stream()
                .map(audit -> {
                    PurchasePriceAuditDTO dto = new PurchasePriceAuditDTO();
                    dto.setId(audit.getId());
                    dto.setItemName(audit.getPurchasePrice().getItem().getSubCategory().getName());
                    dto.setChangeDate(audit.getChangeDate());
                    dto.setOldPrice(audit.getOldPrice());
                    dto.setNewPrice(audit.getNewPrice());
                    dto.setChangedBy(audit.getChangedBy().getLastName() + " " + audit.getChangedBy().getMiddleName() + " " + audit.getChangedBy().getFirstName());
                    return dto;
                }).toList();
    }

    @Override
    public List<PricingAuditDTO> getPricingHistoryOfItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        Pricing pricing = item.getPricing();
        if(pricing == null){
            throw new NotFoundException(" Pricing not found for item");
        }
        List<PricingAudit> audits = pricingAuditRepository.findByPricingId(pricing.getId());

        return  audits.stream()
                .map(audit -> {
                    PricingAuditDTO dto = new PricingAuditDTO();
                    dto.setId(audit.getId());
                    dto.setItemName(audit.getPricing().getItem().getSubCategory().getName());
                    dto.setChangeDate(audit.getChangeDate());
                    dto.setOldPrice(audit.getOldPrice());
                    dto.setNewPrice(audit.getNewPrice());
                    dto.setChangedBy(audit.getChangedBy().getLastName() + " " + audit.getChangedBy().getMiddleName() + " " + audit.getChangedBy().getFirstName());
                    return dto;
                }).toList();
    }

    public ItemDTO createItemLocations(Long id, CreateItemLocationsFrom form) {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        if(user.getWarehouse() == null){
            throw new InvalidResourceException("User not is inventory staff of any warehouse");
        }
        Item item = itemRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Item not found")
        );

        Date date = new Date();
        Set<Long> toLocationIdsSet = new HashSet<>();
        for (UpdateItemLocationRequest request: form.getLocations()
        ) {
            Long toLocationId = request.getToLocation_id();
            if (!toLocationIdsSet.add(toLocationId)) {
                throw new DuplicateResourceException("Location is duplicate");
            }
            //check location thuộc warehouse hiện tại k
            Location location = locationRepository.findByIdAndWarehouse(request.getToLocation_id(), user.getWarehouse()).orElseThrow(
                    ()-> new NotFoundException("Location not found or not belong to this warehouse")
            );
            //Check location co item chua
            if(location.getItem()!=null && !Objects.equals(location.getItem().getId(), item.getId())){
                throw new InvalidResourceException("The location already has the others item");
            }
            if(location.getItem() != null){
                if(Objects.equals(location.getItem().getId(), item.getId())){
                    location.setItem_quantity(location.getItem_quantity()+ request.getQuantity());
                }
            }else {
                location.setItem_quantity(request.getQuantity());
                location.setItem(item);
            }
            ItemMovement itemMovement = ItemMovement.builder()
                    .toLocation(location)
                    .notes("Nhập kho")
                    .movedAt(date)
                    .quantity(request.getQuantity())
                    .movedBy(user)
                    .item(item)
                    .build();
            location.getToMovements().add(itemMovement);
            if(item.getLocations().stream().noneMatch(
                    location1 -> location1.getId().equals(location.getId())
            )){
                item.getLocations().add(location);
            }

            itemMovementRepository.save(itemMovement);
            locationRepository.save(location);
        }

        itemRepository.save(item);
        return modelMapper.map(item, ItemDTO.class);

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
//    private Period getPeriod(){
//        LocalDate today = LocalDate.now();
//        LocalDateTime startOfDay = today.atStartOfDay();
//        Instant instant = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
//        Date sdate = Date.from(instant);
//        LocalTime endOfDay = LocalTime.of(23, 59, 59, 999999999); // 23:59:59.999999999
//        LocalDateTime endOfToday = LocalDateTime.of(today, endOfDay);
//        Instant instantEndOfDay = endOfToday.atZone(ZoneId.systemDefault()).toInstant();
//        Date edate = Date.from(instantEndOfDay);
//        Period period = periodRepository.findByStartDateAndEndDate(sdate, edate);
//        if(period == null){
//            period = Period.builder()
//                    .startDate(sdate)
//                    .endDate(edate)
//                    .build();
//            periodRepository.save(period);
//        }
//        return period;
//    }
}
