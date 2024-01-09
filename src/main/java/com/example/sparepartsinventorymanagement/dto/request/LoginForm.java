package com.example.sparepartsinventorymanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginForm {
    @Schema(description = "input username",example = "AD0001")
    @NotNull(message = "username is required")
    @NotEmpty(message = "Required field.")
    private String username;

    @Schema(description = "input user password",example = "Admin@123")
    @Size(min = 6, max = 1000)
    @NotNull(message = "password is required")
    @NotEmpty(message = "Required field.")
    private String password;
}
