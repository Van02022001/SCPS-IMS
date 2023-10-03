package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class CreateCustomerForm {
    @Schema(description = "Customer code", example = "C1234")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Customer code cannot be null")
    @NotEmpty(message = "Required field.")
    private String code;

    @Schema(description = "Customer name", example = "John Doe")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Customer name cannot be null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String name;

    @Schema(description = "Customer phone number", example = "1234567890")
    @NotNull(message = "Phone number cannot be null")
    private String phone;

    @Schema(description = "Customer email", example = "john.doe@example.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format.")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be null")
    private String email;

    @Schema(description = "Customer tax code", example = "TC123456")
    @NotBlank(message = "Tax code cannot be null")
    @NotEmpty(message = "Required field.")
    private String taxCode;

    @Schema(description = "Customer address", example = "123 ABC Street, City, Country")
    @Size(min = 1, max = 300)
    @NotBlank(message = "Address cannot be null")
    private String address;

    @Schema(description = "Customer type", example = "INDIVIDUAL")
    @NotNull(message = "Customer type cannot be null")
    private CustomerType type;

    @Schema(description = "Customer description", example = "Important customer for our company")
    @Size(max = 500)
    private String description;

}
