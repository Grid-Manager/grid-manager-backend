package com.Lino.grid_manager_back.category.repository;

import com.Lino.grid_manager_back.category.entity.CategoryScoringProfile;
import com.Lino.grid_manager_back.domain.enums.RaceType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryScoringProfileRepository
    extends JpaRepository<CategoryScoringProfile, Long> {
  boolean existsByCategoryIdAndSeasonIdAndRaceTypeAndEffectiveFrom(
      Long categoryId, Long seasonId, RaceType raceType, LocalDate effectiveFrom);

  Optional<CategoryScoringProfile>
      findFirstByCategoryIdAndSeasonIdIsNullAndRaceTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
          Long categoryId, RaceType raceType, LocalDate effectiveFrom);

  Optional<CategoryScoringProfile>
      findFirstByCategoryIdAndSeasonIdAndRaceTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
          Long categoryId, Long seasonId, RaceType raceType, LocalDate effectiveFrom);
}
