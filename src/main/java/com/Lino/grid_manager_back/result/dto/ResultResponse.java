package com.Lino.grid_manager_back.result.dto;

import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
public record ResultResponse(Long id, Long position, Integer points, RaceStatusPilot raceStatusPilot,
        Integer penalties, Integer yellowFlags, Integer redFlags, Integer flagCount, Long pilotId, Long raceId) {}
