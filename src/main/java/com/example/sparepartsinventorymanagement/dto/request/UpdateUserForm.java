package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserForm {
    @NotNull(message = "ID người dùng không được để trống")
    @NotEmpty(message = "Required field.")
    private Long userId;
    @NotBlank(message = "Tên vai trò không được để trống")
    @NotEmpty(message = "Required field.")
    private String roleName;
    @NotEmpty(message = "Required field.")
    @Valid
    private Set<String> permissions; // IDs of permissions to be assigned
    @PositiveOrZero(message = "ID kho hàng phải là số dương hoặc bằng 0")
    private Long warehouseId; // Only for INVENTORY_STAFF
}
