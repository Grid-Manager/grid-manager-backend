package com.Lino.grid_manager_back.climate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.climate.dto.ClimateResponse;
import com.Lino.grid_manager_back.climate.dto.CreateClimateRequest;
import com.Lino.grid_manager_back.climate.dto.UpdateClimateRequest;
import com.Lino.grid_manager_back.climate.entity.Climate;
import com.Lino.grid_manager_back.climate.mapper.ClimateMapper;
import com.Lino.grid_manager_back.climate.repository.ClimateRepository;
import com.Lino.grid_manager_back.infrastructure.exception.DuplicateResourceException;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.race.entity.Race;
import com.Lino.grid_manager_back.race.repository.RaceRepository;

@Service
public class ClimateService {
    private final ClimateRepository climateRepository;
    private final RaceRepository raceRepository;
    private final ClimateMapper mapper;

    public ClimateService(ClimateRepository climateRepository, RaceRepository raceRepository, ClimateMapper mapper) {
        this.climateRepository = climateRepository;
        this.raceRepository = raceRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ClimateResponse create(CreateClimateRequest request) {
        Race race = raceRepository.findById(request.raceId())
                .orElseThrow(() -> new ResourceNotFoundException("Corrida n\u00e3o encontrada."));
        if (climateRepository.existsByRaceId(race.getId())) {
            throw new DuplicateResourceException("J\u00e1 existe um registro clim\u00e1tico para esta corrida.");
        }
        Climate climate = mapper.toEntity(request);
        climate.setRace(race);
        return mapper.toResponse(climateRepository.save(climate));
    }

    @Transactional(readOnly = true)
    public ClimateResponse findById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Transactional
    public ClimateResponse update(Long id, UpdateClimateRequest request) {
        Climate climate = findEntity(id);
        mapper.update(request, climate);
        return mapper.toResponse(climate);
    }

    @Transactional
    public void delete(Long id) {
        climateRepository.delete(findEntity(id));
    }

    private Climate findEntity(Long id) {
        return climateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clima n\u00e3o encontrado."));
    }
}
