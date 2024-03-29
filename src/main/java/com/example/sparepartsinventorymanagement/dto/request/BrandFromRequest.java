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
public class BrandFromRequest {
    @Schema(description = "Name of brand", example = "Sai Gon Golden")
    @Size(min = 1, max = 100, message = "Name must be at least 1 character and at most 100 characters")
    @NotBlank(message = "Name not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String name;

    @Schema(description = "Brand description", example = "Là doanh nghiệp...")
    @NotBlank(message = "Description not null")
    @NotEmpty(message = "Required field")
    @Size(min = 1, max = 200, message = "Description must be at least 1 character and at most 200 characters")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String description;
}
