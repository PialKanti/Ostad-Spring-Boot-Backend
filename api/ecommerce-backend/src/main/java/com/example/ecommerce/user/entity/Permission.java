package com.example.ecommerce.user.entity;

import com.example.ecommerce.common.entity.BaseEntity;
import com.example.ecommerce.user.enums.PermissionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission extends BaseEntity {
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private PermissionType code;

    @ManyToMany(mappedBy = "permissions")
    @JsonBackReference
    private Set<Role> roles;
}
