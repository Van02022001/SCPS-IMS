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
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductMetaForm {
    @Schema(description = "Key of product meta", example = "Mô tả sơ lược")
    @Size(min = 1, max = 100)
    @NotBlank(message = "key not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String key;

    @Schema(description = "Product description", example = "Là sản phầm được làm từ ...")
    @NotBlank(message = "Description not null")
    @NotEmpty(message = "Required field")
    @Pattern(regexp = "^[A-Z].*", message = "The first letter must be uppercase.")
    private String description;
}
