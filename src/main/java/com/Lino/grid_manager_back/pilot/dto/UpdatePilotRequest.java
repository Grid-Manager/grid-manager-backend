package com.Lino.grid_manager_back.pilot.dto;

import java.util.Set;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdatePilotRequest(
        @Size(max = 150) String name,
        @Min(18) @Max(60) Integer age,
        Long categoryId,
        Set<Long> seasonIds) {}
