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
public class CategoryFormRequest {
    @Schema(description = "Name of category", example = "Ron")
    @Size(min = 1, max = 100)
    @NotBlank(message = "Name of category not null")
    @NotEmpty(message = "Required field.")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String name;

    @Schema(description = "Category description", example = "Ron cao su hay còn được gọi bằng các tên khác nhau như gioăng cao su, vòng đệm cao su. Sản phẩm được làm từ cao su tự nhiên hoặc cao su tổng hợp, có khả năng làm kín các các chi tiết kỹ thuật, cách nhiệt, chống ồn, chống thấm nước, chống chảy dầu rất tốt.")
    @NotBlank(message = "Description not null")
    @NotEmpty(message = "Required field")
    @Pattern(regexp = "^[\\p{Lu}].*", message = "The first letter must be uppercase.")
    private String description;
}
