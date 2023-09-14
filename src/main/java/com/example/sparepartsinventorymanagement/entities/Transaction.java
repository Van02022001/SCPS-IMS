package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_order", columnList = "order_id")
})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "mode", nullable = false)
    private int mode;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Getters and setters (omitted for brevity)
}