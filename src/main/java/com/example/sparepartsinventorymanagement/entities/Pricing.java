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
@Table(name = "pricings")
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pricing_id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATE_FORMAT)
    private Date startDate;

    @Column(name = "end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATE_FORMAT)
    private Date endDate;

    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
