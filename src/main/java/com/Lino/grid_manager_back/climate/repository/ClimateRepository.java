package com.Lino.grid_manager_back.climate.repository;

import com.Lino.grid_manager_back.climate.entity.Climate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClimateRepository extends JpaRepository<Climate, Long> {
  boolean existsByRaceId(Long raceId);
}
