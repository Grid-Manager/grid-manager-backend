package com.Lino.grid_manager_back.result.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Lino.grid_manager_back.category.entity.CategoryScoringProfile;
import com.Lino.grid_manager_back.category.entity.CategoryScoringRule;
import com.Lino.grid_manager_back.category.repository.CategoryScoringProfileRepository;
import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
import com.Lino.grid_manager_back.infrastructure.exception.DuplicateResourceException;
import com.Lino.grid_manager_back.infrastructure.exception.ResourceNotFoundException;
import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.pilot.repository.PilotRepository;
import com.Lino.grid_manager_back.race.entity.Race;
import com.Lino.grid_manager_back.race.repository.RaceRepository;
import com.Lino.grid_manager_back.result.dto.CreateResultRequest;
import com.Lino.grid_manager_back.result.dto.ResultResponse;
import com.Lino.grid_manager_back.result.dto.UpdateResultRequest;
import com.Lino.grid_manager_back.result.entity.Result;
import com.Lino.grid_manager_back.result.mapper.ResultMapper;
import com.Lino.grid_manager_back.result.repository.ResultRepository;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

@Service
public class ResultService {
    private final ResultRepository resultRepository;
    private final PilotRepository pilotRepository;
    private final RaceRepository raceRepository;
    private final CategoryScoringProfileRepository scoringProfileRepository;
    private final ResultMapper mapper;

    public ResultService(ResultRepository resultRepository, PilotRepository pilotRepository,
            RaceRepository raceRepository, CategoryScoringProfileRepository scoringProfileRepository,
            ResultMapper mapper) {
        this.resultRepository = resultRepository;
        this.pilotRepository = pilotRepository;
        this.raceRepository = raceRepository;
        this.scoringProfileRepository = scoringProfileRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ResultResponse create(CreateResultRequest request) {
        Race race = raceRepository.findById(request.raceId())
                .orElseThrow(() -> new ResourceNotFoundException("Corrida n\u00e3o encontrada."));
        if (race.getRaceStatus() == com.Lino.grid_manager_back.domain.enums.RaceStatus.SCHEDULED) {
            throw new IllegalArgumentException("Resultados s\u00f3 podem ser registrados durante ou ap\u00f3s a corrida.");
        }
        Pilot pilot = pilotRepository.findById(request.pilotId())
                .orElseThrow(() -> new ResourceNotFoundException("Piloto n\u00e3o encontrado."));
        validatePilotBelongsToRaceCategory(pilot, race);
        validatePilotIsRegisteredInSeason(pilot, race);
        if (resultRepository.findByRaceIdAndPilotId(race.getId(), pilot.getId()).isPresent()) {
            throw new DuplicateResourceException("J\u00e1 existe resultado para este piloto na corrida.");
        }
        if (request.raceStatusPilot() == RaceStatusPilot.FINISHED
                && resultRepository.existsByRaceIdAndPositionAndRaceStatusPilot(
                        race.getId(), request.position(), RaceStatusPilot.FINISHED)) {
            throw new DuplicateResourceException("A posi\u00e7\u00e3o final j\u00e1 est\u00e1 ocupada nesta corrida.");
        }

        Result result = mapper.toEntity(request);
        result.setRace(race);
        result.setPilot(pilot);
        result.setPenalties(zeroIfNull(request.penalties()));
        result.setYellowFlags(zeroIfNull(request.yellowFlags()));
        result.setRedFlags(zeroIfNull(request.redFlags()));
        result.setFlagCount(zeroIfNull(request.flagCount()));
        result.setPoints(calculatePoints(race, result));
        return mapper.toResponse(resultRepository.save(result));
    }

    @Transactional
    public ResultResponse registerFastestLap(Long raceId, Long pilotId, String fastLap) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Corrida n\u00e3o encontrada."));
        Result result = resultRepository.findByRaceIdAndPilotId(raceId, pilotId)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado do piloto n\u00e3o encontrado."));
        if (result.getRaceStatusPilot() != RaceStatusPilot.FINISHED) {
            throw new IllegalArgumentException("A volta mais r\u00e1pida requer um piloto com resultado finalizado.");
        }
        if (race.getRaceStatus() == com.Lino.grid_manager_back.domain.enums.RaceStatus.SCHEDULED) {
            throw new IllegalArgumentException("A volta mais r\u00e1pida s\u00f3 pode ser registrada durante ou ap\u00f3s a corrida.");
        }
        if (fastLap == null || fastLap.isBlank()) {
            throw new IllegalArgumentException("O tempo da volta mais r\u00e1pida \u00e9 obrigat\u00f3rio.");
        }
        if (race.getPilotFasterLap() != null && !race.getPilotFasterLap().getId().equals(pilotId)) {
            Result previous = resultRepository.findByRaceIdAndPilotId(raceId, race.getPilotFasterLap().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resultado da volta mais r\u00e1pida n\u00e3o encontrado."));
            race.setPilotFasterLap(null);
            previous.setPoints(calculatePoints(race, previous));
        }
        race.setPilotFasterLap(result.getPilot());
        race.setFastLap(fastLap.trim());
        result.setPoints(calculatePoints(race, result));
        return mapper.toResponse(result);
    }

    @Transactional(readOnly = true)
    public ResultResponse findById(Long id) {
        return mapper.toResponse(resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado n\u00e3o encontrado.")));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ResultResponse> findAll(Pageable pageable) {
        Page<ResultResponse> page = resultRepository.findAll(pageable).map(mapper::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public ResultResponse update(Long id, UpdateResultRequest request) {
        Result result = findEntity(id);
        RaceStatusPilot status = request.raceStatusPilot() == null
                ? result.getRaceStatusPilot() : request.raceStatusPilot();
        Long position = request.position() == null ? result.getPosition() : request.position();
        if (status == RaceStatusPilot.FINISHED
                && resultRepository.existsByRaceIdAndPositionAndRaceStatusPilotAndIdNot(
                        result.getRace().getId(), position, RaceStatusPilot.FINISHED, id)) {
            throw new DuplicateResourceException("A posição final já está ocupada nesta corrida.");
        }

        mapper.update(request, result);
        if (status != RaceStatusPilot.FINISHED && isFastestLapPilot(result)) {
            clearFastestLap(result.getRace());
        }
        result.setPoints(calculatePoints(result.getRace(), result));
        return mapper.toResponse(result);
    }

    @Transactional
    public void delete(Long id) {
        Result result = findEntity(id);
        if (isFastestLapPilot(result)) {
            clearFastestLap(result.getRace());
        }
        resultRepository.delete(result);
    }

    private int calculatePoints(Race race, Result result) {
        if (result.getRaceStatusPilot() != RaceStatusPilot.FINISHED) {
            return 0;
        }
        Optional<CategoryScoringProfile> profile = scoringProfileRepository
                .findFirstByCategoryIdAndRaceTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                        race.getSeason().getCategory().getId(), race.getRaceType(), race.getRealizationDate());
        if (profile.isEmpty()) {
            throw new IllegalStateException("N\u00e3o h\u00e1 perfil de pontua\u00e7\u00e3o vigente para a categoria e tipo de corrida.");
        }
        CategoryScoringProfile scoringProfile = profile.get();
        int points = scoringProfile.getPositionRules().stream()
                .filter(rule -> rule.getFinishingPosition().longValue() == result.getPosition())
                .map(CategoryScoringRule::getPoints)
                .findFirst()
                .orElse(0);
        boolean isFastestLapPilot = race.getPilotFasterLap() != null
                && race.getPilotFasterLap().getId().equals(result.getPilot().getId());
        boolean receivesBonus = !scoringProfile.isFastestLapRequiresTopTen() || result.getPosition() <= 10;
        return isFastestLapPilot && receivesBonus ? points + scoringProfile.getFastestLapBonus() : points;
    }

    private void validatePilotBelongsToRaceCategory(Pilot pilot, Race race) {
        if (!pilot.getCategory().getId().equals(race.getSeason().getCategory().getId())) {
            throw new IllegalArgumentException("O piloto deve pertencer \u00e0 categoria da corrida.");
        }
    }

    private void validatePilotIsRegisteredInSeason(Pilot pilot, Race race) {
        boolean isRegistered = race.getSeason().getPilots().stream()
                .anyMatch(seasonPilot -> seasonPilot.getId().equals(pilot.getId()));
        if (!isRegistered) {
            throw new IllegalArgumentException("O piloto deve estar inscrito na temporada da corrida.");
        }
    }

    private Result findEntity(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado não encontrado."));
    }

    private boolean isFastestLapPilot(Result result) {
        Pilot pilot = result.getRace().getPilotFasterLap();
        return pilot != null && pilot.getId().equals(result.getPilot().getId());
    }

    private void clearFastestLap(Race race) {
        race.setPilotFasterLap(null);
        race.setFastLap(null);
    }

    private int zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }
}
