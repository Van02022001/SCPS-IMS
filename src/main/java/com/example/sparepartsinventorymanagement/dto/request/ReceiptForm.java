package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReceiptForm {
    private String type;
    private String description;
    private Long createdBy;
    private List<ReceiptDetailForm>  details;
}
