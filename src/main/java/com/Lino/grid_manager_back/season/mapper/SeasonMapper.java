package com.Lino.grid_manager_back.season.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.Lino.grid_manager_back.season.dto.CreateSeasonRequest;
import com.Lino.grid_manager_back.season.dto.SeasonResponse;
import com.Lino.grid_manager_back.season.entity.Season;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeasonMapper {
    @Mapping(target = "id", ignore = true) @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", ignore = true) @Mapping(target = "winnerPilot", ignore = true)
    @Mapping(target = "pilots", ignore = true) @Mapping(target = "races", ignore = true) Season toEntity(CreateSeasonRequest request);
    @Mapping(target = "categoryId", source = "category.id") @Mapping(target = "winnerPilotId", source = "winnerPilot.id")
    @Mapping(target = "pilotIds", expression = "java(season.getPilots().stream().map(com.Lino.grid_manager_back.pilot.entity.Pilot::getId).collect(java.util.stream.Collectors.toSet()))")
    SeasonResponse toResponse(Season season);
}
