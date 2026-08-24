package com.Lino.grid_manager_back.result.service;

import java.util.Optional;
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
import com.Lino.grid_manager_back.result.entity.Result;
import com.Lino.grid_manager_back.result.mapper.ResultMapper;
import com.Lino.grid_manager_back.result.repository.ResultRepository;

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
        Pilot pilot = pilotRepository.findById(request.pilotId())
                .orElseThrow(() -> new ResourceNotFoundException("Piloto n\u00e3o encontrado."));
        validatePilotBelongsToRaceCategory(pilot, race);
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
        if (race.getPilotFasterLap() != null && !race.getPilotFasterLap().getId().equals(pilotId)) {
            Result previous = resultRepository.findByRaceIdAndPilotId(raceId, race.getPilotFasterLap().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resultado da volta mais r\u00e1pida n\u00e3o encontrado."));
            race.setPilotFasterLap(null);
            previous.setPoints(calculatePoints(race, previous));
        }
        race.setPilotFasterLap(result.getPilot());
        race.setFastLap(fastLap);
        result.setPoints(calculatePoints(race, result));
        return mapper.toResponse(result);
    }

    @Transactional(readOnly = true)
    public ResultResponse findById(Long id) {
        return mapper.toResponse(resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado n\u00e3o encontrado.")));
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

    private int zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }
}
