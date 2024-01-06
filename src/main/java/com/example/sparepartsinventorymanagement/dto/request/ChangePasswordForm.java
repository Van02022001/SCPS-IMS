package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordForm {

    @NotBlank(message = "Mật khẩu cũ không được để trống")
    @NotNull(message = "oldPassword cannot be null")
    @NotEmpty(message = "Required field.")

    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 5, message = "Mật khẩu mới phải chứa ít nhất 5 ký tự")
    @NotNull(message = "newPassword cannot be null")
    @NotEmpty(message = "Required field.")

    private String newPassword;
    @NotEmpty(message = "Required field.")
    @NotBlank(message = "Xác nhận mật khẩu mới không được để trống")
    @NotNull(message = "confirmNewPassword cannot be null")
    private String confirmNewPassword;

}
