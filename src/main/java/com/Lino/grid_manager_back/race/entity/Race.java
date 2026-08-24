package com.Lino.grid_manager_back.race.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.Lino.grid_manager_back.climate.entity.Climate;
import com.Lino.grid_manager_back.domain.enums.RaceStatus;
import com.Lino.grid_manager_back.domain.enums.RaceType;
import com.Lino.grid_manager_back.pilot.entity.Pilot;
import com.Lino.grid_manager_back.result.entity.Result;
import com.Lino.grid_manager_back.season.entity.Season;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_race")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Race {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String name;
    @Column(name = "realization_date", nullable = false) private LocalDate realizationDate;
    @Column(name = "start_time", nullable = false) private LocalDateTime startTime;
    @Column(nullable = false) private Integer laps;
    @Column(name = "circuit_length", nullable = false) private Double circuitLength;
    @Column(name = "fast_lap", length = 20) private String fastLap;
    @Builder.Default @Column(name = "has_safety_car", nullable = false) private boolean hasSafetyCar = false;
    @Builder.Default @Column(name = "safety_car_laps", nullable = false) private Integer safetyCarLaps = 0;
    @Enumerated(EnumType.STRING) @Column(name = "race_status", nullable = false, length = 20) private RaceStatus raceStatus;
    @Enumerated(EnumType.STRING) @Column(name = "race_type", nullable = false, length = 20) private RaceType raceType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "season_id", nullable = false) private Season season;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pilot_faster_lap_id") private Pilot pilotFasterLap;
    @OneToOne(mappedBy = "race", fetch = FetchType.LAZY) private Climate climate;
    @Builder.Default @OneToMany(mappedBy = "race", fetch = FetchType.LAZY) private Set<Result> results = new HashSet<>();
}
