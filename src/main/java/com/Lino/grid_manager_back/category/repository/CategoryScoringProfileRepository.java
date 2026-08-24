package com.Lino.grid_manager_back.category.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Lino.grid_manager_back.category.entity.CategoryScoringProfile;
import com.Lino.grid_manager_back.domain.enums.RaceType;

public interface CategoryScoringProfileRepository extends JpaRepository<CategoryScoringProfile, Long> {
    Optional<CategoryScoringProfile> findFirstByCategoryIdAndRaceTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            Long categoryId, RaceType raceType, LocalDate effectiveFrom);
}
