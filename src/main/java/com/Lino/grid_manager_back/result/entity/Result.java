package com.Lino.grid_manager_back.result.entity;

import com.Lino.grid_manager_back.domain.enums.RaceStatusPilot;
import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.race.entity.Race;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_race_results")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Result {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long position;
    @Builder.Default @Column(nullable = false) private Integer points = 0;
    @Enumerated(EnumType.STRING) @Column(name = "race_status_pilot", nullable = false, length = 20) private RaceStatusPilot raceStatusPilot;
    @Builder.Default @Column(nullable = false) private Integer penalties = 0;
    @Builder.Default @Column(name = "yellow_flags", nullable = false) private Integer yellowFlags = 0;
    @Builder.Default @Column(name = "red_flags", nullable = false) private Integer redFlags = 0;
    @Builder.Default @Column(name = "flag_count", nullable = false) private Integer flagCount = 0;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "pilot_id", nullable = false) private Pilot pilot;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "race_id", nullable = false) private Race race;
}
