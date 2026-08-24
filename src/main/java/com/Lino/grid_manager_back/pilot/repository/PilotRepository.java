package com.Lino.grid_manager_back.pilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Lino.grid_manager_back.pilot.entity.Pilot;

public interface PilotRepository extends JpaRepository<Pilot, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByPilotNumber(Long pilotNumber);
}
