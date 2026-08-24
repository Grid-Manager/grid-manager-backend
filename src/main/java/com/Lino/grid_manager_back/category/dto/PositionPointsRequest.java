package com.Lino.grid_manager_back.category.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record PositionPointsRequest(
        @NotNull @Positive Integer position, @NotNull @PositiveOrZero Integer points) {}
