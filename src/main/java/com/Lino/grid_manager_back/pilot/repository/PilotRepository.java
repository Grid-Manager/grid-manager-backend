package com.Lino.grid_manager_back.pilot.repository;

import com.Lino.grid_manager_back.pilot.entity.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilotRepository extends JpaRepository<Pilot, Long> {
  boolean existsByNameIgnoreCase(String name);

  boolean existsByPilotNumber(Long pilotNumber);
}
