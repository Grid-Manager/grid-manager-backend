package com.Lino.grid_manager_back.category.dto;

import com.Lino.grid_manager_back.domain.enums.RaceType;
import java.time.LocalDate;
import java.util.Set;

public record CategoryScoringProfileResponse(
    Long id,
    Long categoryId,
    RaceType raceType,
    LocalDate effectiveFrom,
    Integer fastestLapBonus,
    boolean fastestLapRequiresTopTen,
    Set<PositionPointsRequest> positionRules) {}
