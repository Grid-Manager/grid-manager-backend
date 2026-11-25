package com.Lino.grid_manager_back.result.repository;

import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
import com.Lino.grid_manager_back.result.entity.Result;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
  boolean existsByRaceIdAndPositionAndRaceStatusPilot(
      Long raceId, Long position, RaceStatusPilot raceStatusPilot);

  boolean existsByRaceIdAndPositionAndRaceStatusPilotAndIdNot(
      Long raceId, Long position, RaceStatusPilot raceStatusPilot, Long id);

  Optional<Result> findByRaceIdAndPilotId(Long raceId, Long pilotId);
}
