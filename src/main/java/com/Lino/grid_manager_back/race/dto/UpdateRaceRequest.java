package com.Lino.grid_manager_back.race.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.Lino.grid_manager_back.domain.enums.RaceType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateRaceRequest(
        @Size(max = 150) String name,
        LocalDate realizationDate,
        LocalDateTime startTime,
        @Positive Integer laps,
        @Positive Double circuitLength,
        RaceType raceType,
        Boolean hasSafetyCar,
        @PositiveOrZero Integer safetyCarLaps) {}
