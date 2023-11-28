package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ImportRequestReceiptDetailForm;
import com.example.sparepartsinventorymanagement.dto.request.ImportRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateImportRequestReceipt;
import com.example.sparepartsinventorymanagement.dto.request.UpdateImportRequestReceiptDetail;

import com.example.sparepartsinventorymanagement.dto.response.ImportRequestReceiptDetailResponse;
import com.example.sparepartsinventorymanagement.dto.response.ImportRequestReceiptResponse;
import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import com.example.sparepartsinventorymanagement.service.ReceiptService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + id + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));
        receiptRepository.delete(receipt);
    }

    @Override
    public void confirmImportRequestReceipt(Long receiptId) {
        var receipt = receiptRepository.findById(receiptId)
                .filter(receipt1 -> receipt1.getType() == ReceiptType.PHIEU_YEU_CAU_NHAP_KHO)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found or not of type PHIEU_YEU_CAU_NHAP_KHO"));

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
    public void  startImportProcess(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with id " + receiptId +" not found"));
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
                                detailResponse.setItemName(detail.getItem().getSubCategory().getName());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getPurchasePrice().getPrice());
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
                                detailResponse.setItemName(detail.getItem().getSubCategory().getName());
                                detailResponse.setQuantity(detail.getQuantity());
                                detailResponse.setUnitName(detail.getUnitName());
                                detailResponse.setPrice(detail.getPurchasePrice().getPrice());
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
                    detailResponse.setItemName(detail.getItem().getSubCategory().getName());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getPurchasePrice().getPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }
    @Override

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
                    detailResponse.setItemName(detail.getItem().getSubCategory().getName());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    detailResponse.setPrice(detail.getPurchasePrice().getPrice());
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }
    @Override
    //@Transactional
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

            // Get unit information
            Unit unit = unitRepository.findById(detailForm.getUnitId())
                    .orElseThrow(() -> new NotFoundException("Unit not found"));

            // Check if there is an existing PurchasePrice for this Item
            PurchasePrice existingPrice = purchasePriceRepository.findByItem(item);

            // Capture old price for auditing
            double oldPrice = (existingPrice != null) ? existingPrice.getPrice() : 0.0;

            if (existingPrice != null) {
                if (existingPrice.getPrice() != detailForm.getUnitPrice()) {
                    // Cập nhật giá và tạo bản ghi kiểm toán
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
            } else {
                // Tạo mới PurchasePrice và PurchasePriceAudit
                PurchasePrice newPrice = PurchasePrice.builder()
                        .item(item)
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
            }



            // Create receipt detail
            ReceiptDetail receiptDetail = ReceiptDetail.builder()
                    .item(item)
                    .quantity(detailForm.getQuantity())
                    .unitName(unit.getName())
                    .purchasePrice(existingPrice)
                    .totalPrice(detailForm.getUnitPrice() * detailForm.getQuantity())
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
                    if (detail.getItem() != null && detail.getItem().getSubCategory() != null) {
                        detailResponse.setItemName(detail.getItem().getSubCategory().getName());
                    } else {
                        detailResponse.setItemName("N/A"); // Hoặc xử lý khác
                    }
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    if (detail.getPurchasePrice() != null) {
                        detailResponse.setPrice(detail.getPurchasePrice().getPrice());
                    } else {
                        detailResponse.setPrice(0.0); // Hoặc xử lý khác
                    }
                    detailResponse.setTotalPrice(detail.getTotalPrice());
                    return detailResponse;
                })
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
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
                detail.setPurchasePrice(existingPrice);
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
                detail.setPurchasePrice(newPrice);
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
                    if (detail.getItem() != null && detail.getItem().getSubCategory() != null) {
                        detailResponse.setItemName(detail.getItem().getSubCategory().getName());
                    } else {
                        detailResponse.setItemName("N/A");
                    }
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());
                    if (detail.getPurchasePrice() != null) {
                        detailResponse.setPrice(detail.getPurchasePrice().getPrice());
                    } else {
                        detailResponse.setPrice(0.0);
                    }
                    detailResponse.setTotalPrice(detail.getTotalPrice());
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
    public ImportRequestReceiptResponse createImportReceipt(Long receiptId, Map<Long, Integer> actualQuantities) {
        Receipt requestReceipt  = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with Id " + receiptId + " not found"));

        if (requestReceipt.getStatus() != ReceiptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Receipt is not in the approved state for processing");
        }


        // Create a new receipt
        Receipt actualReceipt  = Receipt.builder()
                .code(generateAndValidateUniqueCode())
                .type(ReceiptType.PHIEU_NHAP_KHO)
                .status(ReceiptStatus.Completed)
                .description("Actual Import based on Request Receipt #" + receiptId)
                .createdBy(getCurrentAuthenticatedUser())
                .lastModifiedBy(getCurrentAuthenticatedUser())
                .totalPrice(requestReceipt.getTotalPrice())
                .totalQuantity(requestReceipt.getTotalQuantity())
                .warehouse(requestReceipt.getWarehouse())
                .build();
        Receipt savedReceipt = receiptRepository.save(actualReceipt );

        boolean hasDiscrepancy = false;
        List<ImportRequestReceiptDetailResponse> detailResponses = new ArrayList<>();
        double totalActualPrice = 0;
        int totalActualQuantity = 0;

        for (ReceiptDetail requestDetail : requestReceipt.getDetails()) {
            int requiredQuantity = requestDetail.getQuantity();
            int actualQuantity = actualQuantities.getOrDefault(requestDetail.getItem().getId(), requiredQuantity);
            int discrepancyQuantity = actualQuantity - requiredQuantity;
            double actualPrice = actualQuantity * requestDetail.getPurchasePrice().getPrice();

            ReceiptDetail actualDetail = ReceiptDetail.builder()
                    .receipt(savedReceipt)
                    .item(requestDetail.getItem())
                    .quantity(actualQuantity)
                    .purchasePrice(requestDetail.getPurchasePrice())
                    .totalPrice(actualPrice)
                    .unitName(requestDetail.getUnitName())
                    .build();
            receiptDetailRepository.save(actualDetail);

            totalActualPrice += actualPrice;
            totalActualQuantity += actualQuantity;

            ImportRequestReceiptDetailResponse detailResponse;
            if (discrepancyQuantity != 0) {
                InventoryDiscrepancyLog discrepancyLog = handleDiscrepancy(actualDetail, requiredQuantity, actualQuantity, requestDetail.getPurchasePrice().getPrice());
                detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, discrepancyQuantity, discrepancyLog);
                hasDiscrepancy = true;
            } else {
                detailResponse = buildImportRequestReceiptDetailResponse(actualDetail, actualQuantity, 0, null);
            }
            detailResponses.add(detailResponse);
            updateInventoryAverageCost(requestDetail.getItem(), actualQuantity, requestDetail.getPurchasePrice().getPrice(), actualReceipt.getWarehouse().getId());
        }

        actualReceipt.setTotalPrice(totalActualPrice);
        actualReceipt.setTotalQuantity(totalActualQuantity);
        receiptRepository.save(actualReceipt);


        // Send notification
        createAndSendNotificationForReceipt(savedReceipt, hasDiscrepancy);

        // Build and return response
        return buildImportRequestReceiptResponse(savedReceipt, detailResponses);
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


    private InventoryDiscrepancyLog handleDiscrepancy(ReceiptDetail detail, int requiredQuantity, int actualQuantity, double unitPrice) {
        // Tạo một đối tượng mới của InventoryDiscrepancyLog
        InventoryDiscrepancyLog discrepancyLog = new InventoryDiscrepancyLog();

        // Thiết lập các thông tin cần thiết cho discrepancyLog
        discrepancyLog.setReceiptDetail(detail);
        discrepancyLog.setRequiredQuantity(requiredQuantity);
        discrepancyLog.setActualQuantity(actualQuantity);
        discrepancyLog.setDiscrepancyQuantity(actualQuantity - requiredQuantity);
        discrepancyLog.setDiscrepancyValue((actualQuantity - requiredQuantity) * unitPrice);
        discrepancyLog.setLogTime(new Date());

        // Lưu discrepancyLog vào cơ sở dữ liệu
        inventoryDiscrepancyLogRepository.save(discrepancyLog);

        return discrepancyLog;
    }




    private ImportRequestReceiptDetailResponse  buildImportRequestReceiptDetailResponse(ReceiptDetail detail, int actualQuantity, int  discrepancyQuantity, InventoryDiscrepancyLog discrepancyLog) {
        return ImportRequestReceiptDetailResponse.builder()
                .id(detail.getId())
                .itemName(detail.getItem().getSubCategory().getName())
                .quantity(actualQuantity)
                .unitName(detail.getUnitName())
                .price(detail.getPurchasePrice().getPrice())
                .totalPrice(detail.getPurchasePrice().getPrice() * actualQuantity)
                .discrepancyQuantity(discrepancyQuantity)
                .discrepancyLogs(detail.getDiscrepancyLogs())
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

    private Inventory updateInventoryAverageCost(Item item, int receivedQuantity, double unitPrice, Long warehouseId) {

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        Inventory inventory = inventoryRepository.findByItemAndWarehouse(item, warehouse)
                .orElseGet(() -> {
                   Inventory inventory1 = Inventory.builder()
                           .item(item)
                           .warehouse(warehouse)
                           .openingStockQuantity(0)
                           .openingStockValue(0.0)
                           .closingStockQuantity(0)
                           .closingStockValue(0.0)
                           .inboundQuantity(0)
                           .inboundQuantity(0)
                           .outboundQuantity(0)
                           .outboundValue(0)
                           .discrepancyQuantity(0)
                           .discrepancyValue(0)
                           .totalValue(0)
                           .isActive(true)
                           .build();
                   return inventory1;
                });


        // Tính toán giá trị tổng cộng mới
        double newTotalValue = inventory.getClosingStockValue() + (unitPrice * receivedQuantity);
        int newTotalQuantity = inventory.getClosingStockQuantity() + receivedQuantity;

        // Tính giá trị trung bình
        double averageCost = newTotalQuantity != 0 ? newTotalValue / newTotalQuantity : 0;

        // Cập nhật các giá trị của Inventory
        inventory.setClosingStockValue(newTotalValue);
        inventory.setClosingStockQuantity(newTotalQuantity);
        inventory.setInboundQuantity(inventory.getInboundQuantity() + receivedQuantity);
        inventory.setInboundValue(inventory.getInboundValue() + (unitPrice * receivedQuantity));
        inventory.setTotalValue(averageCost);  // Giá trị tổng cộng sau khi cập nhật
        inventoryRepository.save(inventory);


        // Updating the total quantity of the item
        int currentQuantity = item.getQuantity(); // Assuming there is a getQuantity method in Item
        item.setQuantity(currentQuantity + receivedQuantity); // Update the item's quantity
        item.setStatus(ItemStatus.Active);
        itemRepository.save(item); // Save the updated item
        return inventory;
    }





}
