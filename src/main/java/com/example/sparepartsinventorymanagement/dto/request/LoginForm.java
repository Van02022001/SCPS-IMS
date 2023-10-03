package com.example.sparepartsinventorymanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginForm {
    @Schema(description = "input username",example = "AD0001")
    private String username;

    @Schema(description = "input user password",example = "Admin@123")
    @Size(min = 6, max = 1000)
    private String password;
}
