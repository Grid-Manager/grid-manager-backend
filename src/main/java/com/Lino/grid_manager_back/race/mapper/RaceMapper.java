package com.Lino.grid_manager_back.race.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.Lino.grid_manager_back.race.dto.CreateRaceRequest;
import com.Lino.grid_manager_back.race.dto.RaceResponse;
import com.Lino.grid_manager_back.race.dto.UpdateRaceRequest;
import com.Lino.grid_manager_back.race.entity.Race;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RaceMapper {
    @Mapping(target = "id", ignore = true) @Mapping(target = "season", ignore = true)
    @Mapping(target = "raceStatus", ignore = true) @Mapping(target = "pilotFasterLap", ignore = true)
    @Mapping(target = "fastLap", ignore = true) @Mapping(target = "climate", ignore = true)
    @Mapping(target = "results", ignore = true) Race toEntity(CreateRaceRequest request);
    @Mapping(target = "seasonId", source = "season.id") @Mapping(target = "pilotFasterLapId", source = "pilotFasterLap.id") RaceResponse toResponse(Race race);
    @Mapping(target = "id", ignore = true) @Mapping(target = "season", ignore = true)
    @Mapping(target = "raceStatus", ignore = true) @Mapping(target = "pilotFasterLap", ignore = true)
    @Mapping(target = "fastLap", ignore = true) @Mapping(target = "climate", ignore = true)
    @Mapping(target = "results", ignore = true) void update(UpdateRaceRequest request, @MappingTarget Race race);
}
