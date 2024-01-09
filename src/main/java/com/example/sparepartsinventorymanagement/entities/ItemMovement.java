package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

@Table(name = "item_movement")
public class ItemMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long id;



    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "notes")
    private String notes;

    @Column(name = "moved_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date movedAt;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "from_location_id")
    private Location fromLocation;

    @ManyToOne
    @JoinColumn(name = "to_location_id")
    private Location toLocation;

    @ManyToOne
    @JoinColumn(name = "receipt_detail_id")
    private ReceiptDetail receiptDetail;

    @ManyToOne
    @JoinColumn(name = "moved_by", nullable = false)
    private User movedBy;
}
