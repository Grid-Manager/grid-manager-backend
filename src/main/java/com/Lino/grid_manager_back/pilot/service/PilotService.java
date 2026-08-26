package com.Lino.grid_manager_back.pilot.service;

import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.Lino.grid_manager_back.pilot.dto.UpdatePilotRequest;
import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.pilot.mapper.PilotMapper;
import com.Lino.grid_manager_back.pilot.repository.PilotRepository;
import com.Lino.grid_manager_back.season.entity.Season;
import com.Lino.grid_manager_back.season.repository.SeasonRepository;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

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

    @Transactional(readOnly = true)
    public PilotResponse findById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<PilotResponse> findAll(Pageable pageable) {
        Page<PilotResponse> page = pilotRepository.findAll(pageable).map(mapper::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public PilotResponse update(Long id, UpdatePilotRequest request) {
        Pilot pilot = findEntity(id);
        Category category = request.categoryId() == null ? pilot.getCategory() : categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria n\u00e3o encontrada."));
        String normalizedName = null;
        if (request.name() != null) {
            normalizedName = namePolicy.normalizeAndValidate(request.name());
            if (pilotRepository.existsByNameIgnoreCase(normalizedName)
                    && !pilot.getName().equalsIgnoreCase(normalizedName)) {
                throw new DuplicateResourceException("Nome de piloto j\u00e1 cadastrado.");
            }
        }
        if (!matchesLicensePattern(category, pilot.getLicense().getLicenseNumber(), pilot.getPilotNumber())) {
            throw new IllegalArgumentException("A licen\u00e7a atual n\u00e3o segue o padr\u00e3o da nova categoria.");
        }
        Set<Season> seasons = request.seasonIds() == null ? pilot.getSeasons()
                : Set.copyOf(seasonRepository.findAllById(request.seasonIds()));
        if (request.seasonIds() != null && seasons.size() != request.seasonIds().size()) {
            throw new ResourceNotFoundException("Uma ou mais temporadas n\u00e3o foram encontradas.");
        }
        if (seasons.stream().anyMatch(season -> !season.getCategory().getId().equals(category.getId()))) {
            throw new IllegalArgumentException("Temporadas devem pertencer \u00e0 categoria do piloto.");
        }
        mapper.update(request, pilot);
        if (normalizedName != null) {
            pilot.setName(normalizedName);
        }
        pilot.setCategory(category);
        pilot.setSeasons(seasons);
        return mapper.toResponse(pilot);
    }

    @Transactional
    public void delete(Long id) {
        pilotRepository.delete(findEntity(id));
    }

    private boolean matchesLicensePattern(Category category, String licenseNumber, Long pilotNumber) {
        if ("CATEGORIA-DATA-PILOTO".equals(category.getLicensePattern())) {
            String defaultPattern = "^" + Pattern.quote(category.getAcronym()) + "-\\d{8}-" + pilotNumber + "$";
            return Pattern.matches(defaultPattern, licenseNumber);
        }
        return Pattern.matches(category.getLicensePattern(), licenseNumber);
    }

    private Pilot findEntity(Long id) {
        return pilotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Piloto n\u00e3o encontrado."));
    }
}
