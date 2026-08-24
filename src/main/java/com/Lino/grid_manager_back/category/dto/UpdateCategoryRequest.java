package com.Lino.grid_manager_back.category.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(max = 100) String name, @Size(max = 50) String propulsionType,
        @Size(max = 50) String vehicleType, @Size(max = 100) String tireSupplier,
        @Size(max = 100) String governingBody, @Positive Integer horsePower,
        @Positive Integer foundingYear, @Size(max = 100) String licensePattern) {}
