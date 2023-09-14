package com.example.sparepartsinventorymanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;;
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
public class CreateProductForm {
    @Schema(description = "Name of product", example = "Bạc lót 220x157 Hold")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Name of product not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String name;

    @Schema(description = "Product description")
    @NotBlank(message = "Description not null")
    @NotEmpty(message = "Required field")
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String description;

    @Schema(description = "Category id")
    @NotBlank(message = "Description not null")
    @NotEmpty(message = "Required field")
    private Long categoryId;
}
