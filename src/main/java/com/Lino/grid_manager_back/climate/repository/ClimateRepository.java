package com.Lino.grid_manager_back.climate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Lino.grid_manager_back.climate.entity.Climate;

public interface ClimateRepository extends JpaRepository<Climate, Long> {
    boolean existsByRaceId(Long raceId);
}
