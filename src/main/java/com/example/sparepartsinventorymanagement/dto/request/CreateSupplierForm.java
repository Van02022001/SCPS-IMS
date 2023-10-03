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
public class CreateSupplierForm {
    @Schema(description = "Supplier code", example = "C1234")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Supplier code cannot be null")
    @NotEmpty(message = "Required field.")
    private String code;

    @Schema(description = "Supplier name", example = "John Doe")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Supplier name cannot be null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String name;

    @Schema(description = "Supplier phone number", example = "1234567890")
    @NotNull(message = "Phone number cannot be null")
    private String phone;

    @Schema(description = "Supplier email", example = "john.doe@example.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format.")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be null")
    private String email;

    @Schema(description = "Supplier tax code", example = "TC123456")
    @NotBlank(message = "Tax code cannot be null")
    @NotEmpty(message = "Required field.")
    private String taxCode;

    @Schema(description = "Supplier address", example = "123 ABC Street, City, Country")
    @Size(min = 1, max = 300)
    @NotBlank(message = "Address cannot be null")
    private String address;




}
