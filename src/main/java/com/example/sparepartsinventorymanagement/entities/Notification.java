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
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Many notifications can belong to one user
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "source_type", length = 50, nullable = false)
    private String sourceType;

    @Column(name = "type", nullable = false)
    private short type;

    @Column(name = "seen", nullable = false)
    private boolean seen;

    @Column(name = "trash", nullable = false)
    private boolean trash;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

}
