package com.Lino.grid_manager_back.category.dto;

public record CategoryResponse(Long id, String name, String acronym, String propulsionType,
        String vehicleType, String tireSupplier, String governingBody, Integer horsePower,
        Integer foundingYear, String licensePattern) {}
