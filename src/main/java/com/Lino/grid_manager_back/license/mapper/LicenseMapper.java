package com.Lino.grid_manager_back.license.mapper;

import com.Lino.grid_manager_back.license.dto.CreateLicenseRequest;
import com.Lino.grid_manager_back.license.dto.LicenseResponse;
import com.Lino.grid_manager_back.license.entity.License;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LicenseMapper {
  License toEntity(CreateLicenseRequest request);

  @Mapping(target = "pilotId", source = "pilot.id")
  LicenseResponse toResponse(License license);
}
