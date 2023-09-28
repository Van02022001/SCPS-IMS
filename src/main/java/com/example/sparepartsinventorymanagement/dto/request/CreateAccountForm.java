package com.example.sparepartsinventorymanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountForm {
    @Schema(name = "First Name of user", example = "Van")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String firstName;

    @Schema(name = "Middle Name of user", example = "Quang")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String middleName;
    @Schema(name = "Last Name of user", example = "Pham")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String lastName;

    @Schema(name = "Email of user", example = "quangvanpham02022001@gmail.com")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format.")
    private String email;

    @Schema(name = "Phone of user", example = "0935182029")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone format. ")
    private String phone;

    @NotBlank
    private String roleName;
}
