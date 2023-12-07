package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptDetailForm;
import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerRequestReceiptDTO;
import com.example.sparepartsinventorymanagement.dto.response.CustomerRequestReceiptDetailDTO;
import com.example.sparepartsinventorymanagement.dto.response.ImportRequestReceiptDetailResponse;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.CustomerRequestReceiptService;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerRequestReceiptServiceImpl implements CustomerRequestReceiptService {


    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;


    private final ItemRepository itemRepository;


    private final UnitRepository unitRepository;
    private final PricingRepository pricingRepository;

    private final EntityManager entityManager;
    private final ModelMapper modelMapper;
    private final CustomerRequestReceiptRepository customerRequestReceiptRepository;
    private final CustomerRequestReceiptDetailRepository customerRequestReceiptDetailRepository;
    private final NotificationService notificationService;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public CustomerRequestReceiptDTO createCustomerRequestReceipt(CustomerRequestReceiptForm form) {
        Customer customer = customerRepository.findById(form.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Check warehouse
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        Warehouse warehouse = warehouseList.stream()
                .filter( warehouse1 -> warehouse1.getId().equals(form.getWarehouseId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Warehouse not found!"));


        // Check inventory staff
        List<User> inventoryStaffList = userRepository.findAllByWarehouseAndRoleName(warehouse, "INVENTORY_STAFF");
        User inventoryStaff = inventoryStaffList.stream()
                .filter(user -> user.getId().equals(form.getInventoryStaff()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Inventory Staff not found!"));


        CustomerRequestReceipt requestReceipt = CustomerRequestReceipt.builder()
                .code(generateAndValidateUniqueCode())
                .createdBy(getCurrentAuthenticatedUser())
                .customer(customer)
                .note(form.getNote())
                .status(CustomerRequestReceiptStatus.Pending_Approval)
                .warehouse(warehouse)
                .build();
        CustomerRequestReceipt savedReceipt = customerRequestReceiptRepository.save(requestReceipt);

        int totalQuantity = 0;

        List<CustomerRequestReceiptDetail>  requestReceiptDetails = new ArrayList<>();

        for(CustomerRequestReceiptDetailForm detailForm : form.getDetails()){
            Item item = itemRepository.findById(detailForm.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found"));

            // Tìm kiếm thông tin tồn kho
            Inventory inventory = inventoryRepository.findByItemAndWarehouse(item, savedReceipt.getWarehouse())
                    .orElseThrow(() -> new NotFoundException("Inventory not found"));
            int requestedQuantity = detailForm.getQuantity();
            int actualQuantity = Math.min(requestedQuantity, inventory.getTotalQuantity()); // Lấy giá trị nhỏ hơn hoặc bằng số tồn kho
            CustomerRequestReceiptDetail requestReceiptDetail = CustomerRequestReceiptDetail.builder()
                    .items(item)
                    .customerRequestReceipt(savedReceipt)
                    .quantity(actualQuantity)
                    .unitName(item.getSubCategory().getUnit().getName())
                    .createdBy(getCurrentAuthenticatedUser())
                    .lastModifiedBy(getCurrentAuthenticatedUser())
                    .creationDate(new Date())
                    .lastModifiedDate(new Date())
                    .build();
            requestReceiptDetails.add(requestReceiptDetail);
            totalQuantity += detailForm.getQuantity();

        }
        requestReceipt.setTotalQuantity(totalQuantity);
        customerRequestReceiptDetailRepository.saveAll(requestReceiptDetails);

        // Send a notification
        notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.REQUESTED,
                savedReceipt.getId(),
                inventoryStaff.getId(),
                NotificationType.YEU_CAU_XUAT_KHO,
                "Yêu cầu xuất kho từ khách hàng #" + savedReceipt.getId() + " đã được tạo."
        );

        CustomerRequestReceiptDTO customerRequestReceiptDTO = new CustomerRequestReceiptDTO();
        customerRequestReceiptDTO.setId(savedReceipt.getId());
        customerRequestReceiptDTO.setCustomerName(savedReceipt.getCustomer().getName());
        customerRequestReceiptDTO.setNote(savedReceipt.getNote());
        customerRequestReceiptDTO.setCode(savedReceipt.getCode());
        customerRequestReceiptDTO.setStatus(savedReceipt.getStatus());
        customerRequestReceiptDTO.setCreatedBy(savedReceipt.getCreatedBy().getLastName() + " " + savedReceipt.getCreatedBy().getMiddleName() + " " + savedReceipt.getCreatedBy().getFirstName());
        customerRequestReceiptDTO.setLastModifiedBy(savedReceipt.getLastModifiedBy() != null ? savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getLastName() + " " + savedReceipt.getLastModifiedBy().getFirstName() : null);
        customerRequestReceiptDTO.setCreatedAt(savedReceipt.getCreationDate());
        customerRequestReceiptDTO.setCreatedAt(savedReceipt.getCreationDate());
        customerRequestReceiptDTO.setUpdatedAt(savedReceipt.getLastModifiedDate());
        customerRequestReceiptDTO.setTotalQuantity(totalQuantity);


        List<CustomerRequestReceiptDetailDTO> detailResponses = requestReceiptDetails.stream()
                .map(detail -> {
                    CustomerRequestReceiptDetailDTO detailResponse = new CustomerRequestReceiptDetailDTO();
                    detailResponse.setId(detail.getId());
                    detailResponse.setItemId(detail.getItems().getId());
                    detailResponse.setItemName(detail.getItems().getSubCategory().getName());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitName(detail.getUnitName());

                    return detailResponse;
                })
                .collect(Collectors.toList());
        customerRequestReceiptDTO.setDetails(detailResponses);
        return customerRequestReceiptDTO;
    }
    @Override
    public void  startImportProcess(Long receiptId) {
        CustomerRequestReceipt receipt = customerRequestReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt with id " + receiptId +" not found"));
        receipt.setStatus(CustomerRequestReceiptStatus.IN_PROGRESS);
        customerRequestReceiptRepository.save(receipt);

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
    public CustomerRequestReceiptDTO getCustomerReceiptById(Long id) {
        CustomerRequestReceipt receipt = customerRequestReceiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CustomerRequestReceipt with ID " + id + " not found"));

        CustomerRequestReceiptDTO response = new CustomerRequestReceiptDTO();
        response.setId(receipt.getId());
        response.setCode(receipt.getCode());
        response.setCustomerName(receipt.getCustomer().getName());
        response.setStatus(receipt.getStatus());
        response.setNote(receipt.getNote());
        response.setTotalQuantity(receipt.getTotalQuantity());
        response.setCreatedBy(receipt.getCreatedBy() != null ? receipt.getCreatedBy().getLastName() + " " + receipt.getCreatedBy().getMiddleName() + " " + receipt.getCreatedBy().getFirstName() : null);
        response.setLastModifiedBy(receipt.getLastModifiedBy() != null ? receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getLastName() + " " + receipt.getLastModifiedBy().getFirstName() : null);
        response.setCreatedAt(receipt.getCreationDate());
        response.setUpdatedAt(receipt.getLastModifiedDate());

        // Fetch and map receipt details
        List<CustomerRequestReceiptDetail> receiptDetails = customerRequestReceiptDetailRepository.findByCustomerRequestReceiptId(receipt.getId());
        List<CustomerRequestReceiptDetailDTO> detailDTOs = receiptDetails.stream()
                .map(detail -> {
                    CustomerRequestReceiptDetailDTO detailDTO = new CustomerRequestReceiptDetailDTO();
                    detailDTO.setId(detail.getId());
                    detailDTO.setItemName(detail.getItems().getSubCategory().getName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setUnitName(detail.getUnitName());
                    return detailDTO;
                })
                .collect(Collectors.toList());
        response.setDetails(detailDTOs);

        return response;
    }
    @Override
    public List<CustomerRequestReceiptDTO> getAllCustomerRequestReceipts() {


        List<CustomerRequestReceipt> requestReceipts = customerRequestReceiptRepository.findAll();

        return requestReceipts.stream()
                .map(this::convertToCustomerRequestReceiptDTO)
                .collect(Collectors.toList());
    }

    private CustomerRequestReceiptDTO convertToCustomerRequestReceiptDTO(CustomerRequestReceipt receipt) {
        List<CustomerRequestReceiptDetail> receiptDetails = customerRequestReceiptDetailRepository.findByCustomerRequestReceiptId(receipt.getId());

        List<CustomerRequestReceiptDetailDTO> detailDTOs = receiptDetails.stream().map(this::convertToCustomerRequestReceiptDetailDTO)
                .collect(Collectors.toList());

        User createdByUser = receipt.getCreatedBy();
        User lastModifiedByUser = receipt.getLastModifiedBy();

        return new CustomerRequestReceiptDTO(
                receipt.getId(),
                receipt.getCode(),
                receipt.getCustomer().getName(),
                receipt.getStatus(),
                receipt.getNote(),
                receipt.getTotalQuantity(),
                detailDTOs,
                createdByUser != null ? createdByUser.getLastName() + " " + createdByUser.getMiddleName() + " " + createdByUser.getFirstName() : null,
                lastModifiedByUser != null ? lastModifiedByUser.getLastName() + " " + lastModifiedByUser.getLastName() + " " + lastModifiedByUser.getFirstName() : null,
                receipt.getCreationDate(),
                receipt.getLastModifiedDate()
        );
    }

    private CustomerRequestReceiptDetailDTO convertToCustomerRequestReceiptDetailDTO(CustomerRequestReceiptDetail detail) {
        return new CustomerRequestReceiptDetailDTO(
                detail.getId(),
                detail.getItems().getId(),
                detail.getItems().getSubCategory().getName(),
                detail.getQuantity(),
                detail.getUnitName()
        );
    }

    private User getCurrentAuthenticatedUser() {
        // Logic to get the current authenticated user
        return userRepository.findById(((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private String generateAndValidateUniqueCode() {
        String code;
        do {
            code = generateUniqueCode();
        } while (isCodeExist(code));
        return code;
    }
    public static String generateUniqueCode() {
        // Ví dụ: tạo mã dựa trên thời gian hiện tại và một số ngẫu nhiên
        return "CRR-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    private boolean isCodeExist(String code) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM Receipt r WHERE r.code = :code", Long.class)
                .setParameter("code", code)
                .getSingleResult();
        return count > 0;
    }
}
