package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address; // Thêm trường address để ánh xạ quan hệ 1-1

    @OneToMany(mappedBy = "warehouse")
    private List<Item> items; // Danh sách các sản phẩm trong kho

    @OneToMany(mappedBy = "warehouse")
    private List<User> inventoryStaffs; // Người quản lý kho

    @Column(name = "name", length = 100, nullable = false)
    private String name; // Tên kho



    @ManyToMany
    @JoinTable(
            name = "warehouse_product",
            joinColumns = @JoinColumn(name = "warehouse_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products; // Danh sách sản phẩm được lưu trữ trong kho


}
