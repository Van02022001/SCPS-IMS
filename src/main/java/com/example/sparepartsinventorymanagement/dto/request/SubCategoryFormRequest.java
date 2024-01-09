package com.example.sparepartsinventorymanagement.dto.request;


import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryFormRequest {
    @Schema(description = "Name of product", example = "Bạc lót 220x157 Hold")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Name of product not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String name;

    @Schema(description = "Product description", example = "Mô tả về product")
    @Size(min = 1, max = 200)
    @NotBlank(message = "Description not null")
    @NotEmpty(message = "Required field")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String description;



    @Schema(description = "List category id")
    @NotNull(message = "Required field")
    private Set<Long> categories_id;

    @Schema(description = "Unit id")
    @NotNull(message = "Required field")
    private Long unit_id;

    @Schema(description = "Length")
    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.0", message = "Length must be greater than or equal to 0.0")
    @DecimalMax(value = "1000.0", message = "Length must be less than or equal to 1000.0")
    private float length;

    @Schema(description = "Width")
    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.0", message = "Width must be greater than or equal to 0.0")
    @DecimalMax(value = "1000.0", message = "Width must be less than or equal to 1000.0")
    private float width;

    @Schema(description = "Height")
    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.0", message = "Height must be greater than or equal to 0.0")
    @DecimalMax(value = "1000.0", message = "Height must be less than or equal to 1000.0")
    private float height;

    @Schema(description = "Diameter")
    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.0", message = "Height must be greater than or equal to 0.0")
    @DecimalMax(value = "1000.0", message = "Height must be less than or equal to 1000.0")
    private float diameter;

    @Schema(description = "Unit of measurement id")
    @NotNull(message = "Unit of measurement id is required")
    private Long unit_mea_id;
}
