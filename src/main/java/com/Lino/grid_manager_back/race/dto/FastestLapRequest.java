package com.Lino.grid_manager_back.race.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FastestLapRequest(
        @NotNull Long pilotId,
        @NotBlank @Size(max = 20) String fastLap) {}
