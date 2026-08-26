package com.Lino.grid_manager_back.result.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.Lino.grid_manager_back.result.dto.CreateResultRequest;
import com.Lino.grid_manager_back.result.dto.ResultResponse;
import com.Lino.grid_manager_back.result.dto.UpdateResultRequest;
import com.Lino.grid_manager_back.result.entity.Result;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResultMapper {
    @Mapping(target = "id", ignore = true) @Mapping(target = "points", ignore = true)
    @Mapping(target = "pilot", ignore = true) @Mapping(target = "race", ignore = true) Result toEntity(CreateResultRequest request);
    @Mapping(target = "pilotId", source = "pilot.id") @Mapping(target = "raceId", source = "race.id") ResultResponse toResponse(Result result);
    @Mapping(target = "id", ignore = true) @Mapping(target = "points", ignore = true)
    @Mapping(target = "pilot", ignore = true) @Mapping(target = "race", ignore = true)
    void update(UpdateResultRequest request, @MappingTarget Result result);
}
