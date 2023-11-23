package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.Location;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemMovementDTO {
    private Long id;

    private int quantity;

    private String notes;
    private ItemMovementByItemDTO item;

    @Column(name = "moved_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date movedAt;

    private LocationDTO fromLocation;

    private LocationDTO toLocation;

    private ItemUserDTO movedBy;
}
