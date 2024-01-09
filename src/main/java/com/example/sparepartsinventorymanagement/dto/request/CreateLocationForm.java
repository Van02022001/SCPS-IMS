package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.LocationTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLocationForm {
    @NotBlank(message = "Shelf number is required")
    @NotNull(message = "binNumber cannot be null")
    @Size(min = 1, max = 40, message = "Shelf number must be at least 1 character and at most 60 characters")
    private String shelfNumber; // số kệ

    @NotBlank(message = "Bin number is required")
    @NotNull(message = "binNumber cannot be null")
    @Size(min = 1, max = 40, message = "Bin number must be at least 1 character and at most 40 characters")
    private String binNumber; // số ngăn

    @NotNull(message = "Tags cannot be null")
    private List<String> tags;
}
