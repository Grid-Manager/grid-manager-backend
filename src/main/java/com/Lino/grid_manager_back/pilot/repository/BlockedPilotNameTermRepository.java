package com.Lino.grid_manager_back.pilot.repository;

import com.Lino.grid_manager_back.pilot.entity.BlockedPilotNameTerm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedPilotNameTermRepository extends JpaRepository<BlockedPilotNameTerm, Long> {
  List<BlockedPilotNameTerm> findAllByOrderByNormalizedTermAsc();
}
