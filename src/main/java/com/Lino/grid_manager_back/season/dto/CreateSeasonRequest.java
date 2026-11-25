package com.Lino.grid_manager_back.season.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateSeasonRequest(
    @NotBlank @Size(max = 150) String name,
    @NotNull @Positive Integer year,
    @NotNull Long categoryId,
    Set<Long> pilotIds) {}
