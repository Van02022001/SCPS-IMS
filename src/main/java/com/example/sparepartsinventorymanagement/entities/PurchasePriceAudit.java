package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "purchase_price_audits")
public class PurchasePriceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_price_change_id", nullable = false)
    private Long purchasePriceId;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Column(name = "change_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date changeDate;

    @Column(name = "old_price")
    private double oldPrice;

    @Column(name = "new_price")
    private double newPrice;
}
