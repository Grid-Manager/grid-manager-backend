package com.Lino.grid_manager_back.pilot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.Lino.grid_manager_back.pilot.dto.CreatePilotRequest;
import com.Lino.grid_manager_back.pilot.dto.PilotResponse;
import com.Lino.grid_manager_back.pilot.dto.UpdatePilotRequest;
import com.Lino.grid_manager_back.pilot.entity.Pilot;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PilotMapper {
    @Mapping(target = "id", ignore = true) @Mapping(target = "category", ignore = true)
    @Mapping(target = "license", ignore = true) @Mapping(target = "seasons", ignore = true)
    @Mapping(target = "results", ignore = true) Pilot toEntity(CreatePilotRequest request);
    @Mapping(target = "categoryId", source = "category.id") @Mapping(target = "licenseId", source = "license.id")
    @Mapping(target = "seasonIds", expression = "java(pilot.getSeasons().stream().map(com.Lino.grid_manager_back.season.entity.Season::getId).collect(java.util.stream.Collectors.toSet()))")
    PilotResponse toResponse(Pilot pilot);
    @Mapping(target = "id", ignore = true) @Mapping(target = "pilotNumber", ignore = true)
    @Mapping(target = "category", ignore = true) @Mapping(target = "license", ignore = true)
    @Mapping(target = "seasons", ignore = true) @Mapping(target = "results", ignore = true)
    void update(UpdatePilotRequest request, @MappingTarget Pilot pilot);
}
