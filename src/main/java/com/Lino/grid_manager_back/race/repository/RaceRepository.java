package com.Lino.grid_manager_back.race.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Lino.grid_manager_back.domain.enums.RaceStatus;
import com.Lino.grid_manager_back.race.entity.Race;

public interface RaceRepository extends JpaRepository<Race, Long> {
    boolean existsBySeasonId(Long seasonId);

    boolean existsBySeasonIdAndRaceStatusNot(Long seasonId, RaceStatus raceStatus);
}
