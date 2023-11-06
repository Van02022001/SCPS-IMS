package com.example.sparepartsinventorymanagement.entities;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "location_tag")
public class LocationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="tag_id")
    private Long id;

    @Column(name="name", nullable=false, unique=true)
    private String name;

    // Các thuộc tính khác, nếu cần thiết

    // Định nghĩa mối quan hệ ngược lại từ Tag đến Location
    @ManyToMany(mappedBy = "tags")
    private List<Location> locations;
}
