package com.Lino.grid_manager_back.result.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
import com.Lino.grid_manager_back.result.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {
    boolean existsByRaceIdAndPositionAndRaceStatusPilot(
            Long raceId, Long position, RaceStatusPilot raceStatusPilot);

    boolean existsByRaceIdAndPositionAndRaceStatusPilotAndIdNot(
            Long raceId, Long position, RaceStatusPilot raceStatusPilot, Long id);

    Optional<Result> findByRaceIdAndPilotId(Long raceId, Long pilotId);

    @Query("""
            select result.pilot.id as pilotId, sum(result.points) as points
            from Result result
            where result.race.season.id = :seasonId
            group by result.pilot.id
            """)
    List<SeasonPilotScore> sumPointsBySeasonId(Long seasonId);

    interface SeasonPilotScore {
        Long getPilotId();

        Long getPoints();
    }
}
