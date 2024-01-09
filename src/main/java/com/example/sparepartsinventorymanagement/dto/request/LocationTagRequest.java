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
public class LocationTagRequest {
    @Schema(description = "Name of location tag", example = "Ống")
    @Size(min = 1, max = 100)
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String name;
}
