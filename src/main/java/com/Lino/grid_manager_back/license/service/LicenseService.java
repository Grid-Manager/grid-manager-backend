package com.Lino.grid_manager_back.license.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.license.dto.LicenseResponse;
import com.Lino.grid_manager_back.license.mapper.LicenseMapper;
import com.Lino.grid_manager_back.license.repository.LicenseRepository;

@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final LicenseMapper mapper;

    public LicenseService(LicenseRepository licenseRepository, LicenseMapper mapper) {
        this.licenseRepository = licenseRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public LicenseResponse findById(Long id) {
        return mapper.toResponse(licenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Licen\u00e7a n\u00e3o encontrada.")));
    }
}
