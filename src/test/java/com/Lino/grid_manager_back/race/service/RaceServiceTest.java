package com.Lino.grid_manager_back.race.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.Lino.grid_manager_back.category.entity.Category;
import com.Lino.grid_manager_back.domain.enums.RaceStatus;
import com.Lino.grid_manager_back.domain.enums.RaceType;
import com.Lino.grid_manager_back.race.dto.UpdateRaceRequest;
import com.Lino.grid_manager_back.race.entity.Race;
import com.Lino.grid_manager_back.race.mapper.RaceMapper;
import com.Lino.grid_manager_back.race.repository.RaceRepository;
import com.Lino.grid_manager_back.season.entity.Season;
import com.Lino.grid_manager_back.season.repository.SeasonRepository;

@ExtendWith(MockitoExtension.class)
class RaceServiceTest {

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private RaceMapper mapper;

    @Test
    void rejectsSafetyCarLapsWhenSafetyCarIsDisabled() {
        RaceService service = new RaceService(raceRepository, seasonRepository, mapper);
        Race race = Race.builder()
                .id(1L)
                .season(Season.builder()
                        .category(Category.builder().id(1L).build())
                        .createdAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                        .build())
                .realizationDate(LocalDate.of(2026, 2, 1))
                .startTime(LocalDateTime.of(2026, 2, 1, 14, 0))
                .raceStatus(RaceStatus.SCHEDULED)
                .raceType(RaceType.STANDARD)
                .hasSafetyCar(false)
                .safetyCarLaps(0)
                .build();
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));

        UpdateRaceRequest request = new UpdateRaceRequest(null, null, null, null, null,
                null, false, 1);

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));

        verifyNoInteractions(mapper);
    }
}
