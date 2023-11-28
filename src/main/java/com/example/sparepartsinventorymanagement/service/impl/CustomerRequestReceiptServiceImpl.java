package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptDetailForm;
import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.response.CustomerRequestReceiptDTO;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.CustomerRequestReceiptService;
import com.example.sparepartsinventorymanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerRequestReceiptServiceImpl implements CustomerRequestReceiptService {


    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;


    private final ItemRepository itemRepository;


    private final UnitRepository unitRepository;
    private final PricingRepository pricingRepository;


    private final ModelMapper modelMapper;
    private final CustomerRequestReceiptRepository customerRequestReceiptRepository;
    private final CustomerRequestReceiptDetailRepository customerRequestReceiptDetailRepository;
    private final NotificationService notificationService;

    @Override
    public CustomerRequestReceiptDTO createCustomerRequestReceipt(CustomerRequestReceiptForm form) {
        Customer customer = customerRepository.findById(form.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        User manager = userRepository.findById(form.getManagerId())
                .orElseThrow(() -> new NotFoundException("Manager not found"));


        CustomerRequestReceipt requestReceipt = CustomerRequestReceipt.builder()
                .customer(customer)
                .createdRequestBy(getCurrentAuthenticatedUser())
                .approvedRequestBy(manager)
                .completedRequestBy(null)
                .note(form.getNote())
                .status(CustomerRequestReceiptStatus.Pending_Approval)
                .build();
        CustomerRequestReceipt savedReceipt = customerRequestReceiptRepository.save(requestReceipt);

        int totalQuantity = 0;
        double totalPrice = 0;
        List<CustomerRequestReceiptDetail>  requestReceiptDetails = new ArrayList<>();

        for(CustomerRequestReceiptDetailForm detailForm : form.getDetails()){
            Item item = itemRepository.findById(detailForm.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found"));

            Unit unit = unitRepository.findById(detailForm.getUnitId())
                    .orElseThrow(() -> new NotFoundException("Unit not found"));

            Pricing pricing = pricingRepository.findByItem(item);

            CustomerRequestReceiptDetail requestReceiptDetail = CustomerRequestReceiptDetail.builder()
                    .items(item)
                    .customerRequestReceipt(savedReceipt)
                    .quantity(detailForm.getQuantity())
                    .unitName(unit.getName())
                    .price(detailForm.getPrice())
                    .totalPrice(detailForm.getQuantity() * detailForm.getQuantity())
                    .build();
            requestReceiptDetails.add(requestReceiptDetail);
            totalQuantity += detailForm.getQuantity();
            totalPrice += requestReceiptDetail.getTotalPrice();
        }
        requestReceipt.setTotalPrice(totalPrice);
        requestReceipt.setTotalQuantity(totalQuantity);
        customerRequestReceiptDetailRepository.saveAll(requestReceiptDetails);

        // Send a notification
        notificationService.createAndSendNotification(
                SourceType.RECEIPT,
                EventType.REQUESTED,
                savedReceipt.getId(),
                manager.getId(),
                NotificationType.YEU_CAU_XUAT_KHO,
                "Yêu cầu xuất kho từ khách hàng #" + savedReceipt.getId() + " đã được tạo."
        );

        return null;
    }

    private User getCurrentAuthenticatedUser() {
        // Logic to get the current authenticated user
        return userRepository.findById(((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
