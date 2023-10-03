package com.example.sparepartsinventorymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserForm {
    private Long userId;
    private String roleName;
    private Set<String> permissions; // IDs of permissions to be assigned
    private Long warehouseId; // Only for INVENTORY_STAFF
}
