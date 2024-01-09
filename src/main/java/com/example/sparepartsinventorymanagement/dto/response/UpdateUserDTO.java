package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private Long userId;
    private String roleName;
    private Set<Permission> permissions; // IDs of permissions to be assigned
    private Long warehouseId; // Only for INVENTORY_STAFF
}
