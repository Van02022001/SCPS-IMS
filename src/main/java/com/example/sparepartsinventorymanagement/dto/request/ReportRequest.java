package com.example.sparepartsinventorymanagement.dto.request;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequest {
    @FutureOrPresentYear2023(message = "Date must be in or after 2023")
    private Date date;

    @Pattern(regexp = "Ngày|Tháng|Năm", message = "Type must be 'Ngày', 'Tháng', or 'Năm'")
    private String type;
}

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureOrPresentYear2023Validator.class)
@interface FutureOrPresentYear2023 {
    String message() default "Invalid date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class FutureOrPresentYear2023Validator implements ConstraintValidator<FutureOrPresentYear2023, Date> {
    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return false;
        }

        int year = date.toInstant().atZone(java.time.ZoneId.systemDefault()).getYear();

        return year >= 2023;
    }
}