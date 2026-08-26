package com.Lino.grid_manager_back.result.dto;

import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateResultRequest(
        @Positive Long position,
        RaceStatusPilot raceStatusPilot,
        @PositiveOrZero Integer penalties,
        @PositiveOrZero Integer yellowFlags,
        @PositiveOrZero Integer redFlags,
        @PositiveOrZero Integer flagCount) {}
