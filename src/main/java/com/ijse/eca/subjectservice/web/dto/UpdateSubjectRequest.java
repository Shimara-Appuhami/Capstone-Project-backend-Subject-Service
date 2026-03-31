package com.ijse.eca.subjectservice.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSubjectRequest(
        @NotBlank String subjectCode,
        @NotBlank String title,
        @NotBlank String department,
        @NotBlank String semester,
        @NotNull @Min(1) Integer credits,
        @NotBlank String description
) {
}
