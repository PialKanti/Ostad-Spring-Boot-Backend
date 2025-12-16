package com.example.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PrePersist
    protected void onCreate() {
        if (this instanceof Auditable auditable) {
            LocalDateTime now = LocalDateTime.now();
            auditable.setCreatedAt(now);
            auditable.setModifiedAt(now);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this instanceof Auditable auditable) {
            auditable.setModifiedAt(LocalDateTime.now());
        }
    }
}
