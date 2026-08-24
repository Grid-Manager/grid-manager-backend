package com.Lino.grid_manager_back.climate.dto;

import com.Lino.grid_manager_back.domain.enums.RainIntensity;
import com.Lino.grid_manager_back.domain.enums.TrackSurface;
import com.Lino.grid_manager_back.domain.enums.WindDirection;
public record ClimateResponse(Long id, Double currentTemperature, Double trackTemperature, Double gripRate,
        Double windSpeed, WindDirection windDirection, Double rainChance, TrackSurface trackSurface,
        RainIntensity rainIntensity, Long raceId) {}
