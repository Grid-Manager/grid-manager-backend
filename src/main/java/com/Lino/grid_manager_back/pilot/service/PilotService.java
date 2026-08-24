package com.Lino.grid_manager_back.pilot.service;

import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.category.repository.CategoryRepository;
import com.Lino.grid_manager_back.infrastructure.exception.DuplicateResourceException;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.license.entity.License;
import com.Lino.grid_manager_back.license.repository.LicenseRepository;
import com.Lino.grid_manager_back.pilot.dto.CreatePilotRequest;
import com.Lino.grid_manager_back.pilot.dto.PilotResponse;
import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.pilot.mapper.PilotMapper;
import com.Lino.grid_manager_back.pilot.repository.PilotRepository;
import com.Lino.grid_manager_back.season.entity.Season;
import com.Lino.grid_manager_back.season.repository.SeasonRepository;

@Service
public class PilotService {
    private final PilotRepository pilotRepository;
    private final CategoryRepository categoryRepository;
    private final LicenseRepository licenseRepository;
    private final SeasonRepository seasonRepository;
    private final PilotMapper mapper;
    private final PilotNamePolicy namePolicy;

    public PilotService(PilotRepository pilotRepository, CategoryRepository categoryRepository,
            LicenseRepository licenseRepository, SeasonRepository seasonRepository, PilotMapper mapper,
            PilotNamePolicy namePolicy) {
        this.pilotRepository = pilotRepository; this.categoryRepository = categoryRepository;
        this.licenseRepository = licenseRepository; this.seasonRepository = seasonRepository;
        this.mapper = mapper; this.namePolicy = namePolicy;
    }

    @Transactional
    public PilotResponse create(CreatePilotRequest request) {
        String name = namePolicy.normalizeAndValidate(request.name());
        if (pilotRepository.existsByNameIgnoreCase(name)) throw new DuplicateResourceException("Nome de piloto já cadastrado.");
        if (pilotRepository.existsByPilotNumber(request.pilotNumber())) throw new DuplicateResourceException("Número de piloto já cadastrado.");
        if (licenseRepository.existsByLicenseNumber(request.licenseNumber())) throw new DuplicateResourceException("Número de licença já cadastrado.");
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        if (!matchesLicensePattern(category, request.licenseNumber(), request.pilotNumber())) {
            throw new IllegalArgumentException("Número de licença não segue o padrão da categoria.");
        }
        Pilot pilot = mapper.toEntity(request);
        pilot.setName(name); pilot.setCategory(category); pilot.setLicense(new License(null, request.licenseNumber(), pilot));
        Set<Season> seasons = request.seasonIds() == null ? Set.of() : Set.copyOf(seasonRepository.findAllById(request.seasonIds()));
        if (seasons.size() != (request.seasonIds() == null ? 0 : request.seasonIds().size()) || seasons.stream().anyMatch(s -> !s.getCategory().getId().equals(category.getId()))) {
            throw new IllegalArgumentException("Temporadas devem existir e pertencer à categoria do piloto.");
        }
        pilot.setSeasons(seasons);
        return mapper.toResponse(pilotRepository.save(pilot));
    }

    private boolean matchesLicensePattern(Category category, String licenseNumber, Long pilotNumber) {
        if ("CATEGORIA-DATA-PILOTO".equals(category.getLicensePattern())) {
            String defaultPattern = "^" + Pattern.quote(category.getAcronym()) + "-\\d{8}-" + pilotNumber + "$";
            return Pattern.matches(defaultPattern, licenseNumber);
        }
        return Pattern.matches(category.getLicensePattern(), licenseNumber);
    }
}
