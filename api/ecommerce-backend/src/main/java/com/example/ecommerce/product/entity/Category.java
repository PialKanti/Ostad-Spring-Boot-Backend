package com.example.ecommerce.product.entity;

import com.example.ecommerce.common.entity.Auditable;
import com.example.ecommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends BaseEntity implements Auditable {
    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;
}
