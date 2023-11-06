package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.dto.request.ReceiptDetailForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptResponse {
    private Long id;
    private String type;
    private String description;
    private Long createdBy;
    private List<ReceiptResponse> details;

}
