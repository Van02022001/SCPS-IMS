package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.ReceiptDetail;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReceiptDiscrepancyLogResponse {

    private Long id;



    private int requiredQuantity;

    private int actualQuantity;

    private int discrepancyQuantity;

    private double discrepancyValue;




    @Column(name = "log_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date logTime;

}
