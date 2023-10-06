package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemFormRequest {

    private double costPrice;

    private double salePrice;

    private int quantity;

    private Long product_id;

    private Long brand_id;

    private List<Long> warehouses_id;


    private List<Long> suppliers;
}
