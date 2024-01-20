package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long id;

    private String code;

    private PricingDTO pricing;

    private PurchasePriceDTO purchasePrice;


    private int quantity;

    private int lost;

    private int available;

    private int defective;

    private int minStockLevel;

    private int maxStockLevel;

    private ItemUserDTO createdBy;

    private ItemUserDTO updatedBy;

    private ItemStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;

    private ItemProductDTO subCategory;

    private BrandDTO brand;

    private ItemSupplierDTO supplier;

    private OriginDTO origin;

    private List<LocationDTO> locations;
}
