package com.Lino.grid_manager_back.climate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.Lino.grid_manager_back.climate.dto.ClimateResponse;
import com.Lino.grid_manager_back.climate.dto.CreateClimateRequest;
import com.Lino.grid_manager_back.climate.dto.UpdateClimateRequest;
import com.Lino.grid_manager_back.climate.entity.Climate;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClimateMapper {
    @Mapping(target = "id", ignore = true) @Mapping(target = "race", ignore = true) Climate toEntity(CreateClimateRequest request);
    @Mapping(target = "raceId", source = "race.id") ClimateResponse toResponse(Climate climate);
    @Mapping(target = "id", ignore = true) @Mapping(target = "race", ignore = true) void update(UpdateClimateRequest request, @MappingTarget Climate climate);
}
