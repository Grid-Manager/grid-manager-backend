package com.Lino.grid_manager_back.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 10) String acronym,
        @NotBlank @Size(max = 50) String propulsionType,
        @NotBlank @Size(max = 50) String vehicleType,
        @NotBlank @Size(max = 100) String tireSupplier,
        @NotBlank @Size(max = 100) String governingBody,
        @NotNull @Positive Integer horsePower,
        @NotNull @Positive Integer foundingYear,
        @NotBlank @Size(max = 100) String licensePattern) {}
