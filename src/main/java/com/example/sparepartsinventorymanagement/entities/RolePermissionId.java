package com.example.sparepartsinventorymanagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RolePermissionId implements Serializable {
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "permission_id")
    private Long permissionId;

    // Constructors, getters, and setters (omitted for brevity)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolePermissionId)) return false;
        RolePermissionId that = (RolePermissionId) o;
        return Objects.equals(getRoleId(), that.getRoleId()) &&
                Objects.equals(getPermissionId(), that.getPermissionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleId(), getPermissionId());
    }
}