package com.Lino.grid_manager_back.result.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
import com.Lino.grid_manager_back.result.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {
    boolean existsByRaceIdAndPositionAndRaceStatusPilot(
            Long raceId, Long position, RaceStatusPilot raceStatusPilot);

    Optional<Result> findByRaceIdAndPilotId(Long raceId, Long pilotId);
}
