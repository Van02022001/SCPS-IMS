package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.UnitMeasurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeDTO {
    private Long id;

    private float length;

    private float width;

    private float height;

    private UnitMeasurementDTO unitMeasurement;

}
