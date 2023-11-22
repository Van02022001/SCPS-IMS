package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.ReceiptStatus;
import com.example.sparepartsinventorymanagement.entities.ReceiptType;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportRequestReceiptResponse {
    private Long id;
    private String code;
    private ReceiptType type;
    private ReceiptStatus status;
    private double totalPrice;
    private int totalQuantity;
    private String description;
    private String createdBy; // ID của người tạo
    private String lastModifiedBy; // ID của người chỉnh sửa cuối cùng
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;
    private List<ImportRequestReceiptDetailResponse> details;

}
