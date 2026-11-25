package com.Lino.grid_manager_back.race.repository;

import com.Lino.grid_manager_back.race.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceRepository extends JpaRepository<Race, Long> {
  boolean existsBySeasonId(Long seasonId);
}
