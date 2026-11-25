package com.Lino.grid_manager_back.race.dto;

import com.Lino.grid_manager_back.domain.enums.RaceStatus;
import com.Lino.grid_manager_back.domain.enums.RaceType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RaceResponse(
    Long id,
    String name,
    LocalDate realizationDate,
    LocalDateTime startTime,
    Integer laps,
    Double circuitLength,
    String fastLap,
    boolean hasSafetyCar,
    Integer safetyCarLaps,
    RaceStatus raceStatus,
    RaceType raceType,
    Long seasonId,
    Long pilotFasterLapId) {}
