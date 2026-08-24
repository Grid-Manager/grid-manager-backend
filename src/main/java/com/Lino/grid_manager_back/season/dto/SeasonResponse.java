package com.Lino.grid_manager_back.season.dto;

import java.util.Set;
public record SeasonResponse(Long id, String name, Integer year, Long categoryId, Long winnerPilotId,
        Set<Long> pilotIds) {}
