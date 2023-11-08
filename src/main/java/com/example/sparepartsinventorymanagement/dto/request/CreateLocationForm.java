package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.entities.LocationTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLocationForm {
    @NotBlank(message = "Shelf number is required")
    private String shelfNumber; // số kệ

    @NotBlank(message = "Bin number is required")
    private String binNumber; // số ngăn

    @NotNull(message = "Tags cannot be null")
    private List<String> tags;
}
