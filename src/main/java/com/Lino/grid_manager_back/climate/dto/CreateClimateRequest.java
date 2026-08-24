package com.Lino.grid_manager_back.climate.dto;

import com.Lino.grid_manager_back.domain.enums.RainIntensity;
import com.Lino.grid_manager_back.domain.enums.TrackSurface;
import com.Lino.grid_manager_back.domain.enums.WindDirection;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateClimateRequest(
        @NotNull @DecimalMin("-10.0") @DecimalMax("60.0") Double currentTemperature,
        @NotNull @DecimalMin("-10.0") @DecimalMax("60.0") Double trackTemperature,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double gripRate,
        @NotNull @PositiveOrZero Double windSpeed,
        @NotNull WindDirection windDirection,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double rainChance,
        @NotNull TrackSurface trackSurface,
        @NotNull RainIntensity rainIntensity,
        @NotNull Long raceId) {}
