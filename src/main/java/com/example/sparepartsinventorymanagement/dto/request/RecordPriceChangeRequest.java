package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordPriceChangeRequest {
    private Long purchasePriceId;
    private Long changedBy;
    private Date changeDate;
    private double oldPrice;
    private double newPrice;
}
