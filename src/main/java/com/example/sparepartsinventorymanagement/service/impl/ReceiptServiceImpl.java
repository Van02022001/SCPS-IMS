package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.*;

import com.example.sparepartsinventorymanagement.dto.response.*;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.InvalidInventoryDataException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.exception.QuantityExceedsInventoryException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import com.example.sparepartsinventorymanagement.service.ReceiptService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptDetailRepository  receiptDetailRepository;
    private final ModelMapper modelMapper;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;
    private final UnitRepository unitRepository;
    private final PurchasePriceRepository purchasePriceRepository;
    private final EntityManager entityManager;
    private final PurchasePriceAuditRepository purchasePriceAuditRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryDiscrepancyLogRepository inventoryDiscrepancyLogRepository;
    private final InventoryDiscrepancyLogsRepository inventoryDiscrepancyLogsRepository;
    private final CustomerRequestReceiptRepository customerRequestReceiptRepository;
    private final LocationRepository locationRepository;


    @Override
    public void deleteImportRequestReceipt(Long id) {
        var receipt = receiptRepository.findById(id)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + id + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));
        receiptRepository.delete(receipt);
    }

    @Override
    public void deleteImportReceipt(Long id) {
        var receipt = receiptRepository.findById(id)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + id + " not found or not of type PHIEU_NHAP_KHO"));
        receiptRepository.delete(receipt);
    }

    @Override
    public void deleteExportReceipt(Long id) {
        var receipt = receiptRepository.findById(id)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_XUAT_KHO)
                .orElseThrow(() -> new NotFoundException("Export Receipt with Id " + id + " not found or not of type PHIEU_XUAT_KHO"));
        receiptRepository.delete(receipt);
    }

    @Override
    public List<ReceiptDetailDTO> getItemsNullLocation(Long id) {
        Receipt receipt = receiptRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy phiếu")
        );
        List<ReceiptDetail> detailResponses = new ArrayList<>();

        for (ReceiptDetail requestDetail : receipt.getDetails()) {
            if(!requestDetail.getItemMovements().isEmpty()){
               detailResponses.add(requestDetail);
            }
        }
        return modelMapper.map(detailResponses, new TypeToken<List<ReceiptDetailDTO>>(){}.getType());
    }

    @Override
    @Transactional
    public void confirmImportRequestReceipt(Long receiptId) {

        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        var receipt = receiptRepository.findById(receiptId)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
//        if (currentUser == null || !currentUser.getId().equals(receipt.getReceivedBy().getId())) {
//            throw new AccessDeniedException("You do not have permission to confirm this import request receipt.");
//        }
        if (currentUser == null || !currentUser.getWarehouse().equals(receipt.getWarehouse())) {
            throw new AccessDeniedException("You do not have permission to confirm this internal export request receipt.");
        }

        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.Approved) {
            throw new IllegalStateException("This import request receipt has already been confirmed.");
        }
        // Cập nhật trạng thái
        receipt.setStatus(ReceiptStatus.Approved);
        var updatedReceipt = receiptRepository.save(receipt);

        // Gửi thông báo cho Manager
        Notification notification =  notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                updatedReceipt.getId(),
                updatedReceipt.getCreatedBy().getId(), // Giả sử createdBy là Manager
                NotificationType.XAC_NHAN_NHAP_KHO,
                "Phiếu yêu cầu nhập kho #" + updatedReceipt.getId() + " đã được xác nhận."
        );

    }

    @Override
    @Transactional
    public void  startImportProcess(Long receiptId) {

        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with id " + receiptId +" not found"));


        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
//        if (currentUser == null || !currentUser.getId().equals(receipt.getReceivedBy().getId())) {
//            throw new AccessDeniedException("You do not have permission to confirm this import request receipt.");
//        }
        if (currentUser == null || !currentUser.getWarehouse().equals(receipt.getWarehouse())) {
            throw new AccessDeniedException("You do not have permission to confirm this internal export request receipt.");
        }
        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.IN_PROGRESS) {
            throw new IllegalStateException("This import request receipt has already been in progress.");
        }
        receipt.setStatus(ReceiptStatus.IN_PROGRESS);
        receiptRepository.save(receipt);

        // Gửi thông báo cho Manager
        Notification notification = notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                receipt.getId(),
                receipt.getCreatedBy().getId(), // Giả sử createdBy là Manager
                NotificationType.DANG_TIEN_HANH_NHAP_KHO,
                "Phiếu yêu cầu nhập kho #" + receipt.getId() + " đang được tiến hành"
        );

    }


    @Override
    public List<ImportRequestReceiptResponse> getAllImportRequestReceipts() {
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_YEU_CAU_NHAP_KHO);
        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                 //   response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết

                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getTotalPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                                .id(detail.getItem().getId())
                                                .brandName(detail.getItem().getBrand().getName())
                                                .originName(detail.getItem().getOrigin().getName())
                                                .subcategoryName(detail.getItem().getSubCategory().getName())
                                                .supplierName(detail.getItem().getSupplier().getName())
                                                .code(detail.getItem().getCode())
                                                .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllImportRequestReceiptsByWareHouse() {
        // Get the current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Use the warehouse ID from the current user to fetch receipts
        Long warehouseId = currentUser.getWarehouse().getId();
        List<Receipt> receipts = receiptRepository.findByTypeAndWarehouseId(ReceiptType.PHIEU_YEU_CAU_NHAP_KHO, warehouseId);

        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
               //     response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllImportReceiptsByWareHouse() {
        // Get the current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Use the warehouse ID from the current user to fetch receipts
        Long warehouseId = currentUser.getWarehouse().getId();
        List<Receipt> receipts = receiptRepository.findByTypeAndWarehouseId(ReceiptType.PHIEU_NHAP_KHO, warehouseId);


        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
               //     response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllImportReceipts() {
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_NHAP_KHO);
        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                   // response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportRequestReceiptResponse getImportRequestReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with ID " + id + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(receipt.getWarehouse().getId());
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setTotalPrice(receipt.getTotalPrice());
    //    response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Thêm thông tin chi tiết phiếu
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }
    @Override
    @Transactional
    public ImportRequestReceiptResponse getImportReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with ID " + id + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(receipt.getWarehouse().getId());
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setTotalPrice(receipt.getTotalPrice());
       // response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Thêm thông tin chi tiết phiếu
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }
    @Override
    @Transactional
    public ImportRequestReceiptResponse createImportRequestReceipt(ImportRequestReceiptForm importRequestReceiptForm) {

        // Check warehouse
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        Warehouse warehouse = warehouseList.stream()
                .filter( warehouse1 -> warehouse1.getId().equals(importRequestReceiptForm.getWarehouseId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Warehouse not found!"));


        // Check inventory staff
        List<User> inventoryStaffList = userRepository.findAllByWarehouseAndRoleName(warehouse, "INVENTORY_STAFF");
        User inventoryStaff = inventoryStaffList.stream()
                .filter(user -> user.getId().equals(importRequestReceiptForm.getInventoryStaffId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Inventory Staff not found!"));

        // Create a new receipt
        Receipt newReceipt = Receipt.builder()
                .code(generateAndValidateUniqueCode())
                .type(ReceiptType.PHIEU_YEU_CAU_NHAP_KHO)
                .status(ReceiptStatus.Pending_Approval)
                .description(importRequestReceiptForm.getDescription())
                .createdBy(getCurrentAuthenticatedUser())
               // .receivedBy(inventoryStaff)
                .warehouse(warehouse)
                .build();
        Receipt savedReceipt = receiptRepository.save(newReceipt);

        // Calculate total quantity and total price
        int totalQuantity = 0;
        double totalPrice = 0;

        List<ReceiptDetail> receiptDetails = new ArrayList<>();

        for (ImportRequestReceiptDetailForm detailForm : importRequestReceiptForm.getDetails()) {
            // Get product information
            Item item = itemRepository.findById(detailForm.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found"));
            double unitPrice = item.getPurchasePrice().getPrice();
            double totalPriceForItem = unitPrice * detailForm.getQuantity();
            // Create receipt detail
            ReceiptDetail receiptDetail = ReceiptDetail.builder()
                    .item(item)
                    .quantity(detailForm.getQuantity())
                    .unitName(item.getSubCategory().getUnit().getName())
                    .unitPrice(unitPrice)

                    .totalPrice(totalPriceForItem)
                    .receipt(savedReceipt)
                    .build();
            receiptDetails.add(receiptDetail);

            totalQuantity += detailForm.getQuantity();
            totalPrice += receiptDetail.getTotalPrice();
        }
        newReceipt.setTotalQuantity(totalQuantity);
        newReceipt.setTotalPrice(totalPrice);
        // Save the receipt and receipt details
        receiptDetailRepository.saveAll(receiptDetails);

        // Send a notification
        notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.REQUESTED,
                savedReceipt.getId(),
                inventoryStaff.getId(),
                NotificationType.YEU_CAU_NHAP_KHO,
                "Yêu cầu nhập kho #" + savedReceipt.getId() + " đã được tạo."
        );

        // Create response
        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(savedReceipt.getWarehouse().getId());
        response.setId(savedReceipt.getId());
        response.setCode(savedReceipt.getCode());
        response.setType(savedReceipt.getType());
        response.setStatus(savedReceipt.getStatus());
        response.setDescription(savedReceipt.getDescription());
      //  response.setReceivedBy(savedReceipt.getReceivedBy().getLastName() + " " + savedReceipt.getReceivedBy().getMiddleName() + " " + savedReceipt.getReceivedBy().getFirstName());

        response.setCreatedBy(savedReceipt.getCreatedBy().getLastName() + " " + savedReceipt.getCreatedBy().getMiddleName() + " " + savedReceipt.getCreatedBy().getFirstName());
        response.setLastModifiedBy(savedReceipt.getLastModifiedBy() != null ? savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(savedReceipt.getCreationDate());
        response.setUpdatedAt(savedReceipt.getLastModifiedDate());
        response.setTotalQuantity(totalQuantity);
        response.setTotalPrice(totalPrice);

        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);
        return response;
    }

    @Override
    public ImportRequestReceiptResponse createInternalRequestReceipt(ImportRequestReceiptForm internalRequestReceiptForm) {
        // Check warehouse
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        Warehouse warehouse = warehouseList.stream()
                .filter( warehouse1 -> warehouse1.getId().equals(internalRequestReceiptForm.getWarehouseId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Warehouse not found!"));


        // Check inventory staff
        List<User> inventoryStaffList = userRepository.findAllByWarehouseAndRoleName(warehouse, "INVENTORY_STAFF");
        User inventoryStaff = inventoryStaffList.stream()
                .filter(user -> user.getId().equals(internalRequestReceiptForm.getInventoryStaffId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Inventory Staff not found!"));

        // Create a new receipt
        Receipt newReceipt = Receipt.builder()
                .code(generateAndValidateUniqueCode())
                .type(ReceiptType.PHIEU_YEU_CAU_NHAP_NOI_BO)
                .status(ReceiptStatus.Pending_Approval)
                .description(internalRequestReceiptForm.getDescription())
                .createdBy(getCurrentAuthenticatedUser())
                //.receivedBy(inventoryStaff)
                .warehouse(warehouse)
                .build();
        Receipt savedReceipt = receiptRepository.save(newReceipt);

        // Calculate total quantity and total price
        int totalQuantity = 0;
        double totalPrice = 0;

        List<ReceiptDetail> receiptDetails = new ArrayList<>();

        for (ImportRequestReceiptDetailForm detailForm : internalRequestReceiptForm.getDetails()) {
            // Get product information
            Item item = itemRepository.findById(detailForm.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found"));
            double unitPrice = item.getPurchasePrice().getPrice();
            double totalPriceForItem = unitPrice * detailForm.getQuantity();
            // Create receipt detail
            ReceiptDetail receiptDetail = ReceiptDetail.builder()
                    .item(item)
                    .quantity(detailForm.getQuantity())
                    .unitName(item.getSubCategory().getUnit().getName())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPriceForItem)
                    .receipt(savedReceipt)
                    .build();
            receiptDetails.add(receiptDetail);

            totalQuantity += detailForm.getQuantity();
            totalPrice += receiptDetail.getTotalPrice();
        }
        newReceipt.setTotalQuantity(totalQuantity);
        newReceipt.setTotalPrice(totalPrice);
        // Save the receipt and receipt details
        receiptDetailRepository.saveAll(receiptDetails);

        // Send a notification
        notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.REQUESTED,
                savedReceipt.getId(),
                inventoryStaff.getId(),
                NotificationType.YEU_CAU_NHAP_KHO_NOI_BO,
                "Yêu cầu nhập kho noi bo #" + savedReceipt.getId() + " đã được tạo."
        );

        // Create response
        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(savedReceipt.getWarehouse().getId());
        response.setId(savedReceipt.getId());
        response.setCode(savedReceipt.getCode());
        response.setType(savedReceipt.getType());
        response.setStatus(savedReceipt.getStatus());
        response.setDescription(savedReceipt.getDescription());
        //  response.setReceivedBy(savedReceipt.getReceivedBy().getLastName() + " " + savedReceipt.getReceivedBy().getMiddleName() + " " + savedReceipt.getReceivedBy().getFirstName());

        response.setCreatedBy(savedReceipt.getCreatedBy().getLastName() + " " + savedReceipt.getCreatedBy().getMiddleName() + " " + savedReceipt.getCreatedBy().getFirstName());
        response.setLastModifiedBy(savedReceipt.getLastModifiedBy() != null ? savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(savedReceipt.getCreationDate());
        response.setUpdatedAt(savedReceipt.getLastModifiedDate());
        response.setTotalQuantity(totalQuantity);
        response.setTotalPrice(totalPrice);

        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);
        return response;
    }

    @Override
    public void confirmInternalImportRequestReceipt(Long receiptId) {
        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        var receipt = receiptRepository.findById(receiptId)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_NOI_BO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found or not of type PHIEU_YEU_CAU_CHUYEN_KHO"));

        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
//        if (currentUser == null || !currentUser.getId().equals(receipt.getReceivedBy().getId())) {
//            throw new AccessDeniedException("You do not have permission to confirm this import request receipt.");
//        }
        if (currentUser == null || !currentUser.getWarehouse().equals(receipt.getWarehouse())) {
            throw new AccessDeniedException("You do not have permission to confirm this internal export request receipt.");
        }
        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.Approved) {
            throw new IllegalStateException("This internal import request receipt has already been confirmed.");
        }
        // Cập nhật trạng thái
        receipt.setStatus(ReceiptStatus.Approved);
        var updatedReceipt = receiptRepository.save(receipt);

        // Gửi thông báo cho Manager
        Notification notification =  notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                updatedReceipt.getId(),
                updatedReceipt.getCreatedBy().getId(), // Giả sử createdBy là Manager
                NotificationType.XAC_NHAN_NHAP_KHO_NOI_BO,
                "Phiếu yêu cầu nhap kho noi bo #" + updatedReceipt.getId() + " đã được xác nhận."
        );
    }

    @Override
    public void startInternalImportProcess(Long receiptId) {
        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with id " + receiptId +" not found"));


        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
//        if (currentUser == null || !currentUser.getId().equals(receipt.getReceivedBy().getId())) {
//            throw new AccessDeniedException("You do not have permission to confirm this import request receipt.");
//        }
        if (currentUser == null || !currentUser.getWarehouse().equals(receipt.getWarehouse())) {
            throw new AccessDeniedException("You do not have permission to confirm this internal export request receipt.");
        }
        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.IN_PROGRESS) {
            throw new IllegalStateException("This internal import request receipt has already been in progress.");
        }
        receipt.setStatus(ReceiptStatus.IN_PROGRESS);
        receiptRepository.save(receipt);

        // Gửi thông báo cho Manager
        Notification notification = notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                receipt.getId(),
                receipt.getCreatedBy().getId(), // Giả sử createdBy là Manager
                NotificationType.DANG_TIEN_HANH_NHAP_KHO,
                "Phiếu yêu cầu nhập kho nội bộ #" + receipt.getId() + " đang được tiến hành"
        );

    }

    @Override
    public ImportRequestReceiptResponse createInternalImportReceipt(Long receiptId, Map<Long, Integer> actualQuantities) {
        Receipt requestReceipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found"));

//        // Kiểm tra xem tất cả các chi tiết trong requestReceipt đã có số lượng thực tế tương ứng trong actualQuantities chưa
//        for (ReceiptDetail requestDetail : requestReceipt.getDetails()) {
//            if (!actualQuantities.containsKey(requestDetail.getId()) || actualQuantities.get(requestDetail.getId()) == null) {
//                throw new IllegalArgumentException("Actual quantity for detail ID " + requestDetail.getId() + " is required");
//            }
//        }
        if (requestReceipt.getStatus() != ReceiptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Receipt is not in the approved state for processing");
        }
        requestReceipt.setStatus(ReceiptStatus.Completed);
        receiptRepository.save(requestReceipt);

        Receipt actualReceipt = Receipt.builder()
                .code(generateAndValidateUniqueCode())
                .type(ReceiptType.PHIEU_NHAP_NOI_BO)
                .status(ReceiptStatus.NOT_COMPLETED)
                .description("Actual Import based on Request Receipt #" + receiptId)
                .createdBy(getCurrentAuthenticatedUser())
                .lastModifiedBy(getCurrentAuthenticatedUser())
                .warehouse(requestReceipt.getWarehouse())
                .build();
        actualReceipt = receiptRepository.save(actualReceipt);

        boolean hasDiscrepancy = false;
        List<ImportRequestReceiptDetailResponse> detailResponses = new ArrayList<>();

        for (ReceiptDetail requestDetail : requestReceipt.getDetails()) {
            int requiredQuantity = requestDetail.getQuantity();
            //int actualQuantity = actualQuantities.get(requestDetail.getId());
            int actualQuantity = actualQuantities.getOrDefault(requestDetail.getId(), requiredQuantity);
            int discrepancyQuantity = actualQuantity - requiredQuantity;
            double unitPrice = requestDetail.getItem().getPurchasePrice().getPrice();
            double totalPriceForItem = unitPrice * actualQuantity;

            ReceiptDetail actualDetail = ReceiptDetail.builder()
                    .receipt(actualReceipt)
                    .item(requestDetail.getItem())
                    .quantity(actualQuantity)
                    .unitPrice(unitPrice)
                    .totalPrice(totalPriceForItem)
                    .unitName(requestDetail.getUnitName())
                    .build();
            actualDetail = receiptDetailRepository.save(actualDetail);
            ImportRequestReceiptDetailResponse detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, discrepancyQuantity, null);
            if (discrepancyQuantity != 0) {
                ReceiptDiscrepancyLogResponse discrepancyLog = handleDiscrepancy(actualDetail, requiredQuantity, actualQuantity, unitPrice);
                detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, discrepancyQuantity, discrepancyLog);
                hasDiscrepancy = true;
            }else {
                detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, 0, null);
            }

            detailResponses.add(detailResponse);

            updateInventoryForInbound(requestDetail.getItem(), actualQuantity, unitPrice, actualReceipt.getWarehouse().getId());
        }

        actualReceipt.setTotalQuantity(detailResponses.stream().mapToInt(ImportRequestReceiptDetailResponse::getQuantity).sum());
        actualReceipt.setTotalPrice(detailResponses.stream().mapToDouble(ImportRequestReceiptDetailResponse::getTotalPrice).sum());
        receiptRepository.save(actualReceipt);

        if (hasDiscrepancy) {
            createAndSendNotificationForReceipt(actualReceipt, true);
        }

        return buildImportRequestReceiptResponse(actualReceipt, detailResponses);
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalImportRequestReceipts() {
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_YEU_CAU_NHAP_NOI_BO);
        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                  //  response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết

                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getTotalPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportRequestReceiptResponse getInternalImportRequestReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_NOI_BO)
                .orElseThrow(() -> new NotFoundException("Receipt with ID " + id + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(receipt.getWarehouse().getId());
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setTotalPrice(receipt.getTotalPrice());
    //    response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Thêm thông tin chi tiết phiếu
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalImportRequestReceiptsByWareHouse() {
        // Get the current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Use the warehouse ID from the current user to fetch receipts
        Long warehouseId = currentUser.getWarehouse().getId();
        List<Receipt> receipts = receiptRepository.findByTypeAndWarehouseId(ReceiptType.PHIEU_YEU_CAU_NHAP_NOI_BO, warehouseId);

        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                //    response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalImportReceipts() {
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_NHAP_NOI_BO);
        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                //    response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportRequestReceiptResponse getInternalImportReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_NHAP_NOI_BO)
                .orElseThrow(() -> new NotFoundException("Receipt with ID " + id + " not found or not of type PHIEU_NHAP_NOI_BO"));

        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(receipt.getWarehouse().getId());
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setTotalPrice(receipt.getTotalPrice());
      //  response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Thêm thông tin chi tiết phiếu
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalImportReceiptsByWareHouse() {
        // Get the current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Use the warehouse ID from the current user to fetch receipts
        Long warehouseId = currentUser.getWarehouse().getId();
        List<Receipt> receipts = receiptRepository.findByTypeAndWarehouseId(ReceiptType.PHIEU_NHAP_NOI_BO, warehouseId);


        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                    //response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportRequestReceiptResponse createInternalExportRequestReceipt(ImportRequestReceiptForm internalRequestReceiptForm) {
        // Check warehouse
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        Warehouse warehouse = warehouseList.stream()
                .filter( warehouse1 -> warehouse1.getId().equals(internalRequestReceiptForm.getWarehouseId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Warehouse not found!"));


        // Check inventory staff
        List<User> inventoryStaffList = userRepository.findAllByWarehouseAndRoleName(warehouse, "INVENTORY_STAFF");
        User inventoryStaff = inventoryStaffList.stream()
                .filter(user -> user.getId().equals(internalRequestReceiptForm.getInventoryStaffId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Inventory Staff not found!"));

        // Create a new receipt
        Receipt newReceipt = Receipt.builder()
                .code(generateAndValidateUniqueCode())
                .type(ReceiptType.PHIEU_YEU_CAU_XUAT_NOI_BO)
                .status(ReceiptStatus.Pending_Approval)
                .description(internalRequestReceiptForm.getDescription())
                .createdBy(getCurrentAuthenticatedUser())
               // .receivedBy(inventoryStaff)
                .warehouse(warehouse)
                .build();
        Receipt savedReceipt = receiptRepository.save(newReceipt);

        // Calculate total quantity and total price
        int totalQuantity = 0;
        double totalPrice = 0;

        List<ReceiptDetail> receiptDetails = new ArrayList<>();

        for (ImportRequestReceiptDetailForm detailForm : internalRequestReceiptForm.getDetails()) {
            // Get product information
            Item item = itemRepository.findById(detailForm.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found"));
            double unitPrice = item.getPricing().getPrice();
            double totalPriceForItem = unitPrice * detailForm.getQuantity();
            // Create receipt detail
            ReceiptDetail receiptDetail = ReceiptDetail.builder()
                    .item(item)
                    .quantity(detailForm.getQuantity())
                    .unitName(item.getSubCategory().getUnit().getName())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPriceForItem)
                    .receipt(savedReceipt)
                    .build();
            receiptDetails.add(receiptDetail);

            totalQuantity += detailForm.getQuantity();
            totalPrice += receiptDetail.getTotalPrice();
        }
        newReceipt.setTotalQuantity(totalQuantity);
        newReceipt.setTotalPrice(totalPrice);
        // Save the receipt and receipt details
        receiptDetailRepository.saveAll(receiptDetails);

        // Send a notification
        notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.REQUESTED,
                savedReceipt.getId(),
                inventoryStaff.getId(),
                NotificationType.YEU_CAU_XUAT_KHO_NOI_BO,
                "Yêu cầu xuất kho nội bộ #" + savedReceipt.getId() + " đã được tạo."
        );

        // Create response
        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(savedReceipt.getWarehouse().getId());
        response.setId(savedReceipt.getId());
        response.setCode(savedReceipt.getCode());
        response.setType(savedReceipt.getType());
        response.setStatus(savedReceipt.getStatus());
        response.setDescription(savedReceipt.getDescription());
        //  response.setReceivedBy(savedReceipt.getReceivedBy().getLastName() + " " + savedReceipt.getReceivedBy().getMiddleName() + " " + savedReceipt.getReceivedBy().getFirstName());

        response.setCreatedBy(savedReceipt.getCreatedBy().getLastName() + " " + savedReceipt.getCreatedBy().getMiddleName() + " " + savedReceipt.getCreatedBy().getFirstName());
        response.setLastModifiedBy(savedReceipt.getLastModifiedBy() != null ? savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(savedReceipt.getCreationDate());
        response.setUpdatedAt(savedReceipt.getLastModifiedDate());
        response.setTotalQuantity(totalQuantity);
        response.setTotalPrice(totalPrice);

        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);
        return response;
    }

    @Override
    public void confirmInternalExportRequestReceipt(Long receiptId) {
        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        var receipt = receiptRepository.findById(receiptId)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_YEU_CAU_XUAT_NOI_BO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found or not of type PHIEU_YEU_CAU_XUAT_NOI_BO"));

        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
        if (currentUser == null || !currentUser.getWarehouse().equals(receipt.getWarehouse())) {
            throw new AccessDeniedException("You do not have permission to confirm this internal export request receipt.");
        }
        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
        if (currentUser == null ) {
            throw new AccessDeniedException("You do not have permission to confirm this internal export request receipt.");
        }

        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.Approved) {
            throw new IllegalStateException("This internal import request receipt has already been confirmed.");
        }
        // Cập nhật trạng thái
        receipt.setStatus(ReceiptStatus.Approved);
        var updatedReceipt = receiptRepository.save(receipt);

        // Gửi thông báo cho Manager
        Notification notification =  notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                updatedReceipt.getId(),
                updatedReceipt.getCreatedBy().getId(), // Giả sử createdBy là Manager
                NotificationType.XAC_NHAN_XUAT_KHO_NOI_BO,
                "Phiếu yêu cầu xuất kho nội bộ #" + updatedReceipt.getId() + " đã được xác nhận."
        );
    }

    @Override
    public void startInternalExportProcess(Long receiptId) {
        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with id " + receiptId +" not found"));

// So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
//        if (currentUser == null || !currentUser.getId().equals(receipt.getReceivedBy().getId())) {
//            throw new AccessDeniedException("You do not have permission to confirm this import request receipt.");
//        }
        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
        if (currentUser == null) {
            throw new AccessDeniedException("You do not have permission to confirm this import request receipt.");
        }
        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.IN_PROGRESS) {
            throw new IllegalStateException("This internal import request receipt has already been in progress.");
        }
        receipt.setStatus(ReceiptStatus.IN_PROGRESS);
        receiptRepository.save(receipt);

        // Gửi thông báo cho Manager
        Notification notification = notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                receipt.getId(),
                receipt.getCreatedBy().getId(), // Giả sử createdBy là Manager
                NotificationType.DANG_TIEN_HANH_XUAT_KHO,
                "Phiếu yêu cầu xuất kho nội bộ #" + receipt.getId() + " đang được tiến hành"
        );
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalExportRequestReceipts() {
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_YEU_CAU_XUAT_NOI_BO);
        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                //    response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết

                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getTotalPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportRequestReceiptResponse getInternalExportRequestReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_YEU_CAU_XUAT_NOI_BO)
                .orElseThrow(() -> new NotFoundException("Receipt with ID " + id + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(receipt.getWarehouse().getId());
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setTotalPrice(receipt.getTotalPrice());
        //response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Thêm thông tin chi tiết phiếu
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalExportRequestReceiptsByWareHouse() {
        // Get the current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Use the warehouse ID from the current user to fetch receipts
        Long warehouseId = currentUser.getWarehouse().getId();
        List<Receipt> receipts = receiptRepository.findByTypeAndWarehouseId(ReceiptType.PHIEU_YEU_CAU_XUAT_NOI_BO, warehouseId);

        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                //    response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalExportReceipts() {
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_XUAT_NOI_BO);
        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                   // response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportRequestReceiptResponse getInternalExportReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_XUAT_NOI_BO)
                .orElseThrow(() -> new NotFoundException("Receipt with ID " + id + " not found or not of type PHIEU_XUAT_NOI_BO"));

        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
        response.setWarehouseId(receipt.getWarehouse().getId());
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setTotalPrice(receipt.getTotalPrice());
        //response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Thêm thông tin chi tiết phiếu
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
        List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }

    @Override
    public List<ImportRequestReceiptResponse> getAllInternalExportReceiptsByWareHouse() {
        // Get the current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Use the warehouse ID from the current user to fetch receipts
        Long warehouseId = currentUser.getWarehouse().getId();
        List<Receipt> receipts = receiptRepository.findByTypeAndWarehouseId(ReceiptType.PHIEU_XUAT_NOI_BO, warehouseId);


        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                   // response.setReceivedBy(receipt.getReceivedBy() != null ? receipt.getReceivedBy().getLastName() +" " + receipt.getReceivedBy().getMiddleName()+" " + receipt.getReceivedBy().getFirstName()  : null);
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() +" " + receipt.getCreatedBy().getMiddleName()+" " + receipt.getCreatedBy().getFirstName()  : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() +" " +receipt.getLastModifiedBy().getLastName()+ " "+ receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());


                    // Thêm thông tin chi tiết
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ImportRequestReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportRequestReceiptResponse createInternalExportReceipt(Long receiptId, Map<Long, Integer> actualQuantities) {
        Receipt requestReceipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found"));

        // Kiểm tra xem tất cả các chi tiết trong requestReceipt đã có số lượng thực tế tương ứng trong actualQuantities chưa
//        for (ReceiptDetail requestDetail : requestReceipt.getDetails()) {
//            if (!actualQuantities.containsKey(requestDetail.getId()) || actualQuantities.get(requestDetail.getId()) == null) {
//                throw new IllegalArgumentException("Actual quantity for detail ID " + requestDetail.getId() + " is required");
//            }
//        }
        if (requestReceipt.getStatus() != ReceiptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Receipt is not in the approved state for processing");
        }
        requestReceipt.setStatus(ReceiptStatus.Completed);
        receiptRepository.save(requestReceipt);

        Receipt actualReceipt = Receipt.builder()
                .code(generateAndValidateUniqueCode())
                .type(ReceiptType.PHIEU_XUAT_NOI_BO)
                .status(ReceiptStatus.NOT_COMPLETED)
                .description("Actual Import based on Request Receipt #" + receiptId)
                .createdBy(getCurrentAuthenticatedUser())
                .lastModifiedBy(getCurrentAuthenticatedUser())
                .warehouse(requestReceipt.getWarehouse())
                .build();
        actualReceipt = receiptRepository.save(actualReceipt);

        boolean hasDiscrepancy = false;
        List<ImportRequestReceiptDetailResponse> detailResponses = new ArrayList<>();

        for (ReceiptDetail requestDetail : requestReceipt.getDetails()) {
            int requiredQuantity = requestDetail.getQuantity();
            //int actualQuantity = actualQuantities.get(requestDetail.getId());
            int actualQuantity = actualQuantities.getOrDefault(requestDetail.getId(), requiredQuantity);
            int discrepancyQuantity = actualQuantity - requiredQuantity;
            double unitPrice = requestDetail.getItem().getPurchasePrice().getPrice();
            double totalPriceForItem = unitPrice * actualQuantity;

            ReceiptDetail actualDetail = ReceiptDetail.builder()
                    .receipt(actualReceipt)
                    .item(requestDetail.getItem())
                    .quantity(actualQuantity)
                    .unitPrice(unitPrice)
                    .totalPrice(totalPriceForItem)
                    .unitName(requestDetail.getUnitName())
                    .build();
            actualDetail = receiptDetailRepository.save(actualDetail);
            ImportRequestReceiptDetailResponse detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, discrepancyQuantity, null);
            if (discrepancyQuantity != 0) {
                ReceiptDiscrepancyLogResponse discrepancyLog = handleDiscrepancy(actualDetail, requiredQuantity, actualQuantity, unitPrice);
                detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, discrepancyQuantity, discrepancyLog);
                hasDiscrepancy = true;
            }else {
                detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, 0, null);
            }

            detailResponses.add(detailResponse);



            updateInventoryForOutbound(requestDetail.getItem(), actualQuantity, unitPrice, actualReceipt.getWarehouse().getId());
        }

        actualReceipt.setTotalQuantity(detailResponses.stream().mapToInt(ImportRequestReceiptDetailResponse::getQuantity).sum());
        actualReceipt.setTotalPrice(detailResponses.stream().mapToDouble(ImportRequestReceiptDetailResponse::getTotalPrice).sum());
        receiptRepository.save(actualReceipt);

        if (hasDiscrepancy) {
            createAndSendNotificationForReceipt(actualReceipt, true);
        }

        return buildImportRequestReceiptResponse(actualReceipt, detailResponses);
    }

    @Override
    //@Transactional
    public ImportRequestReceiptResponse updateImportRequestReceipt(Long id, UpdateImportRequestReceipt importRequestReceiptForm) {
        // Tìm phiếu nhập kho và kiểm tra loại phiếu
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with ID " + id + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

        // Cập nhật thông tin chung của phiếu
        receipt.setDescription(importRequestReceiptForm.getDescription());

        // Tìm nạp tất cả chi tiết phiếu hiện có từ cơ sở dữ liệu
        List<ReceiptDetail> existingDetails = receiptDetailRepository.findByReceiptId(receipt.getId());

        // Xác định các chi tiết phiếu được gửi từ client
        Set<Long> formDetailIds = importRequestReceiptForm.getDetails().stream()
                .map(UpdateImportRequestReceiptDetail::getId)
                .collect(Collectors.toSet());

        // Xử lý và cập nhật các chi tiết phiếu được gửi từ client
        List<ReceiptDetail> updatedDetails = new ArrayList<>();

        for (UpdateImportRequestReceiptDetail detailForm : importRequestReceiptForm.getDetails()) {
            // Tìm nạp hoặc tạo mới chi tiết phiếu dựa trên ID
            ReceiptDetail detail = existingDetails.stream()
                    .filter(d -> d.getId().equals(detailForm.getId()))
                    .findFirst()
                    .orElse(new ReceiptDetail());

            detail.setReceipt(receipt);
            detail.setItem(itemRepository.findById(detailForm.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found")));
            detail.setQuantity(detailForm.getQuantity());
            detail.setUnitName(unitRepository.findById(detailForm.getUnitId())
                    .orElseThrow(() -> new NotFoundException("Unit not found")).getName());

            // Check and update PurchasePrice
            PurchasePrice existingPrice = purchasePriceRepository.findByItem(detail.getItem());
            double oldPrice = (existingPrice != null) ? existingPrice.getPrice() : 0.0;

            if (existingPrice != null) {
                if (existingPrice.getPrice() != detailForm.getUnitPrice()) {
                    // Update price and create audit record
                    existingPrice.setPrice(detailForm.getUnitPrice());
                    purchasePriceRepository.save(existingPrice);

                    PurchasePriceAudit priceAudit = PurchasePriceAudit.builder()
                            .changedBy(getCurrentAuthenticatedUser())
                            .changeDate(new Date())
                            .oldPrice(oldPrice)
                            .newPrice(detailForm.getUnitPrice())
                            .purchasePrice(existingPrice)
                            .build();
                    purchasePriceAuditRepository.save(priceAudit);
                }
                //detail.setPurchasePrice(existingPrice);
            } else {
                // Create new PurchasePrice and PurchasePriceAudit
                PurchasePrice newPrice = PurchasePrice.builder()
                        .item(detail.getItem())
                        .price(detailForm.getUnitPrice())
                        .effectiveDate(new Date())
                        .build();
                purchasePriceRepository.save(newPrice);

                PurchasePriceAudit priceAudit = PurchasePriceAudit.builder()
                        .changedBy(getCurrentAuthenticatedUser())
                        .changeDate(new Date())
                        .oldPrice(0.0)
                        .newPrice(detailForm.getUnitPrice())
                        .purchasePrice(newPrice)
                        .build();
                purchasePriceAuditRepository.save(priceAudit);
               // detail.setPurchasePrice(newPrice);
            }

            detail.setTotalPrice(detailForm.getUnitPrice() * detailForm.getQuantity());
            updatedDetails.add(detail);
        }

        // Xóa các chi tiết phiếu cũ không còn tồn tại trong request cập nhật
        List<ReceiptDetail> detailsToRemove = existingDetails.stream()
                .filter(detail -> !formDetailIds.contains(detail.getId()))
                .collect(Collectors.toList());
        receiptDetailRepository.deleteAll(detailsToRemove);

        // Lưu danh sách chi tiết phiếu đã cập nhật
        updatedDetails = receiptDetailRepository.saveAll(updatedDetails);

        // Tính lại totalPrice và totalQuantity
        double totalPrice = updatedDetails.stream().mapToDouble(detail -> detail.getTotalPrice()).sum();
        int totalQuantity = updatedDetails.stream().mapToInt(ReceiptDetail::getQuantity).sum();

        // Cập nhật lại totalPrice và totalQuantity cho phiếu
        receipt.setTotalPrice(totalPrice);
        receipt.setTotalQuantity(totalQuantity);
        receiptRepository.save(receipt);

        // Tạo và trả về response
        ImportRequestReceiptResponse response = new ImportRequestReceiptResponse();

        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
     //   response.setReceivedBy(receipt.getReceivedBy().getLastName() + " " + receipt.getReceivedBy().getMiddleName() + " " + receipt.getReceivedBy().getFirstName());
        response.setCreatedBy(receipt.getCreatedBy().getLastName() + " " + receipt.getCreatedBy().getMiddleName() + " " + receipt.getCreatedBy().getFirstName());
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());
        response.setTotalQuantity(totalQuantity);
        response.setTotalPrice(totalPrice);

        List<ImportRequestReceiptDetailResponse> detailResponses = updatedDetails.stream()
                .map(detail -> {
                    ImportRequestReceiptDetailResponse detailResponse = new ImportRequestReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    return detailResponse;
                })
                .collect(Collectors.toList());

        response.setDetails(detailResponses);

        return response;
    }


    private User getCurrentAuthenticatedUser() {
        // Logic to get the current authenticated user
        return userRepository.findById(((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public static String generateUniqueCode() {
        // Ví dụ: tạo mã dựa trên thời gian hiện tại và một số ngẫu nhiên
        return "RCPT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    private boolean isCodeExist(String code) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM Receipt r WHERE r.code = :code", Long.class)
                .setParameter("code", code)
                .getSingleResult();
        return count > 0;
    }

    private String generateAndValidateUniqueCode() {
        String code;
        do {
            code = generateUniqueCode();
        } while (isCodeExist(code));
        return code;
    }

@Override
@Transactional
public ImportRequestReceiptResponse createImportReceipt(Long receiptId, Map<Long, Integer> actualQuantities) {
    Receipt requestReceipt = receiptRepository.findById(receiptId)
            .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found"));

    // Kiểm tra xem tất cả các chi tiết trong requestReceipt đã có số lượng thực tế tương ứng trong actualQuantities chưa
    for (ReceiptDetail requestDetail : requestReceipt.getDetails()) {
        if (!actualQuantities.containsKey(requestDetail.getId()) || actualQuantities.get(requestDetail.getId()) == null) {
            throw new IllegalArgumentException("Actual quantity for detail ID " + requestDetail.getId() + " is required");
        }
    }
    requestReceipt.setStatus(ReceiptStatus.Completed);
    receiptRepository.save(requestReceipt);

    Receipt actualReceipt = Receipt.builder()
            .code(generateAndValidateUniqueCode())
            .type(ReceiptType.PHIEU_NHAP_KHO)
            .status(ReceiptStatus.NOT_COMPLETED)
            .description("Actual Import based on Request Receipt #" + receiptId)
            .createdBy(getCurrentAuthenticatedUser())
            .lastModifiedBy(getCurrentAuthenticatedUser())
            .warehouse(requestReceipt.getWarehouse())
            .build();
    actualReceipt = receiptRepository.save(actualReceipt);

    boolean hasDiscrepancy = false;
    List<ImportRequestReceiptDetailResponse> detailResponses = new ArrayList<>();

    for (ReceiptDetail requestDetail : requestReceipt.getDetails()) {
        int requiredQuantity = requestDetail.getQuantity();
        int actualQuantity = actualQuantities.get(requestDetail.getId());
        //int actualQuantity = actualQuantities.getOrDefault(requestDetail.getId(), requiredQuantity);
        int discrepancyQuantity = actualQuantity - requiredQuantity;
        double unitPrice = requestDetail.getItem().getPurchasePrice().getPrice();
        double totalPriceForItem = unitPrice * actualQuantity;

        ReceiptDetail actualDetail = ReceiptDetail.builder()
                .receipt(actualReceipt)
                .item(requestDetail.getItem())
                .quantity(actualQuantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPriceForItem)
                .unitName(requestDetail.getUnitName())
                .build();
        actualDetail = receiptDetailRepository.save(actualDetail);
        ImportRequestReceiptDetailResponse detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, discrepancyQuantity, null);
        if (discrepancyQuantity != 0) {
            ReceiptDiscrepancyLogResponse discrepancyLog = handleDiscrepancy(actualDetail, requiredQuantity, actualQuantity, unitPrice);
            detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, discrepancyQuantity, discrepancyLog);
            hasDiscrepancy = true;
        }else {
            detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, 0, null);
        }

        detailResponses.add(detailResponse);

        updateInventoryForInbound(requestDetail.getItem(), actualQuantity, unitPrice, actualReceipt.getWarehouse().getId());
    }

    actualReceipt.setTotalQuantity(detailResponses.stream().mapToInt(ImportRequestReceiptDetailResponse::getQuantity).sum());
    actualReceipt.setTotalPrice(detailResponses.stream().mapToDouble(ImportRequestReceiptDetailResponse::getTotalPrice).sum());
    receiptRepository.save(actualReceipt);



    if (hasDiscrepancy) {
        createAndSendNotificationForReceipt(actualReceipt, true);
    }

    return buildImportRequestReceiptResponse(actualReceipt, detailResponses);
}



    @Override
    @Transactional
public ExportReceiptResponse createExportReceipt(Long receiptId, Map<Long, Integer> actualQuantities) {
    CustomerRequestReceipt customerRequestReceipt = customerRequestReceiptRepository.findById(receiptId)
            .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found"));

    if (customerRequestReceipt.getStatus() != CustomerRequestReceiptStatus.IN_PROGRESS) {
        throw new IllegalStateException("Receipt is not in the approved state for processing");
    }

    customerRequestReceipt.setStatus(CustomerRequestReceiptStatus.Completed);
    customerRequestReceiptRepository.save(customerRequestReceipt);

    Receipt actualReceipt = Receipt.builder()
            .code(generateAndValidateUniqueCode())
            .customerRequestReceipt(customerRequestReceipt)
            .type(ReceiptType.PHIEU_XUAT_KHO)
            .status(ReceiptStatus.NOT_COMPLETED)
            .description("Actual Export based on Customer Request Receipt #" + receiptId)
            .createdBy(getCurrentAuthenticatedUser())
            .lastModifiedBy(getCurrentAuthenticatedUser())
            .totalQuantity(customerRequestReceipt.getTotalQuantity())
            .warehouse(customerRequestReceipt.getWarehouse())
            .build();
    Receipt savedExportReceipt = receiptRepository.save(actualReceipt);

    List<ExportReceiptDetailResponse> detailResponses = new ArrayList<>();
    double totalExportPrice = 0.0;

    for (CustomerRequestReceiptDetail requestDetail : customerRequestReceipt.getCustomerRequestReceiptDetailList()) {
        Item item = requestDetail.getItems();
        int requestedQuantity = requestDetail.getQuantity();
        int actualQuantity = actualQuantities.getOrDefault(item.getId(), requestedQuantity);

        // Kiểm tra nếu actualQuantity lớn hơn totalQuantity trong Inventory
        Inventory inventory = inventoryRepository.findByItemAndWarehouse(item, savedExportReceipt.getWarehouse())
                .orElseThrow(() -> new NotFoundException("Inventory not found"));
        if (actualQuantity > inventory.getTotalQuantity()) {
            throw new NotFoundException("Actual quantity is greater than total quantity in Inventory for item with ID " + item.getId());
        }

        double unitPrice = item.getPricing().getPrice();
        double totalPriceForItem = unitPrice * actualQuantity;

        ReceiptDetail receiptDetail = ReceiptDetail.builder()
                .receipt(savedExportReceipt)
                .item(item)
                .quantity(actualQuantity)
                .unitName(requestDetail.getUnitName())
                .totalPrice(totalPriceForItem)
                .build();
        receiptDetailRepository.save(receiptDetail);

        totalExportPrice += totalPriceForItem;

        ExportReceiptDetailResponse detailResponse = ExportReceiptDetailResponse.builder()
                .id(receiptDetail.getId())
                .item(InfoItemDTO.builder()
                        .id(item.getId())
                        .brandName(item.getBrand().getName())
                        .originName(item.getOrigin().getName())
                        .subcategoryName(item.getSubCategory().getName())
                        .supplierName(item.getSupplier().getName())
                        .code(item.getCode())
                        .imageUrl(item.getSubCategory().getImages().get(0).getUrl())
                        .build())
                .quantity(actualQuantity)
                .unitName(receiptDetail.getUnitName())
                .price(unitPrice)
                .totalPrice(totalPriceForItem)
                .build();
        detailResponses.add(detailResponse);



        //updateInventoryForOutbound(item, actualQuantity, unitPrice, customerRequestReceipt.getLastModifiedBy().getWarehouse().getId());

        updateInventoryForOutbound(item, actualQuantity, unitPrice, savedExportReceipt.getWarehouse().getId());
    }

    savedExportReceipt.setTotalPrice(totalExportPrice);
    savedExportReceipt.setTotalQuantity(actualQuantities.values().stream().mapToInt(Integer::intValue).sum());
    receiptRepository.save(savedExportReceipt);

    return ExportReceiptResponse.builder()
            .warehouseId(savedExportReceipt.getWarehouse().getId())
            .id(savedExportReceipt.getId())
            .code(savedExportReceipt.getCode())
            .type(savedExportReceipt.getType())
            .createdBy(savedExportReceipt.getCreatedBy().getLastName() + " " + savedExportReceipt.getCreatedBy().getMiddleName() + " " + savedExportReceipt.getCreatedBy().getFirstName())
            .lastModifiedBy(savedExportReceipt.getLastModifiedBy() != null ? savedExportReceipt.getLastModifiedBy().getLastName() + " " + savedExportReceipt.getLastModifiedBy().getLastName() + " " + savedExportReceipt.getLastModifiedBy().getFirstName() : null)
            .createdAt(savedExportReceipt.getCreationDate())
            .updatedAt(savedExportReceipt.getLastModifiedDate())
            .status(savedExportReceipt.getStatus())
            .totalPrice(savedExportReceipt.getTotalPrice())
            .totalQuantity(savedExportReceipt.getTotalQuantity())
            .description(savedExportReceipt.getDescription())
            .details(detailResponses)
            .build();
}


    @Override
    @Transactional
    public ImportRequestReceiptResponse updateImportReceipt(Long receiptId, Map<Long, Integer> actualQuantities) {
        Receipt existingReceipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found"));

        if (existingReceipt.getStatus() != ReceiptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Receipt is not in a state that allows updating");
        }

        boolean hasDiscrepancy = false;
        double totalUpdatedPrice = 0;
        int totalUpdatedQuantity = 0;
        List<ImportRequestReceiptDetailResponse> detailResponses = new ArrayList<>();

        for (ReceiptDetail detail : existingReceipt.getDetails()) {
            int requiredQuantity = detail.getQuantity();
            int actualQuantity = actualQuantities.getOrDefault(detail.getItem().getId(), requiredQuantity);
            int discrepancyQuantity = actualQuantity - requiredQuantity;
            double unitPrice = detail.getItem().getPurchasePrice().getPrice();
            double totalPriceForItem = unitPrice * actualQuantity;
            detail.setQuantity(actualQuantity);
            detail.setUnitPrice(unitPrice);
            detail.setTotalPrice(totalPriceForItem);
            receiptDetailRepository.save(detail);

            totalUpdatedPrice += totalPriceForItem;
            totalUpdatedQuantity += actualQuantity;

            ImportRequestReceiptDetailResponse detailResponse;
            if (discrepancyQuantity != 0) {
                ReceiptDiscrepancyLogResponse discrepancyLog = handleDiscrepancy(detail, requiredQuantity, actualQuantity, detail.getUnitPrice());
                detailResponse = buildImportRequestReceiptDetailResponse(detail, actualQuantity, discrepancyQuantity, discrepancyLog);
                hasDiscrepancy = true;
            } else {
                detailResponse = buildImportRequestReceiptDetailResponse(detail, actualQuantity, 0, null);
            }
            detailResponses.add(detailResponse);
            updateInventoryForInbound(detail.getItem(),actualQuantity, unitPrice,  existingReceipt.getWarehouse().getId());

        }

        existingReceipt.setTotalPrice(totalUpdatedPrice);
        existingReceipt.setTotalQuantity(totalUpdatedQuantity);
        receiptRepository.save(existingReceipt);

        // Send notification if needed
        createAndSendNotificationForReceipt(existingReceipt, hasDiscrepancy);

        // Build and return response
        return buildImportRequestReceiptResponse(existingReceipt, detailResponses);
    }

    @Override
    public List<ExportReceiptResponse> getAllExportReceipts() {
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_XUAT_KHO);
        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ExportReceiptResponse response = new ExportReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() + " " + receipt.getCreatedBy().getMiddleName() + " " + receipt.getCreatedBy().getFirstName() : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());

                    // Add details
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ExportReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ExportReceiptDetailResponse detailResponse = new ExportReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                // Uncomment and adjust if you have a price field in the detail
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());

                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ExportReceiptResponse> getAllExportReceiptsByWareHouse() {
        // Get the current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Use the warehouse ID from the current user to fetch receipts
        Long warehouseId = currentUser.getWarehouse().getId();
        List<Receipt> receipts = receiptRepository.findByTypeAndWarehouseId(ReceiptType.PHIEU_XUAT_KHO, warehouseId);

        return receipts.stream()
                .sorted(Comparator.comparing(Receipt::getCreationDate).reversed())
                .map(receipt -> {
                    ExportReceiptResponse response = new ExportReceiptResponse();
                    response.setWarehouseId(receipt.getWarehouse().getId());
                    response.setId(receipt.getId());
                    response.setCode(receipt.getCode());
                    response.setType(receipt.getType());
                    response.setStatus(receipt.getStatus());
                    response.setDescription(receipt.getDescription());
                    response.setTotalQuantity(receipt.getTotalQuantity());
                    response.setTotalPrice(receipt.getTotalPrice());
                    response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() + " " + receipt.getCreatedBy().getMiddleName() + " " + receipt.getCreatedBy().getFirstName() : null);
                    response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getFirstName() : null);
                    response.setCreatedAt(receipt.getCreationDate());
                    response.setUpdatedAt(receipt.getLastModifiedDate());

                    // Add details
                    List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
                    List<ExportReceiptDetailResponse> detailResponses = receiptDetails.stream()
                            .map(detail -> {
                                ExportReceiptDetailResponse detailResponse = new ExportReceiptDetailResponse();
                                detailResponse.setId(detail.getId());
                                detailResponse.setItem(InfoItemDTO.builder()
                                        .id(detail.getItem().getId())
                                        .brandName(detail.getItem().getBrand().getName())
                                        .originName(detail.getItem().getOrigin().getName())
                                        .subcategoryName(detail.getItem().getSubCategory().getName())
                                        .supplierName(detail.getItem().getSupplier().getName())
                                        .code(detail.getItem().getCode())
                                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                                        .build());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                // Uncomment and adjust if you have a price field in the detail
                                detailResponse.setPrice(detail.getUnitPrice());
                                detailResponse.setTotalPrice(detail.getTotalPrice());

                                return detailResponse;
                            })
                            .collect(Collectors.toList());

                    response.setDetails(detailResponses);
                    return response;
                })
                .collect(Collectors.toList());

    }


    @Override
    @Transactional
    public CheckInventoryReceiptResponse createCheckInventoryReceipt(CheckInventoryReceiptForm checkInventoryReceiptForm) {
        User currentUser = getCurrentAuthenticatedUser();
        Warehouse warehouse = currentUser.getWarehouse();

        Receipt checkInventoryReceipt = Receipt.builder()
                .code(generateAndValidateUniqueCode())
                .type(ReceiptType.PHIEU_KIEM_KHO)
                .status(ReceiptStatus.Pending_Approval)
                .description(checkInventoryReceiptForm.getDescription())
                .createdBy(currentUser)
                .warehouse(warehouse)
                .build();

        checkInventoryReceipt = receiptRepository.save(checkInventoryReceipt);

        Map<Long, InventoryDiscrepancyLogs> discrepancyLogsMap = new HashMap<>();

        boolean hasDiscrepancy = false;

        for (InventoryCheckDetail detail : checkInventoryReceiptForm.getDetails()) {
            Item item = itemRepository.findById(detail.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found with ID: " + detail.getItemId()));


            int totalLocationQuantity = 0;

            List<LocationQuantityDetail> locationQuantities = detail.getLocationQuantities();
            Iterator<LocationQuantityDetail> iterator = locationQuantities.iterator();


            while (iterator.hasNext()) {
                LocationQuantityDetail locationQuantityDetail = iterator.next();
                Location location = locationRepository.findById(locationQuantityDetail.getLocationId())
                        .orElseThrow(() -> new NotFoundException("Location not found with ID: " + locationQuantityDetail.getLocationId()));

                // Cập nhật số lượng mới cho location từ form, ngay cả khi nó là 0
                location.setItem_quantity(locationQuantityDetail.getQuantity());
                locationRepository.save(location);

                // Nếu số lượng mới là 0, xóa location
                if (locationQuantityDetail.getQuantity() == 0) {
                    location.setItem(null);
                    locationRepository.save(location);
                    // Sử dụng phương thức tùy chỉnh để xóa location
                   // locationRepository.deleteLocationById(locationQuantityDetail.getLocationId());
                   // iterator.remove();
                } else {


                    totalLocationQuantity += locationQuantityDetail.getQuantity();
                }
            }

            if (totalLocationQuantity != detail.getActualQuantity()) {
                throw new QuantityExceedsInventoryException("The total quantity at locations does not match the actual quantity for item with ID: " + item.getId());
            }

            // Logic to handle inventory discrepancy
            Inventory inventory = inventoryRepository.findByItemAndWarehouse(item, warehouse)
                    .orElseThrow(() -> new NotFoundException("Inventory not found for item with ID: " + item.getId()));

            InventoryDiscrepancyLogs log = handleInventoryDiscrepancy(detail, inventory, hasDiscrepancy);
            if (log != null) {
                discrepancyLogsMap.put(detail.getItemId(), log);
            }
        }

        if (hasDiscrepancy) {
            createAndSendNotificationForInventoryCheck(checkInventoryReceipt, hasDiscrepancy);
        }

        return buildCheckInventoryReceiptResponse(checkInventoryReceipt, checkInventoryReceiptForm.getDetails(), discrepancyLogsMap);
    }
    private void validateStatusQuantities(Map<InventoryStatus, Integer> statusQuantities, int actualQuantity) throws InvalidInventoryDataException {
        if (statusQuantities == null) {
            throw new InvalidInventoryDataException("Status quantities cannot be null");
        }

        int totalStatusQuantity = 0;
        for (Map.Entry<InventoryStatus, Integer> entry : statusQuantities.entrySet()) {
            if (entry.getValue() < 0) {
                throw new InvalidInventoryDataException("Quantities for " + entry.getKey() + " cannot be negative");
            }
            totalStatusQuantity += entry.getValue();
        }

        if (totalStatusQuantity > actualQuantity) {
            throw new InvalidInventoryDataException("Total status quantities exceed actual quantity");
        }
    }
//    private void handleInventoryDiscrepancy(InventoryCheckDetail detail, Inventory inventory, boolean hasDiscrepancy) {
//        int actualQuantity = detail.getActualQuantity();
//        Map<InventoryStatus, Integer> statusQuantities = detail.getStatusQuantities();
//        // Lưu trữ số lượng dự kiến trước khi xử lý chênh lệch
//        int expectedQuantity = inventory.getTotalQuantity();
//
//        // Validate dữ liệu trước khi xử lý
//        validateStatusQuantities(statusQuantities, actualQuantity);
//
//        // Tính toán discrepancyQuantity và discrepancyValue
//        int discrepancyQuantity = actualQuantity - inventory.getAvailable();
//
//        double discrepancyValue = calculateDiscrepancyValue(inventory.getItem().getId(), inventory.getWarehouse().getId(), inventory,statusQuantities);
//
//        // Nếu có sự chênh lệch, đánh dấu hasDiscrepancy và tạo log
//        if (discrepancyQuantity != 0 || !statusQuantities.isEmpty()) {
//            hasDiscrepancy = true;
//            InventoryDiscrepancyLogs log = InventoryDiscrepancyLogs.builder()
//                    .inventory(inventory)
//                    .requiredQuantity(expectedQuantity)
//                    .actualQuantity(actualQuantity)
//                    .discrepancyQuantity(discrepancyQuantity)
//                    .discrepancyValue(discrepancyValue)
//                    .logTime(new Date())
//                    .note(detail.getNote())
//                    .build();
//
//            inventoryDiscrepancyLogsRepository.save(log);
//        }
//
//        // Cập nhật số lượng tồn kho bằng cách sử dụng thông tin từ statusQuantities
//        updateInventoryQuantity(inventory, statusQuantities);
//    }
    private InventoryDiscrepancyLogs  handleInventoryDiscrepancy(InventoryCheckDetail detail, Inventory inventory, boolean hasDiscrepancy) {
        int actualQuantity = detail.getActualQuantity();
        Map<InventoryStatus, Integer> statusQuantities = detail.getStatusQuantities();
        // Lưu trữ số lượng dự kiến trước khi xử lý chênh lệch
        int expectedQuantity = inventory.getTotalQuantity();

        // Validate dữ liệu trước khi xử lý
        validateStatusQuantities(statusQuantities, actualQuantity);

        // Tính toán discrepancyQuantity và discrepancyValue
        int discrepancyQuantity = actualQuantity - inventory.getAvailable();

        double discrepancyValue = calculateDiscrepancyValue(inventory.getItem().getId(), inventory.getWarehouse().getId(), inventory,statusQuantities);

        // Nếu có sự chênh lệch, đánh dấu hasDiscrepancy và tạo log
        InventoryDiscrepancyLogs log = null;
        if (discrepancyQuantity != 0 || !statusQuantities.isEmpty()) {
            hasDiscrepancy = true;
             log = InventoryDiscrepancyLogs.builder()
                    .inventory(inventory)
                    .requiredQuantity(expectedQuantity)
                    .actualQuantity(actualQuantity)
                    .discrepancyQuantity(discrepancyQuantity)
                    .discrepancyValue(discrepancyValue)
                    .logTime(new Date())
                    .note(detail.getNote())
                    .build();

            inventoryDiscrepancyLogsRepository.save(log);
        }

        // Cập nhật số lượng tồn kho bằng cách sử dụng thông tin từ statusQuantities
        updateInventoryQuantity(inventory, statusQuantities);

        return log;
    }
    private double calculateDiscrepancyValue(Long itemId, Long warehouseId, Inventory inventory, Map<InventoryStatus, Integer> statusQuantities) {
        double discrepancyValue = 0.0;
        double unitValue = getAverageUnitValueOfItemInWarehouse(itemId, warehouseId);

        for (Map.Entry<InventoryStatus, Integer> entry : statusQuantities.entrySet()) {
            switch (entry.getKey()) {
                case LOST:
                    // Giảm giá trị chênh lệch cho hàng bị mất
                    discrepancyValue -= entry.getValue() * unitValue;
                    break;
                case DEFECTIVE:
                    // Không thay đổi giá trị chênh lệch cho hàng hư hỏng
                    break;
                case REDUNDANT:
                    // Tăng giá trị chênh lệch cho hàng thừa
                    discrepancyValue += entry.getValue() * unitValue;
                    break;
                case ENOUGH:
                    // Không thay đổi giá trị chênh lệch
                    break;
                // Các trường hợp khác nếu có
            }
        }
        return discrepancyValue;
    }


    private CheckInventoryReceiptResponse buildCheckInventoryReceiptResponse(Receipt receipt, List<InventoryCheckDetail> checkDetails, Map<Long, InventoryDiscrepancyLogs> discrepancyLogsMap) {
        List<InventoryCheckDetailResponse> detailResponses = new ArrayList<>();

        for (InventoryCheckDetail detail : checkDetails) {
            // Lấy ra item dựa trên itemId
            Item item = itemRepository.findById(detail.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found with ID: " + detail.getItemId()));
            Long warehouseId = receipt.getWarehouse().getId();

            // Tìm Inventory dựa trên itemId và warehouseId
            Inventory inventory = inventoryRepository.findByItemIdAndWarehouseId(detail.getItemId(), warehouseId)
                    .orElseThrow(() -> new NotFoundException("Inventory not found for item with ID: " + detail.getItemId()));



            // Lấy InventoryDiscrepancyLogs từ map
            InventoryDiscrepancyLogs latestLog = discrepancyLogsMap.get(detail.getItemId());


            // Tạo danh sách LocationQuantityResponse dựa trên danh sách location của item
            List<LocationQuantityResponse> locationQuantityResponses = getLocationQuantityResponsesForItem(inventory.getItem());

            // Tạo và thiết lập các thông tin cho InventoryCheckDetailResponse
            InventoryCheckDetailResponse detailResponse = new InventoryCheckDetailResponse();
            detailResponse.setItemId(item.getId());
            detailResponse.setCodeItem(item.getCode());
            detailResponse.setItemName(item.getSubCategory().getName());
            detailResponse.setExpectedQuantity(latestLog.getRequiredQuantity());
            detailResponse.setActualQuantity(latestLog.getActualQuantity());
            detailResponse.setDiscrepancyQuantity(latestLog.getDiscrepancyQuantity());
            detailResponse.setDiscrepancyValue(latestLog.getDiscrepancyValue());
            detailResponse.setNote(detail.getNote());
            detailResponse.setLocations(locationQuantityResponses);
            // ... Xử lý và thiết lập locationQuantityResponses

            detailResponses.add(detailResponse);
        }

        // Tạo và trả về CheckInventoryReceiptResponse
        return new CheckInventoryReceiptResponse(
                receipt.getWarehouse().getId(),
                receipt.getId(),
                receipt.getCode(),
                receipt.getType(),
                receipt.getStatus(),
                receipt.getDescription(),
                formatFullName(receipt.getCreatedBy()),
                receipt.getLastModifiedBy() != null ? formatFullName(receipt.getLastModifiedBy()) : null,
                receipt.getCreationDate(),
                receipt.getLastModifiedDate(),
                detailResponses
        );
    }

    private void updateInventoryQuantity(Inventory inventory, Map<InventoryStatus, Integer> statusQuantities) {
        int lostQuantity = statusQuantities.getOrDefault(InventoryStatus.LOST, 0);
        int redundantQuantity = statusQuantities.getOrDefault(InventoryStatus.REDUNDANT, 0);
        int defectiveQuantity = statusQuantities.getOrDefault(InventoryStatus.DEFECTIVE, 0);

        // Cập nhật số lượng dựa trên LOST và DEFECTIVE, và cập nhật giá trị tồn kho
        inventory.setAvailable(inventory.getAvailable() - lostQuantity - defectiveQuantity + redundantQuantity);
        inventory.setTotalQuantity(inventory.getTotalQuantity() - lostQuantity + redundantQuantity);

        // Điều chỉnh giá trị tồn kho
        inventory.setTotalValue(inventory.getTotalValue() - (lostQuantity * getAverageUnitValueOfItemInWarehouse(inventory.getItem().getId(), inventory.getWarehouse().getId()))
                + (redundantQuantity * getAverageUnitValueOfItemInWarehouse(inventory.getItem().getId(), inventory.getWarehouse().getId())));

        // Cập nhật số lượng hàng mất và hư hỏng
        inventory.setLost(inventory.getLost() + lostQuantity);
        inventory.setDefective(inventory.getDefective() + defectiveQuantity);

        inventoryRepository.save(inventory);
    }

    // Đoạn mã giả định để lấy số lượng tổng cộng mong đợi từ bảng inventory
    private int getExpectedQuantityBeforeCheck(Long itemId, Long warehouseId) {
        // Giả định rằng có một phương thức trong repository để lấy Inventory dựa trên itemId và warehouse

        Optional<Inventory> inventory = inventoryRepository.findByItemIdAndWarehouseId(itemId, warehouseId);
        // Nếu Inventory tồn tại, trả về totalQuantity, nếu không trả về 0
        return inventory.map(Inventory::getTotalQuantity).orElse(0);
    }




    public double getAverageUnitValueOfItemInWarehouse(Long itemId, Long warehouseId) {
        // Tìm kiếm Inventory dựa trên itemId và warehouseId
        Optional<Inventory> inventory = inventoryRepository.findByItemIdAndWarehouseId(itemId, warehouseId);

        // Nếu Inventory tìm thấy, trả về averageUnitValue
        return inventory.map(Inventory::getAverageUnitValue).orElse(0.0);
    }
    @Override
    public List<CheckInventoryReceiptResponse> getAllCheckInventoryReceipts() {

        User currentUser = getCurrentAuthenticatedUser();
        List<Receipt> receipts = receiptRepository.findByType(ReceiptType.PHIEU_KIEM_KHO);
        List<CheckInventoryReceiptResponse> responses = new ArrayList<>();

        for (Receipt receipt : receipts) {


            if (currentUser == null || (!currentUser.getRole().getName().equals("MANAGER") && !currentUser.getId().equals(receipt.getCreatedBy().getId()))) {
                throw new AccessDeniedException("You do not have permission to view this inventory check receipt.");
            }

            List<Inventory> inventories = inventoryRepository.findByWarehouseId(receipt.getWarehouse().getId());
            List<InventoryCheckDetailResponse> detailResponses = new ArrayList<>();

            for (Inventory inventory : inventories) {
                Item item = inventory.getItem();
                List<InventoryDiscrepancyLogs> logsList = inventory.getDiscrepancyLogs();

                // Tìm log gần nhất phù hợp với thời gian của receipt
                InventoryDiscrepancyLogs latestLog = logsList.stream()
                        .filter(log -> log.getLogTime().compareTo(receipt.getCreationDate()) <= 0)
                        .max(Comparator.comparing(InventoryDiscrepancyLogs::getLogTime))
                        .orElse(null);

                List<LocationQuantityResponse> locationQuantityResponses = getLocationQuantityResponsesForItem(item);

                InventoryCheckDetailResponse detailResponse = new InventoryCheckDetailResponse();
                detailResponse.setItemId(item.getId());
                detailResponse.setCodeItem(item.getCode());
                detailResponse.setItemName(item.getSubCategory().getName());
                detailResponse.setExpectedQuantity(inventory.getAvailable());
                detailResponse.setActualQuantity(latestLog != null ? latestLog.getActualQuantity() : 0);
                detailResponse.setDiscrepancyQuantity(latestLog != null ? latestLog.getDiscrepancyQuantity() : 0);
                detailResponse.setDiscrepancyValue(latestLog != null ? latestLog.getDiscrepancyValue() : 0);
                detailResponse.setNote(latestLog != null ? latestLog.getNote() : "");
                detailResponse.setLocations(locationQuantityResponses);

                detailResponses.add(detailResponse);
            }

            CheckInventoryReceiptResponse checkInventoryReceiptResponse = new CheckInventoryReceiptResponse(
                    receipt.getWarehouse().getId(),
                    receipt.getId(),
                    receipt.getCode(),
                    receipt.getType(),
                    receipt.getStatus(),
                    receipt.getDescription(),
                    formatFullName(receipt.getCreatedBy()),
                    receipt.getLastModifiedBy() != null ? formatFullName(receipt.getLastModifiedBy()) : null,
                    receipt.getCreationDate(),
                    receipt.getLastModifiedDate(),
                    detailResponses
            );

            responses.add(checkInventoryReceiptResponse);
        }

        return responses;
    }




    @Override
    public CheckInventoryReceiptResponse getCheckInventoryReceiptById(Long receiptId) {
        User currentUser = getCurrentAuthenticatedUser();
        Receipt receipt = receiptRepository.findById(receiptId)
                .filter(r -> r.getType() == ReceiptType.PHIEU_KIEM_KHO)
                .orElseThrow(() -> new NotFoundException("Inventory Check Receipt with ID " + receiptId + " not found or not of type PHIEU_KIEM_KHO"));

        if (currentUser == null || (!currentUser.getRole().getName().equals("MANAGER") && !currentUser.getId().equals(receipt.getCreatedBy().getId()))) {
            throw new AccessDeniedException("You do not have permission to view this inventory check receipt.");
        }

        List<Inventory> inventories = inventoryRepository.findByWarehouseId(receipt.getWarehouse().getId());
        List<InventoryCheckDetailResponse> detailResponses = new ArrayList<>();

        for (Inventory inventory : inventories) {
            Item item = inventory.getItem();
            List<InventoryDiscrepancyLogs> logsList = inventory.getDiscrepancyLogs();

            // Tìm log gần nhất phù hợp với thời gian của receipt
            InventoryDiscrepancyLogs latestLog = logsList.stream()
                    .filter(log -> log.getLogTime().compareTo(receipt.getCreationDate()) <= 0)
                    .max(Comparator.comparing(InventoryDiscrepancyLogs::getLogTime))
                    .orElse(null);

            List<LocationQuantityResponse> locationQuantityResponses = getLocationQuantityResponsesForItem(item);

            InventoryCheckDetailResponse detailResponse = new InventoryCheckDetailResponse();
            detailResponse.setItemId(item.getId());
            detailResponse.setCodeItem(item.getCode());
            detailResponse.setItemName(item.getSubCategory().getName());
            detailResponse.setExpectedQuantity(inventory.getAvailable()); // Lấy số lượng dự kiến từ inventory
            detailResponse.setActualQuantity(latestLog != null ? latestLog.getActualQuantity() : 0);
            detailResponse.setDiscrepancyQuantity(latestLog != null ? latestLog.getDiscrepancyQuantity() : 0);
            detailResponse.setDiscrepancyValue(latestLog != null ? latestLog.getDiscrepancyValue() : 0);
            detailResponse.setNote(latestLog != null ? latestLog.getNote() : "");
            detailResponse.setLocations(locationQuantityResponses);

            detailResponses.add(detailResponse);
        }

        CheckInventoryReceiptResponse checkInventoryReceiptResponse = new CheckInventoryReceiptResponse(
                receipt.getWarehouse().getId(),
                receipt.getId(),
                receipt.getCode(),
                receipt.getType(),
                receipt.getStatus(),
                receipt.getDescription(),
                formatFullName(receipt.getCreatedBy()),
                receipt.getLastModifiedBy() != null ? formatFullName(receipt.getLastModifiedBy()) : null,
                receipt.getCreationDate(),
                receipt.getLastModifiedDate(),
                detailResponses
        );

        return checkInventoryReceiptResponse;
    }



//    @Override
//    public CheckInventoryReceiptResponse getCheckInventoryReceiptById(Long receiptId) {
//
//        // Lấy thông tin người dùng hiện tại
//        User currentUser = getCurrentAuthenticatedUser();
//        Receipt receipt = receiptRepository.findById(receiptId)
//                .filter(r -> r.getType() == ReceiptType.PHIEU_KIEM_KHO)
//                .orElseThrow(() -> new NotFoundException("Inventory Check Receipt with ID " + receiptId + " not found or not of type PHIEU_KIEM_KHO"));
//        // So sánh id của người dùng hiện tại với inventoryStaffId liên quan đến phiếu nhập kho
//        if (currentUser == null || (!currentUser.getRole().getName().equals("MANAGER") && !currentUser.getId().equals(receipt.getCreatedBy().getId()))) {
//            throw new AccessDeniedException("You do not have permission to view this inventory check receipt.");
//        }
//            List<Inventory> inventories = inventoryRepository.findByWarehouse(receipt.getWarehouse());
//            List<InventoryCheckDetailResponse> detailResponses = new ArrayList<>();
//
//            for (Inventory inventory : inventories) {
//
//                InventoryCheckDetailResponse detailResponse = new InventoryCheckDetailResponse();
//                detailResponse.setItemId(inventory.getItem().getId());
//                detailResponse.setCodeItem(inventory.getItem().getCode());
//                detailResponse.setItemName(inventory.getItem().getSubCategory().getName());
//                detailResponse.setExpectedQuantity(inventory.getAvailable());
//                detailResponse.setActualQuantity(0); // Thiết lập giá trị mặc định cho actualQuantity
//                detailResponse.setDiscrepancyQuantity(0); // Thiết lập giá trị mặc định cho discrepancyQuantity
//                detailResponse.setDiscrepancyValue(0); // Thiết lập giá trị mặc định cho discrepancyValue
//                detailResponse.setNote(""); // Thiết lập giá trị mặc định cho note
//
//                // Lấy danh sách các location của item và tạo các LocationQuantityResponse
//                List<LocationQuantityResponse> locationQuantityResponses = getLocationQuantityResponsesForItem(inventory.getItem());
//                detailResponse.setLocations(locationQuantityResponses);
//
//                detailResponses.add(detailResponse);
//            }
//
//
//            CheckInventoryReceiptResponse checkInventoryReceiptResponse = new CheckInventoryReceiptResponse(
//                    receipt.getWarehouse().getId(),
//                    receipt.getId(),
//                    receipt.getCode(),
//                    receipt.getType(),
//                    receipt.getStatus(),
//                    receipt.getDescription(),
//                    formatFullName(receipt.getCreatedBy()),
//                    receipt.getLastModifiedBy() != null ? formatFullName(receipt.getLastModifiedBy()) : null,
//                   // receipt.getReceivedBy() != null ? formatFullName(receipt.getReceivedBy()) : null,
//                    receipt.getCreationDate(),
//                    receipt.getLastModifiedDate(),
//                    detailResponses
//            );
//
//        return checkInventoryReceiptResponse;
//    }

    @Override
    public void confirmCheckingInventoryReceipt(Long receiptId) {
        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        var receipt = receiptRepository.findById(receiptId)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_KIEM_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found or not of type PHIEU_KIEM_KHO"));

        // So sánh id của người dùng hiện tại với manager liên quan đến phiếu nhập kho
        if (currentUser == null || !currentUser.getRole().getName().equals("MANAGER")) {
            throw new AccessDeniedException("You do not have permission to confirm this Checking Inventory  receipt.");
        }

        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.Completed) {
            throw new IllegalStateException("This Checking Inventory  receipt has already been confirmed.");
        }
        // Cập nhật trạng thái
        receipt.setStatus(ReceiptStatus.Completed);
        var updatedReceipt = receiptRepository.save(receipt);

        // Gửi thông báo cho inventory_staff
        Notification notification =  notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                updatedReceipt.getId(),
                updatedReceipt.getCreatedBy().getId(), // Giả sử createdBy là Manager
                NotificationType.XAC_NHAN_KIEM_KHO,
                "Phiếu kiểm kho #" + updatedReceipt.getId() + " đã được manager xác nhận."
        );
    }

    @Override
    public void cancelImportRequestReceipt(Long receiptId) {
        // Lấy thông tin người dùng hiện tại
        User currentUser = getCurrentAuthenticatedUser();

        var receipt = receiptRepository.findById(receiptId)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

        // So sánh id của người dùng hiện tại với manager liên quan đến phiếu nhập kho
        if (currentUser == null || !currentUser.getRole().getName().equals("MANAGER")) {
            throw new AccessDeniedException("You do not have permission to confirm this Checking Inventory  receipt.");
        }

        // Kiểm tra xem phiếu đã được xác nhận chưa
        if (receipt.getStatus() == ReceiptStatus.Canceled) {
            throw new IllegalStateException("This import request  receipt has already been canceled.");
        }

        // Cập nhật trạng thái
        receipt.setStatus(ReceiptStatus.Canceled);
        var updatedReceipt = receiptRepository.save(receipt);

        // Gửi thông báo cho inventory_staff
        Notification notification =  notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.CONFIRMED,
                updatedReceipt.getId(),
                updatedReceipt.getLastModifiedBy().getId(),
                NotificationType.HUY_NHAP_KHO,
                "Phiếu yêu cầu nhập kho #" + updatedReceipt.getId() + " đã được manager hủy yêu cầu."
        );
    }



    private List<LocationQuantityResponse> getLocationQuantityResponsesForItem(Item item) {
        // Lấy ra tất cả locations từ item và loại bỏ những cái có quantity bằng 0
        return item.getLocations().stream()
                .filter(location -> location.getItem_quantity() > 0) // Chỉ lấy những location có quantity lớn hơn 0
                .map(location -> new LocationQuantityResponse(
                        location.getId(),
                        location.getShelfNumber(),
                        location.getBinNumber(),
                        location.getItem_quantity()
                        // Bạn có thể cần thêm các trường khác từ Location entity vào đây nếu cần
                ))
                .collect(Collectors.toList());
    }

    private String formatFullName(User user) {
        if (user != null) {
            return String.format("%s %s %s", user.getLastName(), user.getMiddleName(), user.getFirstName()).trim();
        } else {
            return null;
        }
    }



    @Override
    @Transactional
    public ExportReceiptResponse getExportReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .filter(r -> r.getType() == ReceiptType.PHIEU_XUAT_KHO)
                .orElseThrow(() -> new NotFoundException("Export Receipt with ID " + id + " not found or not of type PHIEU_XUAT_KHO"));

        ExportReceiptResponse response = new ExportReceiptResponse();
        response.setWarehouseId(receipt.getWarehouse().getId());
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setType(receipt.getType());
        response.setStatus(receipt.getStatus());
        response.setDescription(receipt.getDescription());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setTotalPrice(receipt.getTotalPrice());
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() + " " + receipt.getCreatedBy().getMiddleName() + " " + receipt.getCreatedBy().getFirstName() : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Adding receipt details
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.findByReceiptId(receipt.getId());
        List<ExportReceiptDetailResponse> detailResponses = receiptDetails.stream()
                .map(detail -> {
                    ExportReceiptDetailResponse detailResponse = new ExportReceiptDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setItem(InfoItemDTO.builder()
                            .id(detail.getItem().getId())
                            .brandName(detail.getItem().getBrand().getName())
                            .originName(detail.getItem().getOrigin().getName())
                            .subcategoryName(detail.getItem().getSubCategory().getName())
                            .supplierName(detail.getItem().getSupplier().getName())
                            .code(detail.getItem().getCode())
                            .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                            .build());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getUnitPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }


    private void createAndSendNotificationForReceipt(Receipt receipt, boolean hasDiscrepancy) {
        String message = hasDiscrepancy ?
                "Phiếu nhập kho #" + receipt.getId() + " đã được xử lý với sự chênh lệch." :
                "Phiếu nhập kho #" + receipt.getId() + " đã được xử lý thành công.";

            notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                hasDiscrepancy ? EventType.UPDATED : EventType.CONFIRMED,
                receipt.getId(),
                receipt.getCreatedBy().getId(),
                hasDiscrepancy ? NotificationType.XAC_NHAN_NHAP_KHO : NotificationType.XAC_NHAN_NHAP_KHO,
                message
        );
    }

    private void createAndSendNotificationForInventoryCheck( Receipt receipt, boolean hasDiscrepancy) {
        String message = hasDiscrepancy ?
                "Phiếu kiểm kho #" + receipt.getId() + " có sự chênh lệch và cần được xem xét." :
                "Phiếu kiểm kho #" + receipt.getId() + " không có sự chênh lệch.";

        // Giả sử rằng phương thức createAndSendNotification đã được cập nhật để nhận User như người nhận
        List<User> managers = userRepository.findUserByRole_Name("MANAGER");
        if (!managers.isEmpty()) {
            Long userId = managers.get(0).getId();
            notificationService.createAndSendNotification(
                    SourceType.INVENTORY,
                    hasDiscrepancy ? EventType.DISCREPANCY : EventType.NO_DISCREPANCY,
                    receipt.getId(),
                    userId,
                    hasDiscrepancy ? NotificationType.DISCREPANCY_FOUND : NotificationType.NO_DISCREPANCY_FOUND,
                    message
            );
        }else {
            throw new NotFoundException("manager not found");
        }

    }

    private ReceiptDiscrepancyLogResponse convertToReceiptDiscrepancyLogResponse(ReceiptDiscrepancyLog log) {
        return new ReceiptDiscrepancyLogResponse(
                log.getId(),
                log.getRequiredQuantity(),
                log.getActualQuantity(),
                log.getDiscrepancyQuantity(),
                log.getDiscrepancyValue(),

                log.getLogTime()
        );
    }

    private ReceiptDiscrepancyLogResponse handleDiscrepancy(ReceiptDetail detail, int requiredQuantity, int actualQuantity, double unitPrice) {
        ReceiptDiscrepancyLog discrepancyLog = new ReceiptDiscrepancyLog();
        discrepancyLog.setReceiptDetail(detail);
        discrepancyLog.setRequiredQuantity(requiredQuantity);
        discrepancyLog.setActualQuantity(actualQuantity);
        discrepancyLog.setDiscrepancyQuantity(actualQuantity - requiredQuantity);
        discrepancyLog.setDiscrepancyValue((actualQuantity - requiredQuantity) * unitPrice);
        discrepancyLog.setLogTime(new Date());
        ReceiptDiscrepancyLog savedLog = inventoryDiscrepancyLogRepository.save(discrepancyLog);

        return convertToReceiptDiscrepancyLogResponse(savedLog);
    }







    private ImportRequestReceiptDetailResponse buildImportRequestReceiptDetailResponse(
            ReceiptDetail detail, int actualQuantity, int discrepancyQuantity, ReceiptDiscrepancyLogResponse discrepancyLogResponse) {

        return ImportRequestReceiptDetailResponse.builder()
                .id(detail.getId())
                .item(InfoItemDTO.builder()
                        .id(detail.getItem().getId())
                        .brandName(detail.getItem().getBrand().getName())
                        .originName(detail.getItem().getOrigin().getName())
                        .subcategoryName(detail.getItem().getSubCategory().getName())
                        .supplierName(detail.getItem().getSupplier().getName())
                        .code(detail.getItem().getCode())
                        .imageUrl(detail.getItem().getSubCategory().getImages().get(0).getUrl())
                        .build())
                .quantity(actualQuantity)
                .unitName(detail.getUnitName())
                .price(detail.getUnitPrice())
                .totalPrice(detail.getUnitPrice() * actualQuantity)
                .discrepancyQuantity(discrepancyQuantity)
                .discrepancyLogs(discrepancyLogResponse != null ? Arrays.asList(discrepancyLogResponse) : null)
                .build();
    }



    private ImportRequestReceiptResponse buildImportRequestReceiptResponse(Receipt receipt, List<ImportRequestReceiptDetailResponse> detailResponses) {
        return ImportRequestReceiptResponse.builder()
                .warehouseId(receipt.getWarehouse().getId())
                .id(receipt.getId())
                .code(receipt.getCode())
                .type(receipt.getType())
                .status(receipt.getStatus())
                .description(receipt.getDescription())
                .createdBy(receipt.getCreatedBy().getLastName() + " " + receipt.getCreatedBy().getFirstName()) // hoặc bất kỳ định dạng nào bạn chọn
                .lastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getFirstName() : null)
                .createdAt(receipt.getCreationDate())
                .updatedAt(receipt.getLastModifiedDate())
                .totalQuantity(receipt.getTotalQuantity())
                .totalPrice(receipt.getTotalPrice())
                .details(detailResponses)
                .build();
    }

    private InternalRequestReceiptResponse buildInternalImportReceiptResponse(Receipt receipt, List<InternalRequestReceiptDetailResponse> detailResponses) {
        return InternalRequestReceiptResponse.builder()
                .warehouseId(receipt.getWarehouse().getId())
                .id(receipt.getId())
                .code(receipt.getCode())
                .type(receipt.getType())
                .status(receipt.getStatus())
                .description(receipt.getDescription())
                .createdBy(receipt.getCreatedBy().getLastName() + " " + receipt.getCreatedBy().getFirstName()) // hoặc bất kỳ định dạng nào bạn chọn
                .lastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getFirstName() : null)
                .createdAt(receipt.getCreationDate())
                .updatedAt(receipt.getLastModifiedDate())
                .totalQuantity(receipt.getTotalQuantity())
                .totalPrice(receipt.getTotalPrice())
                .details(detailResponses)
                .build();
    }





    private void updateInventoryForOutbound(Item item, int quantity, double unitPrice, Long warehouseId) {

        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new NotFoundException("Warehouse not found"));
        // Tìm kiếm Inventory hoặc tạo mới nếu không tồn tại
        Optional<Inventory> optionalInventory = inventoryRepository.findByItemAndWarehouse(item, warehouse);
        Inventory inventory;

        if (optionalInventory.isPresent()) {
            inventory = optionalInventory.get();
        } else {

            inventory = new Inventory();
            inventory.setItem(item);
            inventory.setWarehouse(warehouseRepository.findById(warehouseId).orElse(null));

        }

        // Cập nhật thông tin inventory
        int currentOutboundQuantity = inventory.getOutboundQuantity();
        double currentOutboundValue = inventory.getOutboundValue();
        double currentTotalValue = inventory.getTotalValue();
        double currentAverageUnitValue = inventory.getAverageUnitValue();

        // Cập nhật outboundQuantity, outboundValue và totalValue
        inventory.setOutboundQuantity(currentOutboundQuantity + quantity);
        inventory.setOutboundValue(currentOutboundValue + (unitPrice * quantity));
        inventory.setAvailable(inventory.getAvailable() - quantity);
        inventory.setTotalQuantity(inventory.getTotalQuantity() - quantity);
        inventory.setTotalValue(currentTotalValue - (unitPrice * quantity));
        inventory.setAverageUnitValue(inventory.getTotalValue() / inventory.getTotalQuantity());


        // Lưu thông tin inventory vào cơ sở dữ liệu
        inventoryRepository.save(inventory);

        int currentQuantity = item.getQuantity(); // Assuming there is a getQuantity method in Item
        int currentAvaible = item.getAvailable();
        item.setQuantity(currentQuantity - quantity); // Update the item's quantity
        item.setAvailable(currentAvaible - item.getAvailable());
        itemRepository.save(item); // Save the updated item

    }


    private void updateInventoryForInbound(Item item, int quantity, double unitPrice, Long warehouseId) {

        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new NotFoundException("Warehouse not found"));
        // Tìm kiếm Inventory hoặc tạo mới nếu không tồn tại
        Optional<Inventory> optionalInventory = inventoryRepository.findByItemAndWarehouse(item, warehouse);
        Inventory inventory;

        if (optionalInventory.isPresent()) {
            inventory = optionalInventory.get();
        } else {

            inventory = new Inventory();
            inventory.setItem(item);
            inventory.setWarehouse(warehouseRepository.findById(warehouseId).orElse(null));

        }

        // Cập nhật thông tin inventory
        int currentInboundQuantity = inventory.getInboundQuantity();
        double currentInboundValue = inventory.getInboundValue();
        double currentTotalValue = inventory.getTotalValue();

        // Cập nhật outboundQuantity, outboundValue và totalValue
        inventory.setInboundQuantity(currentInboundQuantity + quantity);
        inventory.setInboundValue(currentInboundValue + (unitPrice * quantity));
        inventory.setAvailable(inventory.getAvailable() + quantity);
        inventory.setTotalQuantity(inventory.getTotalQuantity() + quantity);
        inventory.setTotalValue(currentTotalValue + (unitPrice * quantity));
        inventory.setAverageUnitValue(inventory.getTotalValue() / inventory.getTotalQuantity());
        // Lưu thông tin inventory vào cơ sở dữ liệu
        inventoryRepository.save(inventory);

        // Updating the total quantity of the item
        int currentQuantity = item.getQuantity(); // Assuming there is a getQuantity method in Item
        int currentAvaible = item.getAvailable();
        item.setQuantity(currentQuantity + quantity); // Update the item's quantity
        item.setStatus(ItemStatus.Active);
        item.setAvailable(currentAvaible + quantity);
        itemRepository.save(item); // Save the updated item
    }



}
