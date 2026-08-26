package com.Lino.grid_manager_back.season.dto;

import java.util.Set;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateSeasonRequest(
        @Size(max = 150) String name,
        @Positive Integer year,
        Set<Long> pilotIds) {}
