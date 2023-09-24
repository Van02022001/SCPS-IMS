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
@Table(name = "image" )
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 1024)
    private String name;

    @Column(name = "title", length = 2048)
    private String title;

    @Column(name = "description", length = 4096)
    private String description;


    @Column(name = "url", length = 2048)
    private String url;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY) // Một hình ảnh thuộc về một sản phẩm
    @JoinColumn(name = "product_id")  // Tên trường khóa ngoại trong bảng Image
    private Product product;      // Sản phẩm liên quan đến hình ảnh


    @OneToOne(mappedBy = "image")
    private User user;

}
