package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.LocationTag;
import com.example.sparepartsinventorymanagement.entities.Warehouse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationFormRequest {
    @Schema(description = "Shelf Number of location", example = "Kệ 1")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Shelf Number not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String shelfNumber;

    @Schema(description = "Bin Number of location", example = "Ngăn 1")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Bin Number not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String binNumber;

    private Set<Long> tags_id;
}
