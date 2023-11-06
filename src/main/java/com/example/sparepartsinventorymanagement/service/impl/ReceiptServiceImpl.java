package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ReceiptForm;
import com.example.sparepartsinventorymanagement.repository.ReceiptDetailRepository;
import com.example.sparepartsinventorymanagement.repository.ReceiptRepository;
import com.example.sparepartsinventorymanagement.service.ReceiptService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReceiptServiceImpl implements ReceiptService {
    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptDetailRepository  receiptDetailRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<?> createReceipt(ReceiptForm receiptForm) {
        return null;
    }
}
