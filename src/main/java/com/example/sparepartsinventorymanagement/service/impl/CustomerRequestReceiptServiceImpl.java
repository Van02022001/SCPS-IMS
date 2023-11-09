package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CustomerRequestReceiptForm;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.repository.CustomerRepository;
import com.example.sparepartsinventorymanagement.repository.ItemRepository;
import com.example.sparepartsinventorymanagement.repository.UnitRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.CustomerRequestReceiptService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomerRequestReceiptServiceImpl implements CustomerRequestReceiptService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<?> createCustomerRequestReceipt(CustomerRequestReceiptForm form) {


        return null;
    }
}
