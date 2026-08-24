package com.Lino.grid_manager_back.race.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.Lino.grid_manager_back.domain.enums.RaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateRaceRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull LocalDate realizationDate,
        @NotNull LocalDateTime startTime,
        @NotNull @Positive Integer laps,
        @NotNull @Positive Double circuitLength,
        @NotNull Long seasonId,
        @NotNull RaceType raceType) {}
