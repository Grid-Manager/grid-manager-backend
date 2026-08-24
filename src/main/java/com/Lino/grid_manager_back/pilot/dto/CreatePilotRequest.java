package com.Lino.grid_manager_back.pilot.dto;

import java.util.Set;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePilotRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull @Min(18) @Max(60) Integer age,
        @NotNull @Positive Long pilotNumber,
        @NotNull Long categoryId,
        @NotBlank @Size(max = 100) String licenseNumber,
        Set<Long> seasonIds) {}
