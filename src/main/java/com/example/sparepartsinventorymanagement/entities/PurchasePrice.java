package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "purchase_prices")
public class PurchasePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_price_id")
    private Long id;

    @Column(name = "effective_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATE_FORMAT)
    private Date effectiveDate; // Ngày giá này bắt đầu có hiệu lực

    @Column(name = "price", nullable = false)
    private double price; // Giá mua vào

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @OneToMany(mappedBy = "purchasePrice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchasePriceAudit> purchasePriceAudits;

}
