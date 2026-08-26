package com.Lino.grid_manager_back.result.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.category.repository.CategoryScoringProfileRepository;
import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
import com.Lino.grid_manager_back.domain.enums.RaceType;
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
import com.Lino.grid_manager_back.season.entity.Season;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private PilotRepository pilotRepository;

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private CategoryScoringProfileRepository scoringProfileRepository;

    @Mock
    private ResultMapper mapper;

    @Test
    void assignsZeroPointsToDisqualifiedPilot() {
        ResultService service = new ResultService(resultRepository, pilotRepository, raceRepository,
                scoringProfileRepository, mapper);
        Category category = Category.builder().id(3L).build();
        Pilot pilot = Pilot.builder().id(9L).category(category).build();
        Season season = Season.builder().category(category).pilots(Set.of(pilot)).build();
        Race race = Race.builder()
                .id(5L)
                .season(season)
                .raceType(RaceType.STANDARD)
                .build();
        Result result = Result.builder().position(4L).raceStatusPilot(RaceStatusPilot.DSQ).build();
        ResultResponse response = new ResultResponse(1L, 4L, 0, RaceStatusPilot.DSQ,
                0, 0, 0, 0, 9L, 5L);

        when(raceRepository.findById(5L)).thenReturn(Optional.of(race));
        when(pilotRepository.findById(9L)).thenReturn(Optional.of(pilot));
        when(resultRepository.findByRaceIdAndPilotId(5L, 9L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any(CreateResultRequest.class))).thenReturn(result);
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Result.class))).thenReturn(response);

        ResultResponse actual = service.create(new CreateResultRequest(4L, RaceStatusPilot.DSQ,
                null, null, null, null, 9L, 5L));

        ArgumentCaptor<Result> savedResult = ArgumentCaptor.forClass(Result.class);
        org.mockito.Mockito.verify(resultRepository).save(savedResult.capture());
        assertEquals(0, savedResult.getValue().getPoints());
        assertEquals(response, actual);
    }

    @Test
    void clearsFastestLapWhenItsPilotIsNoLongerFinished() {
        ResultService service = new ResultService(resultRepository, pilotRepository, raceRepository,
                scoringProfileRepository, mapper);
        Category category = Category.builder().id(3L).build();
        Pilot pilot = Pilot.builder().id(9L).category(category).build();
        Race race = Race.builder()
                .id(5L)
                .season(Season.builder().category(category).build())
                .pilotFasterLap(pilot)
                .fastLap("1:18.234")
                .raceType(RaceType.STANDARD)
                .build();
        Result result = Result.builder()
                .id(1L)
                .position(4L)
                .points(12)
                .pilot(pilot)
                .race(race)
                .raceStatusPilot(RaceStatusPilot.FINISHED)
                .build();
        ResultResponse response = new ResultResponse(1L, 4L, 0, RaceStatusPilot.DNF,
                0, 0, 0, 0, 9L, 5L);
        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));
        doAnswer(invocation -> {
            invocation.<Result>getArgument(1).setRaceStatusPilot(RaceStatusPilot.DNF);
            return null;
        }).when(mapper).update(any(UpdateResultRequest.class), any(Result.class));
        when(mapper.toResponse(any(Result.class))).thenReturn(response);

        ResultResponse actual = service.update(1L, new UpdateResultRequest(
                null, RaceStatusPilot.DNF, null, null, null, null));

        assertEquals(0, result.getPoints());
        assertEquals(null, race.getPilotFasterLap());
        assertEquals(null, race.getFastLap());
        assertEquals(response, actual);
    }
}
