package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.EventType;
import com.example.sparepartsinventorymanagement.entities.NotificationType;
import com.example.sparepartsinventorymanagement.entities.SourceType;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;


    private Long userId;


    private Long sourceId;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    private NotificationType type;


    private boolean seen;


    private boolean trash;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;

    private String content;

    @Enumerated(EnumType.STRING)

    private EventType eventType;
}
