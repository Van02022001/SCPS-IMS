package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "receipts")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="created_by_id")
    private Long id;


    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReceiptType type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;


    @Column(name = "tax", nullable = false)
    private float tax;


    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;


    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updateBy;


    @Column(name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;


}