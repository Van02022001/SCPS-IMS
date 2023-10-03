package com.example.sparepartsinventorymanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountForm {
    @Schema(name = "firstName", example = "Văn")
    @NotBlank(message = "firstName not null")
    @NotEmpty(message = "Required field.")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String firstName;

    @Schema(name = "middleName", example = "Quang")
    @NotBlank(message = "middleName not null")
    @NotEmpty(message = "Required field.")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String middleName;


    @Schema(name = "lastName", example = "Phạm")
    @NotBlank(message = "lastName not null")
    @NotEmpty(message = "Required field.")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String lastName;

    @Schema(name = "email", example = "quangvanpham02022001@gmail.com")
    @NotBlank(message = "email not null")
    @NotEmpty(message = "Required field.")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format.")
    private String email;

    @Schema(name = "phone", example = "0935182029")
    @NotBlank(message = "phone not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone format.")
    private String phone;


    @Schema(name = "roleName", example = "INVENTORY_STAFF")
    @NotBlank(message = "roleName not null")
    @NotEmpty(message = "Required field.")
    private String roleName;
}
