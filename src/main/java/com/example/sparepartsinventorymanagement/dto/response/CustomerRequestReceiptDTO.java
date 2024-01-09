package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.CustomerRequestReceiptStatus;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestReceiptDTO {
    private Long id;
    private String code;
    private String customerName;
    private CustomerRequestReceiptStatus status;
    private String note;
    private int totalQuantity;
    private List<CustomerRequestReceiptDetailDTO> details;
    private String createdBy; // ID của người tạo
    private String lastModifiedBy; // ID của người chỉnh sửa cuối cùng
    private String receivedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt; //
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;

}
