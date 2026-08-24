package com.Lino.grid_manager_back.climate.dto;

import com.Lino.grid_manager_back.domain.enums.RainIntensity;
import com.Lino.grid_manager_back.domain.enums.TrackSurface;
import com.Lino.grid_manager_back.domain.enums.WindDirection;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateClimateRequest(
        @DecimalMin("-10.0") @DecimalMax("60.0") Double currentTemperature,
        @DecimalMin("-10.0") @DecimalMax("60.0") Double trackTemperature,
        @DecimalMin("0.0") @DecimalMax("100.0") Double gripRate,
        @PositiveOrZero Double windSpeed, WindDirection windDirection,
        @DecimalMin("0.0") @DecimalMax("100.0") Double rainChance,
        TrackSurface trackSurface, RainIntensity rainIntensity) {}
