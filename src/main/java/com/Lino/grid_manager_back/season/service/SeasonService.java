package com.Lino.grid_manager_back.season.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.category.repository.CategoryRepository;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.pilot.repository.PilotRepository;
import com.Lino.grid_manager_back.season.dto.CreateSeasonRequest;
import com.Lino.grid_manager_back.season.dto.SeasonResponse;
import com.Lino.grid_manager_back.season.dto.UpdateSeasonRequest;
import com.Lino.grid_manager_back.season.entity.Season;
import com.Lino.grid_manager_back.season.mapper.SeasonMapper;
import com.Lino.grid_manager_back.season.repository.SeasonRepository;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

@Service
public class SeasonService {
    private final SeasonRepository seasonRepository;
    private final CategoryRepository categoryRepository;
    private final PilotRepository pilotRepository;
    private final SeasonMapper mapper;

    public SeasonService(SeasonRepository seasonRepository, CategoryRepository categoryRepository,
            PilotRepository pilotRepository, SeasonMapper mapper) {
        this.seasonRepository = seasonRepository;
        this.categoryRepository = categoryRepository;
        this.pilotRepository = pilotRepository;
        this.mapper = mapper;
    }

    @Transactional
    public SeasonResponse create(CreateSeasonRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria n\u00e3o encontrada."));
        if (request.year() < category.getFoundingYear()) {
            throw new IllegalArgumentException("O ano da temporada n\u00e3o pode ser anterior ao da categoria.");
        }

        Season season = mapper.toEntity(request);
        season.setCategory(category);
        season.setCreatedAt(LocalDateTime.now());
        season.setWinnerPilot(null);
        season.setPilots(resolvePilots(request.pilotIds(), category.getId()));
        return mapper.toResponse(seasonRepository.save(season));
    }

    @Transactional(readOnly = true)
    public SeasonResponse findById(Long id) {
        return mapper.toResponse(seasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temporada n\u00e3o encontrada.")));
    }

    @Transactional(readOnly = true)
    public PagedResponse<SeasonResponse> findAll(Pageable pageable) {
        Page<SeasonResponse> page = seasonRepository.findAll(pageable).map(mapper::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public SeasonResponse update(Long id, UpdateSeasonRequest request) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temporada não encontrada."));
        if (request.name() != null && request.name().isBlank()) {
            throw new IllegalArgumentException("Nome da temporada não pode ser vazio.");
        }
        if (request.year() != null && request.year() < season.getCategory().getFoundingYear()) {
            throw new IllegalArgumentException("O ano da temporada não pode ser anterior ao da categoria.");
        }
        mapper.update(request, season);
        if (request.name() != null) {
            season.setName(request.name().trim());
        }
        if (request.pilotIds() != null) {
            season.setPilots(resolvePilots(request.pilotIds(), season.getCategory().getId()));
        }
        return mapper.toResponse(season);
    }

    @Transactional
    public void delete(Long id) {
        seasonRepository.delete(seasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temporada não encontrada.")));
    }

    private Set<Pilot> resolvePilots(Set<Long> pilotIds, Long categoryId) {
        if (pilotIds == null || pilotIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(pilotIds));
        if (pilots.size() != pilotIds.size()) {
            throw new ResourceNotFoundException("Um ou mais pilotos n\u00e3o foram encontrados.");
        }
        if (pilots.stream().anyMatch(pilot -> !pilot.getCategory().getId().equals(categoryId))) {
            throw new IllegalArgumentException("Todos os pilotos da temporada devem pertencer \u00e0 categoria.");
        }
        return pilots;
    }
}
