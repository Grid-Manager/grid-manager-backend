package com.Lino.grid_manager_back.license.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLicenseRequest(@NotBlank @Size(max = 100) String licenseNumber) {}
