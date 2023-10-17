package com.example.sparepartsinventorymanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseFormRequest {
    @Schema(description = "Name of warehouse", example = "Kho 1")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Name of warehouse not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String name;

    @Schema(description = "Full address", example = "123 Main Street, Quận 1, Hồ Chí Minh")
    @Size(min = 1, max = 200, message = "Address must be between 1 and 200 characters.")
    @NotBlank(message = "Address cannot be blank.")
    @Pattern(regexp = "^[\\p{L}0-9, .,-]+$", message = "Address is invalid")
    private String address;

}
