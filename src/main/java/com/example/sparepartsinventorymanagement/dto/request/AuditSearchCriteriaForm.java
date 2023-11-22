package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditSearchCriteriaForm {

    private Long itemId;
    private Long userId;

    @PastOrPresent(message = "Start date must be in the past or present")
    private Date startDate;

    @FutureOrPresent(message = "End date must be in the present or future")
    private Date endDate;
}
