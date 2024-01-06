package com.example.sparepartsinventorymanagement.dto.request;

import com.example.sparepartsinventorymanagement.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePricingRequest {
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date startDate;

    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @NotNull(message = "price cannot be null")
    private double price;
}
