package com.Lino.grid_manager_back.race.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.Lino.grid_manager_back.domain.enums.RaceStatus;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.race.dto.CreateRaceRequest;
import com.Lino.grid_manager_back.race.dto.RaceResponse;
import com.Lino.grid_manager_back.race.dto.UpdateRaceRequest;
import com.Lino.grid_manager_back.race.entity.Race;
import com.Lino.grid_manager_back.race.mapper.RaceMapper;
import com.Lino.grid_manager_back.race.repository.RaceRepository;
import com.Lino.grid_manager_back.season.entity.Season;
import com.Lino.grid_manager_back.season.repository.SeasonRepository;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

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

    @Transactional
    public RaceResponse start(Long id) {
        Race race = findEntity(id);
        if (race.getRaceStatus() != RaceStatus.SCHEDULED) {
            throw new IllegalArgumentException("A corrida s\u00f3 pode ser iniciada quando estiver agendada.");
        }
        race.setRaceStatus(RaceStatus.IN_PROGRESS);
        return mapper.toResponse(race);
    }

    @Transactional
    public RaceResponse finish(Long id) {
        Race race = findEntity(id);
        if (race.getRaceStatus() != RaceStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("A corrida s\u00f3 pode ser finalizada quando estiver em andamento.");
        }
        race.setRaceStatus(RaceStatus.FINISHED);
        return mapper.toResponse(race);
    }

    @Transactional(readOnly = true)
    public PagedResponse<RaceResponse> findAll(Pageable pageable) {
        Page<RaceResponse> page = raceRepository.findAll(pageable).map(mapper::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public RaceResponse update(Long id, UpdateRaceRequest request) {
        Race race = findEntity(id);
        if (request.name() != null && request.name().isBlank()) {
            throw new IllegalArgumentException("Nome da corrida não pode ser vazio.");
        }
        if (race.getRaceStatus() == RaceStatus.FINISHED) {
            throw new IllegalArgumentException("Uma corrida finalizada não pode ser alterada.");
        }

        java.time.LocalDate realizationDate = request.realizationDate() == null
                ? race.getRealizationDate() : request.realizationDate();
        java.time.LocalDateTime startTime = request.startTime() == null ? race.getStartTime() : request.startTime();
        if (realizationDate.isBefore(race.getSeason().getCreatedAt().toLocalDate())) {
            throw new IllegalArgumentException("A data da corrida não pode ser anterior à criação da temporada.");
        }
        if (!startTime.toLocalDate().equals(realizationDate)) {
            throw new IllegalArgumentException("O horário de início deve pertencer à data da corrida.");
        }
        boolean hasSafetyCar = request.hasSafetyCar() == null ? race.isHasSafetyCar() : request.hasSafetyCar();
        int safetyCarLaps = request.safetyCarLaps() == null ? race.getSafetyCarLaps() : request.safetyCarLaps();
        if (!hasSafetyCar && safetyCarLaps > 0) {
            throw new IllegalArgumentException("Voltas de Safety Car exigem o controle de Safety Car ativo.");
        }

        mapper.update(request, race);
        if (request.name() != null) {
            race.setName(request.name().trim());
        }
        if (!hasSafetyCar) {
            race.setSafetyCarLaps(0);
        }
        return mapper.toResponse(race);
    }

    @Transactional
    public void delete(Long id) {
        raceRepository.delete(findEntity(id));
    }

    private Race findEntity(Long id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corrida não encontrada."));
    }
}
