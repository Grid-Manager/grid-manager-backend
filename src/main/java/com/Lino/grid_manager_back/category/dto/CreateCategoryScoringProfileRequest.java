package com.Lino.grid_manager_back.category.dto;

import com.Lino.grid_manager_back.domain.enums.RaceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.Set;

public record CreateCategoryScoringProfileRequest(
    Long seasonId,
    @NotNull RaceType raceType,
    @NotNull LocalDate effectiveFrom,
    @NotNull @PositiveOrZero Integer fastestLapBonus,
    boolean fastestLapRequiresTopTen,
    @NotEmpty Set<@Valid PositionPointsRequest> positionRules) {}
