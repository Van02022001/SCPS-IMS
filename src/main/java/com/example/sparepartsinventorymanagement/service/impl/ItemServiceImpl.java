package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.*;
import com.example.sparepartsinventorymanagement.dto.response.*;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.DuplicateResourceException;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.ItemService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
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

    private final ReceiptDetailRepository receiptDetailRepository;

    private final ModelMapper modelMapper;
    private final WarehouseRepository warehouseRepository;
    private final ReceiptRepository receiptRepository;
    private final PricingRepository pricingRepository;
    private final PurchasePriceRepository purchasePriceRepository;
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
    @Transactional
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
        // Map the request to a Pricing entity
        Pricing pricing = new Pricing();
        pricing.setStartDate(currentDate);
        pricing.setPrice(form.getPrice());

        // Set the item to the pricing
        pricing.setItem(item);

        // Save the Pricing entity
        Pricing savedPricing = pricingRepository.save(pricing);

        // Create and save the PricingAudit
        PricingAudit pricingAudit = new PricingAudit();
        pricingAudit.setChangeDate(currentDate); // Set the current date as the change date
        pricingAudit.setOldPrice(item.getPricing() != null ? item.getPricing().getPrice() : 0.0); // Old price from the item's current pricing
        pricingAudit.setNewPrice(pricing.getPrice());
        pricingAudit.setPricing(savedPricing);
        pricingAudit.setChangedBy(user);
        pricingAuditRepository.save(pricingAudit);

        // Update the Item's Pricing
        item.setPricing(savedPricing);

        PurchasePrice newPrice = PurchasePrice.builder()
                .item(item)
                .price(form.getPurchasePrice())
                .effectiveDate(new Date())
                .build();
        purchasePriceRepository.save(newPrice);
        PurchasePriceAudit priceAudit = PurchasePriceAudit.builder()
                .changedBy(user)
                .changeDate(currentDate)
                .oldPrice(0.0)
                .newPrice(newPrice.getPrice())
                .purchasePrice(newPrice)
                .build();
        purchasePriceAuditRepository.save(priceAudit);
        item.setPurchasePrice(newPrice);


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
        //Update min max stock level
        if(item.getMinStockLevel() != form.getMinStockLevel()){
            item.setMinStockLevel(form.getMinStockLevel());
        }
        if(item.getMaxStockLevel() != form.getMaxStockLevel()){
            item.setMaxStockLevel(form.getMaxStockLevel());
        }

        Date date = new Date();
        //update item
        item.setSubCategory(subCategory);
        item.setBrand(brand);
        item.setOrigin(origin);
        item.setSupplier(supplier);
        item.setUpdatedBy(user);
        item.setUpdatedAt(date);
        //update pricing
        if(item.getPricing() != null && item.getPricing().getPrice() != form.getPrice()){
            // Capture old price for auditing
            double oldPrice = item.getPricing().getPrice();

            // Update pricing
            item.getPricing().setPrice(form.getPrice());
            item.getPricing().setStartDate(date);
            pricingRepository.save(item.getPricing());

            // Audit the price change
            var auditRecord = new PricingAudit();
            auditRecord.setPricing(item.getPricing());
            auditRecord.setOldPrice(oldPrice);
            auditRecord.setNewPrice(form.getPrice());
            auditRecord.setChangeDate(date); // Assuming current date as change date
            auditRecord.setChangedBy(user);
            pricingAuditRepository.save(auditRecord);
        }
        //update purchase price
        if(item.getPurchasePrice() != null && item.getPurchasePrice().getPrice() != form.getPurchasePrice()){

            // Capture old price for auditing
            double oldPrice = item.getPurchasePrice().getPrice();
            item.getPurchasePrice().setPrice(form.getPurchasePrice());
            purchasePriceRepository.save(item.getPurchasePrice());

            PurchasePriceAudit priceAudit = PurchasePriceAudit.builder()
                    .changedBy(user)
                    .changeDate(date)
                    .oldPrice(oldPrice)
                    .newPrice(form.getPurchasePrice())
                    .purchasePrice(item.getPurchasePrice())
                    .build();
            purchasePriceAuditRepository.save(priceAudit);
        }

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
    public List<ItemDTO> getAllItemByWarehouse(Long warehouseId) {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Principal userPrincipal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Tìm kho theo ID
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        // Kiểm tra xem người dùng hiện tại có phải là nhân viên kho của Warehouse đang được truy vấn không
//        if (!currentUser.getWarehouse().getId().equals(warehouseId)) {
//            throw new InvalidResourceException("User is not an inventory staff of the requested warehouse");
//        }

        // Lấy ra danh sách Inventory trong kho
        List<Inventory> inventoryList = warehouse.getInventoryList();

        // Dùng Java Stream để lấy ra tất cả các Item từ mỗi Inventory, tránh trùng lặp
        Set<Item> items = inventoryList.stream()
                .map(Inventory::getItem)
                .collect(Collectors.toSet());

        // Chuyển đổi Set<Item> thành List<ItemDTO>
        return items.stream()
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
        ReceiptDetail receiptDetail = receiptDetailRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Receipt Detail not found")
        );

        int totalQuantity = 0;
        for (UpdateItemLocationRequest request: form.getLocations()
             ) {
            totalQuantity += request.getQuantity();
        }
        if(totalQuantity != receiptDetail.getQuantity()){
            throw new InvalidResourceException("Quantity is invalid, has not same with quantity of item import");
        }
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
            if(itemMovementRepository.existsByReceiptDetailAndToLocation(receiptDetail, location)){
               throw new InvalidResourceException("ReceiptDetail was imported in location");
            }

            if(location.getItem() != null){
                if(Objects.equals(location.getItem().getId(), receiptDetail.getItem().getId())){
                    location.setItem_quantity(location.getItem_quantity()+ request.getQuantity());
                }else{
                    throw new InvalidResourceException("The location already has the others item");
                }
            }else {
                location.setItem_quantity(request.getQuantity());
                location.setItem(receiptDetail.getItem());
            }
            ItemMovement itemMovement = ItemMovement.builder()
                    .toLocation(location)
                    .notes("Nhập kho")
                    .movedAt(date)
                    .quantity(request.getQuantity())
                    .movedBy(user)
                    .item(receiptDetail.getItem())
                    .receiptDetail(receiptDetail)
                    .build();
            location.getToMovements().add(itemMovement);
            if(receiptDetail.getItem().getLocations().stream().noneMatch(
                    location1 -> location1.getId().equals(location.getId())
            )){
                receiptDetail.getItem().getLocations().add(location);
            }

            itemMovementRepository.save(itemMovement);
            locationRepository.save(location);
        }

        itemRepository.save(receiptDetail.getItem());
        return modelMapper.map(receiptDetail.getItem(), ItemDTO.class);

    }

    @Override
    public ItemDTO updateItemLocationAfterExport(UpdateItemLocationAfterExportForm form) {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy người dùng.")
        );

        if(user.getWarehouse() == null){
            throw new AccessDeniedException("Người dùng không có quyền thao tác với kho này.");
        }
        ReceiptDetail receiptDetail = receiptDetailRepository.findById(form.getReceipt_detail_id()).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy nội dung phiếu xuất kho.")
        );

        int totalQuantity = 0;
        for (UpdateItemLocationAfterExportRequest request: form.getLocations()
        ) {
            totalQuantity += request.getQuantity();
        }
        if(totalQuantity != receiptDetail.getQuantity()){
            throw new InvalidResourceException("Số lượng sản phẩm không giống với số lượng sản phẩm xuất.");
        }
        Date date = new Date();
        Set<Long> fromLocationIdsSet = new HashSet<>();
        for (UpdateItemLocationAfterExportRequest request: form.getLocations()
        ) {
            Long fromLocationId = request.getFromLocation_id();
            if (!fromLocationIdsSet.add(fromLocationId)) {
                throw new DuplicateResourceException("Trùng vị trí.");
            }
            //check location thuộc warehouse hiện tại k
            Location location = locationRepository.findByIdAndWarehouse(request.getFromLocation_id(), user.getWarehouse()).orElseThrow(
                    ()-> new NotFoundException("Vị trí không tìm thấy hoặc không thuộc về kho này.")
            );
            if(itemMovementRepository.existsByReceiptDetailAndFromLocation(receiptDetail, location)){
                throw new InvalidResourceException("Hàng đã xuất kho tại vị trí này theo nội dung phiếu xuất kho.");
            }
            if(location.getItem() != null){
                if(location.getItem_quantity() <= 0){
                    throw new InvalidResourceException("Số lượng sản phầm không đúng, nhỏ hơn hoặc bằng 0.");
                }

                if(Objects.equals(location.getItem().getId(), receiptDetail.getItem().getId())){
                    if(location.getItem_quantity() < request.getQuantity()){
                        throw new InvalidResourceException("Số lượng sản phẩm trong vị trí không đủ để xuất.");
                    }
                    location.setItem_quantity(location.getItem_quantity() - request.getQuantity());
                    if(location.getItem_quantity()<=0){
                        location.setItem(null);
                    }
                }else{
                    throw new InvalidResourceException("Vị trí đã tồn tại sản phẩm khác.");
                }
            }else{
                throw new InvalidResourceException("Vị trí chưa có sản phẩm nào.");
            }
            ItemMovement itemMovement = ItemMovement.builder()
                    .fromLocation(location)
                    .notes("Xuất kho")
                    .movedAt(date)
                    .quantity(request.getQuantity())
                    .movedBy(user)
                    .item(receiptDetail.getItem())
                    .receiptDetail(receiptDetail)
                    .build();
            location.getFromMovements().add(itemMovement);
            if(receiptDetail.getItem().getLocations().stream().noneMatch(
                    location1 -> location1.getId().equals(location.getId())
            )){
                receiptDetail.getItem().getLocations().add(location);
            }
            itemMovementRepository.save(itemMovement);
            locationRepository.save(location);
        }

        itemRepository.save(receiptDetail.getItem());
        return modelMapper.map(receiptDetail.getItem(), ItemDTO.class);
    }

    @Override
    public boolean checkUpdateItemLocationAfterUpdate(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy phiếu.")
        );
        int count = 0;
        for (ReceiptDetail receiptDetail: receipt.getDetails()
             ) {
            if(itemMovementRepository.existsByReceiptDetail(receiptDetail)){
                count++;
            }
        }
        if(receipt.getDetails().size() == count){
            receipt.setStatus(ReceiptStatus.Completed);
            receiptRepository.save(receipt);
            return true;
        }else {
            return false;
        }
    }
    @Override
    public List<ItemDTO> getItemsByThisWarehouse() {
        Principal userPrincipal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new NotFoundException("Không tìm thấy người dùng.")
        );

        if (user.getWarehouse() == null) {
            throw new AccessDeniedException("Người dùng không có quyền thao tác với kho này.");
        }

        List<Location> locations = locationRepository.findByWarehouse(user.getWarehouse());
        Set<Item> items = new HashSet<>();
        for (Location location : locations) {
            if (location.getItem() != null) {
                items.add(location.getItem());
            }
        }

        return items.stream().map(item -> {
            Inventory inventory = inventoryRepository.findByItemAndWarehouse(item, user.getWarehouse())
                    .orElseThrow(() -> new NotFoundException("Sản phẩm không có tồn kho."));
            item.setAvailable(inventory.getAvailable());
            item.setQuantity(inventory.getTotalQuantity());
            item.setDefective(inventory.getDefective());

            // Lọc các vị trí thuộc kho của người dùng
            List<LocationDTO> filteredLocations = item.getLocations().stream()
                    .filter(location -> location.getWarehouse().getId().equals(user.getWarehouse().getId()))
                    .map(location -> modelMapper.map(location, LocationDTO.class))
                    .collect(Collectors.toList());

            ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);
            itemDTO.setLocations(filteredLocations);
            return itemDTO;
        }).collect(Collectors.toList());
    }

//    @Override
//    public List<ItemDTO> getItemsByThisWarehouse() {
//        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
//                ()-> new NotFoundException("Không tìm thấy người dùng.")
//        );
//
//        if(user.getWarehouse() == null){
//            throw new AccessDeniedException("Người dùng không có quyền thao tác với kho này.");
//        }
//        List<Location> locations = locationRepository.findByWarehouse(user.getWarehouse());
//        Set<Item> items = new HashSet<>();
//        for (Location location: locations
//             ) {
//            if(location.getItem() != null){
//                items.add(location.getItem());
//            }
//        }
//        for (Item item: items
//             ) {
//            if(!item.getLocations().isEmpty()){
//                for (Location location: item.getLocations()
//                ) {
//                    if(!Objects.equals(location.getWarehouse().getId(), user.getWarehouse().getId())){
//                        item.getLocations().remove(location);
//                    }
//                    if(item.getLocations().isEmpty()) break;
//                }
//            }
//            Inventory inventory = inventoryRepository.findByItemAndWarehouse(item, user.getWarehouse()).orElseThrow(
//                    ()-> new NotFoundException("Sản phẩm không có tồn kho.")
//            );
//            item.setAvailable(inventory.getAvailable());
//            item.setQuantity(inventory.getTotalQuantity());
//            item.setDefective(inventory.getDefective());
//        }
//        return modelMapper.map(items, new TypeToken<List<ItemDTO>>(){}.getType());
//    }

    @Override
    public List<ItemWarehouseDTO> getAllItemsWithDetailsByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        List<Inventory> inventories = inventoryRepository.findByWarehouse(warehouse);

        return inventories.stream().filter(inventory -> inventory.getItem().getStatus() == ItemStatus.Active).map(inventory -> {
            Item item = inventory.getItem();
            Image image = getImageForItem(item); // Assume this method fetches the Image entity for the Item
            String imageUrl = (image != null) ? image.getUrl() : null;

            ItemWarehouseDTO dto = new ItemWarehouseDTO();
            dto.setId(item.getId());
            dto.setCode(item.getCode());
            dto.setSubcategoryName(item.getSubCategory().getName()); // Assuming name is from SubCategory
            dto.setBrandName(item.getBrand().getName());
            dto.setSupplierName(item.getSupplier().getName());
            dto.setOriginName(item.getOrigin().getName());
            dto.setImageUrl(imageUrl);
            dto.setAvailableQuantity(inventory.getAvailable());

            return dto;
        }).collect(Collectors.toList());
    }
    private Image getImageForItem(Item item) {
        if(!item.getSubCategory().getImages().isEmpty()){
            return item.getSubCategory().getImages().get(0);
        }
        return null;
    }
    @Override
    public List<ItemDTO> getAllItemByWarehouseForSaleStaff(Long warehouseId) {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Principal userPrincipal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Tìm kho theo ID
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        // Lấy ra danh sách Inventory trong kho
        List<Inventory> inventoryList = warehouse.getInventoryList();

        // Dùng Java Stream để lấy ra tất cả các Item từ mỗi Inventory, tránh trùng lặp
        Set<Item> items = inventoryList.stream()
                .map(Inventory::getItem)
                .collect(Collectors.toSet());

        // Chuyển đổi Set<Item> thành List<ItemDTO>
        return items.stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String getNameItemByItemId(Long itemId) {
        // Tìm Item dựa trên itemId
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item not found")
        );

        // Lấy SubCategory liên kết với Item
        SubCategory subCategory = item.getSubCategory();

        // Kiểm tra xem SubCategory có null không
        if (subCategory == null) {
            throw new NotFoundException("SubCategory not found for the item");
        }

        // Trả về tên của SubCategory, là tên của Item
        return subCategory.getName();
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
}
