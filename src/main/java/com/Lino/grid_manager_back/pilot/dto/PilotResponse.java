package com.Lino.grid_manager_back.pilot.dto;

import java.util.Set;
public record PilotResponse(Long id, String name, Integer age, Long pilotNumber, Long categoryId,
        Long licenseId, Set<Long> seasonIds) {}
