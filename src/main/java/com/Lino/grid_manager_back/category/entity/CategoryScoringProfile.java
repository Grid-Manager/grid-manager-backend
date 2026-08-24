package com.Lino.grid_manager_back.category.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.Lino.grid_manager_back.domain.enums.RaceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_category_scoring_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryScoringProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "race_type", nullable = false, length = 20)
    private RaceType raceType;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Builder.Default
    @Column(name = "fastest_lap_bonus", nullable = false)
    private Integer fastestLapBonus = 0;

    @Builder.Default
    @Column(name = "fastest_lap_requires_top_ten", nullable = false)
    private boolean fastestLapRequiresTopTen = false;

    @Builder.Default
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryScoringRule> positionRules = new HashSet<>();
}
