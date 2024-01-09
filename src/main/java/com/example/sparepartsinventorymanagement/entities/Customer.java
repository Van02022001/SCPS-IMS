package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;


    @Column(name = "code", nullable =false)
    private String code;

    @Column(name = "name", nullable =false)
    private String name;

    @Column(name = "phone", nullable =false)
    private String phone;


    @Column(name="email", nullable =false)
    private String email;

    @Column(name = "tax_code", nullable =false)
    private String taxCode;


    @Column(name = "address", nullable =false)
    private String address;


    @Column(name = "status")
    private boolean status;

    @Column(name="customer_type", nullable =false)
    @Enumerated(EnumType.STRING)
    private CustomerType type;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;


    @OneToMany(mappedBy = "customer")
    private List<CustomerRequestReceipt> customerRequestReceiptList;

}
