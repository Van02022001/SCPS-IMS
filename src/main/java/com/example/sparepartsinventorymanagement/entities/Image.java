package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY) // Một hình ảnh thuộc về một sản phẩm
    @JoinColumn(name = "product_id")  // Tên trường khóa ngoại trong bảng Image
    private Product product;      // Sản phẩm liên quan đến hình ảnh


    @OneToOne(mappedBy = "image")
    @JsonBackReference
    private User  user;

}