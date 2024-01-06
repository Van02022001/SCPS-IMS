package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordForm {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @NotNull(message = "email cannot be null")
    private String email;
}
