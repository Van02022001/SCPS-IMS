package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "sub_category")
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="subcategory_id")
    private Long id;

    @Column(name = "name", length = 75, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TINYTEXT")
    private String description;



    @Column(name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATETIME_FORMAT)
    @DateTimeFormat(pattern = DateTimeUtils.DATETIME_FORMAT)
    private Date updatedAt;

    @Column(name="status", nullable = false)
    private SubCategoryStatus status;

    @ManyToMany(mappedBy = "subCategories")
    private Set<Category> categories;

    @OneToOne
    @JoinColumn(name = "sub_category_meta_id")
    private SubCategoryMeta subCategoryMeta;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @OneToOne(mappedBy = "subCategory")
    private Size size;

    @OneToMany(mappedBy = "subCategory", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Item> items;
}