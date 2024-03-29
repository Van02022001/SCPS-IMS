package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.request.UpdatePurchasePriceRequest;
import com.example.sparepartsinventorymanagement.dto.response.CreatePurchasePriceDTO;
import com.example.sparepartsinventorymanagement.dto.response.GetListPurchasePriceDTO;
import com.example.sparepartsinventorymanagement.dto.response.PurchasePriceDTO;
import com.example.sparepartsinventorymanagement.dto.response.UpdatePurchasePriceDTO;
import com.example.sparepartsinventorymanagement.entities.Item;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PurchasePriceService {
    List<GetListPurchasePriceDTO> getAllPurchasePrice();


    void createOrUpdatePurchasePrice(Item item, double unitPrice);



}
