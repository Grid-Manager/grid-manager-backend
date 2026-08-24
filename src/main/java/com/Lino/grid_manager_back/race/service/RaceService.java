package com.Lino.grid_manager_back.race.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.domain.enums.RaceStatus;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.race.dto.CreateRaceRequest;
import com.Lino.grid_manager_back.race.dto.RaceResponse;
import com.Lino.grid_manager_back.race.entity.Race;
import com.Lino.grid_manager_back.race.mapper.RaceMapper;
import com.Lino.grid_manager_back.race.repository.RaceRepository;
import com.Lino.grid_manager_back.season.entity.Season;
import com.Lino.grid_manager_back.season.repository.SeasonRepository;

@Service
public class RaceService {
    private final RaceRepository raceRepository;
    private final SeasonRepository seasonRepository;
    private final RaceMapper mapper;

    public RaceService(RaceRepository raceRepository, SeasonRepository seasonRepository, RaceMapper mapper) {
        this.raceRepository = raceRepository;
        this.seasonRepository = seasonRepository;
        this.mapper = mapper;
    }

    @Transactional
    public RaceResponse create(CreateRaceRequest request) {
        Season season = seasonRepository.findById(request.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Temporada n\u00e3o encontrada."));
        if (request.realizationDate().isBefore(season.getCreatedAt().toLocalDate())) {
            throw new IllegalArgumentException("A data da corrida n\u00e3o pode ser anterior \u00e0 cria\u00e7\u00e3o da temporada.");
        }
        if (!request.startTime().toLocalDate().equals(request.realizationDate())) {
            throw new IllegalArgumentException("O hor\u00e1rio de in\u00edcio deve pertencer \u00e0 data da corrida.");
        }
        Race race = mapper.toEntity(request);
        race.setSeason(season);
        race.setRaceStatus(RaceStatus.SCHEDULED);
        race.setHasSafetyCar(false);
        race.setSafetyCarLaps(0);
        return mapper.toResponse(raceRepository.save(race));
    }

    @Transactional(readOnly = true)
    public RaceResponse findById(Long id) {
        return mapper.toResponse(raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corrida n\u00e3o encontrada.")));
    }
}
