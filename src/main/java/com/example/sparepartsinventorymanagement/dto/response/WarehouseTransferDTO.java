package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseTransferDTO {
    private Long id;
    private Integer quantity;
    private Date transferDate;
    private Long destinationWarehouseId;
    private Long sourceWarehouseId;
    private Long itemId;
    private Date creationDate;
    private Date lastModifiedDate;
    private Long createdByUserId;
    private Long lastModifiedByUserId;
}
